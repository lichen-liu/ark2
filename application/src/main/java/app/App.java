package app;

import java.net.InetSocketAddress;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.util.Set;

import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.Gateway;
import org.hyperledger.fabric.gateway.Identities;
import org.hyperledger.fabric.gateway.Network;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallets;
import org.hyperledger.fabric.gateway.X509Identity;
import org.hyperledger.fabric.protos.peer.Collection;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric_ca.sdk.HFCAClient;

import app.service.UserRegistrationService;
import app.service.AdminEnrollmentService;
import app.factory.CaClientFactory;
import app.factory.WalletFactory;

class App {
    public static final String userId = "appUser3";
    public static final String pemPath = "../blockchain/hlf2-network/organizations/peerOrganizations/org1.example.com/ca/ca.org1.example.com-cert.pem";
    public static final String url = "https://localhost:7054";
    public static final String mspId = "Org1MSP";
    public static final String affliation = "org1.department1";
    public static final String channel = "mychannel";
    public static final String contractName = "agreements";

    public static void main(String[] args) throws Exception {
        // Conf conf = new Conf();
        // System.out.println(conf);

        // AppServer server = new AppServer(conf.getAppServerSocketAddress());

        // HFCAClient client = CaClientFactory.CreateCaClient(caUrl, pemPath);

        App app = new App();
        app.invokePeer();
    }

    public App() {
    }

    public void invokePeer() {
        try {

            Wallet wallet = WalletFactory.GetWallet(mspId);
            HFCAClient client = CaClientFactory.CreateCaClient(url, pemPath);
            tryEnrollAdmin(wallet, client);
            tryRegisterUser(wallet, client);

        } catch (Exception e) {

            System.out.println("An error occurred when fetching wallet or client");
            System.err.println(e);

        }

        try (Gateway gateway = this.connect()) {

            Network network = gateway.getNetwork(channel);
            Contract contract = network.getContract(contractName);

            // transactions
            System.out.println("\n[0] result: "
                    + new String(contract.evaluateTransaction("getPointTransaction", "point_transaction_id_0")));

            System.out.println("\n[1] result: " + new String(contract.submitTransaction("initLedger")));

            System.out.println("\n[2] result: " + new String(contract.submitTransaction("publishNewPost", "future",
                    "I am smart", "user007", "signature(user007)")));

        } catch (Exception e) {

            e.printStackTrace(System.out);

        }
    }

    private Gateway connect() throws Exception {
        Wallet wallet = WalletFactory.GetWallet(mspId);
        Path networkConfigPath = Paths.get("..", "blockchain", "hlf2-network", "organizations", "peerOrganizations",
                "org1.example.com", "connection-org1.yaml");

        Gateway.Builder builder = Gateway.createBuilder();
        builder.identity(wallet, userId).networkConfig(networkConfigPath).discovery(true);
        return builder.connect();
    }

    private void tryEnrollAdmin(Wallet wallet, HFCAClient client) {
        try {
            AdminEnrollmentService enrollmentService = new AdminEnrollmentService();
            enrollmentService.EnrollAdmin(wallet, client, mspId);
        } catch (Exception e) {
            System.out.println("An error occurred when enrolling admin");
            System.err.println(e);
        }
    }

    private void tryRegisterUser(Wallet wallet, HFCAClient client) {
        // Collection identities = client.getHFCAIdentities(user);
        // for (var identity : identities) {
        // if(identity.)
        // }
        try {
            User user = createUser((X509Identity) wallet.get(WalletFactory.adminEntityName));
            UserRegistrationService registrationService = new UserRegistrationService();
            registrationService.RegisterUser(wallet, client, user, userId);
        } catch (Exception e) {
            System.out.println("An error occurred when registrating user");
            System.err.println(e);
        }
    }

    private User createUser(X509Identity identity) {

        return new User() {

            @Override
            public String getName() {
                return WalletFactory.adminEntityName;
            }

            @Override
            public Set<String> getRoles() {
                return null;
            }

            @Override
            public String getAccount() {
                return null;
            }

            @Override
            public String getAffiliation() {
                return affliation;
            }

            @Override
            public Enrollment getEnrollment() {
                return new Enrollment() {

                    @Override
                    public PrivateKey getKey() {
                        return identity.getPrivateKey();
                    }

                    @Override
                    public String getCert() {
                        return Identities.toPemString(identity.getCertificate());
                    }
                };
            }

            @Override
            public String getMspId() {
                return mspId;
            }

        };
    }
}

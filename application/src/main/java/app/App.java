package app;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.*;

import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.Gateway;
import org.hyperledger.fabric.gateway.Identities;
import org.hyperledger.fabric.gateway.Network;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.X509Identity;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric_ca.sdk.HFCAClient;

import app.factory.CaClientFactory;
import app.factory.WalletFactory;
import app.service.AdminEnrollmentService;
import app.service.UserRegistrationService;

class App {
    static {
        System.setProperty("org.hyperledger.fabric.sdk.service_discovery.as_localhost", "true");
    }

    public static void main(String[] args) throws Exception {
        // Conf conf = new Conf();
        // System.out.println(conf);

        // AppServer server = new AppServer(conf.getAppServerSocketAddress());
        App app = new App();
        app.invokePeer();
    }

    public App() {
    }

    public void invokePeer() {

        String dir = System.getProperty("user.dir");
        File file = new File("peer.yaml");
        ObjectMapper om = new ObjectMapper(new YAMLFactory());

        try {

            PeerInfo peer = om.readValue(file, PeerInfo.class);
            Wallet wallet = WalletFactory.GetWallet(peer.getMpsId());
            HFCAClient client = CaClientFactory.CreateCaClient(peer.getCaUrl(), peer.getPemPath());

            tryEnrollAdmin(wallet, client, peer);
            tryRegisterUser(wallet, client, peer);

            try (Gateway gateway = this.connect(wallet, peer.getUserId())) {

                Network network = gateway.getNetwork(peer.getChannel());
                Contract contract = network.getContract(peer.getContractName());

                CCTesting t = new CCTesting();
                t.test(contract);

            } catch (Exception e) {
                e.printStackTrace(System.out);
            }

        } catch (Exception e) {
            System.out.println("An error occurred when fetching wallet or client");
            System.err.println(e);
        }
    }

    private Gateway connect(Wallet wallet, String userId) throws Exception {

        Path networkConfigPath = Paths.get("..", "blockchain", "hlf2-network", "organizations", "peerOrganizations",
                "org1.example.com", "connection-org1.yaml");

        Gateway.Builder builder = Gateway.createBuilder();
        builder.identity(wallet, userId).networkConfig(networkConfigPath).discovery(true);
        return builder.connect();
    }

    private void tryEnrollAdmin(Wallet wallet, HFCAClient client, PeerInfo peer) {

        var entity = new AdminEnrollmentService.Entity();
        entity.adminName = peer.getAdminName();
        entity.adminSecret = peer.getAdminSecret();
        entity.hostName = peer.getHostName();
        entity.mspId = peer.getMpsId();
        entity.profile = peer.getProfile();

        try {
            var enrollmentService = new AdminEnrollmentService(wallet, client);
            enrollmentService.EnrollAdmin(entity);
        } catch (Exception e) {
            System.out.println("An error occurred when enrolling admin");
            System.err.println(e);
        }
    }

    private void tryRegisterUser(Wallet wallet, HFCAClient client, PeerInfo peer) throws Exception {

        User adminUser = createUser((X509Identity) wallet.get(peer.getAdminName()), peer);
        var identities = client.getHFCAIdentities(adminUser);
        for (var identity : identities) {
            // System.out.println(String.format("Existing enrollment ids are: %s",
            // identity.getEnrollmentId()));
            if (identity.getEnrollmentId().equals(peer.getUserId()))
                return;
        }

        try {
            var registrationService = new UserRegistrationService(wallet, client);
            registrationService.RegisterUser(adminUser, peer.getUserId());
        } catch (Exception e) {
            System.out.println("An error occurred when registrating user");
            System.err.println(e);
        }
    }

    private User createUser(X509Identity identity, PeerInfo peer) {

        return new User() {

            @Override
            public String getName() {
                return peer.getAdminName();
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
                return peer.getAffliation();
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
                return peer.getMpsId();
            }

        };
    }
}

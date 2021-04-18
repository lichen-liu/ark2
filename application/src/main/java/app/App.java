package app;

import java.io.File;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.Identities;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.X509Identity;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric_ca.sdk.HFCAClient;

import app.backend.AdminEnrollmentService;
import app.backend.CaClientFactory;
import app.backend.ContractFactory;
import app.backend.UserRegistrationService;
import app.backend.WalletFactory;
import app.gui.ForumJFrame;
import app.tests.LikeTests;
import app.tests.TransactionTests;
import app.user.PublishableAppUser;

class App {

    static {
        System.setProperty("org.hyperledger.fabric.sdk.service_discovery.as_localhost", "true");
    }

    private PublishableAppUser appClient;
    private Contract contract;

    private Contract contract2;

    public static void main(final String[] args) throws Exception {
        final App app = new App();
        app.gui();
        //app.test();
    }

    public App() {
        final File file = new File("peer.yaml");
        final ObjectMapper om = new ObjectMapper(new YAMLFactory());

        try {

            final PeerInfo peerInfo = om.readValue(file, PeerInfo.class);
            final Wallet wallet = WalletFactory.GetWallet(peerInfo.getAdminName());
            final HFCAClient client = CaClientFactory.CreateCaClient(peerInfo.getCaUrl(), peerInfo.getPemPath());

            tryEnrollAdmin(wallet, client, peerInfo);
            tryRegisterUser(wallet, client, peerInfo);

            final var contractCreation = new ContractFactory.Entity();
            contractCreation.userId = peerInfo.getUserId();
            contractCreation.channel = peerInfo.getChannel();
            contractCreation.contractName = peerInfo.getContractName();
            contractCreation.networkConfigPath = Paths.get("..", "blockchain", "hlf2-network", "organizations",
                    "peerOrganizations", "org1.example.com", "connection-org1.yaml");
            ;

            final var contract = ContractFactory.CreateContract(wallet, contractCreation);
            final var appClient = new PublishableAppUser(wallet, contract, peerInfo.getUserId());

            this.appClient = appClient;
            this.contract = contract;

            this.contract2 = ContractFactory.CreateContract(wallet, contractCreation);


        } catch (final Exception e) {
            System.out.println("An error occurred when fetching wallet or client");
            System.err.println(e);
            e.printStackTrace();

        }
    }

    private void test() {
        try {
            //new LikeTests(this.contract).benchmark();
            new TransactionTests().benchmark();
            //new TransactionTests().test(this.appClient);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private void gui() {
        ForumJFrame.run(this.contract);
    }

    private void tryEnrollAdmin(final Wallet wallet, final HFCAClient client, final PeerInfo peer) {

        final var entity = new AdminEnrollmentService.Entity();
        entity.adminName = peer.getAdminName();
        entity.adminSecret = peer.getAdminSecret();
        entity.hostName = peer.getHostName();
        entity.mspId = peer.getMpsId();
        entity.profile = peer.getProfile();

        try {
            final var enrollmentService = new AdminEnrollmentService(wallet, client);
            enrollmentService.EnrollAdmin(entity);
        } catch (final Exception e) {
            System.out.println("An error occurred when enrolling admin");
            e.printStackTrace();
        }
    }

    private void tryRegisterUser(final Wallet wallet, final HFCAClient client, final PeerInfo peer) throws Exception {

        final User adminUser = createUser((X509Identity) wallet.get(peer.getAdminName()), peer);
        final var identities = client.getHFCAIdentities(adminUser);
        for (final var identity : identities) {
            if (identity.getEnrollmentId().equals(peer.getUserId()))
                return;
        }

        try {
            final var registrationService = new UserRegistrationService(wallet, client);
            registrationService.RegisterUser(adminUser, peer.getUserId());
        } catch (final Exception e) {
            System.out.println("An error occurred when registrating user");
            System.err.println(e);
        }
    }

    private User createUser(final X509Identity identity, final PeerInfo peer) {

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

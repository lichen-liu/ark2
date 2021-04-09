package app;

import java.net.InetSocketAddress;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.Gateway;
import org.hyperledger.fabric.gateway.Network;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallets;

class App {
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
        try {
            new AdminEnrollment().enroll(true);
            new UserRegistration().register(true);
        } catch (Exception e) {
            System.out.println("Something is wrong with Admin Enrollment or User Registration");
            System.err.println(e);
        }

        // connect to the network and invoke the smart contract
        try (Gateway gateway = this.connect()) {
            Network network = gateway.getNetwork("mychannel");
            Contract contract = network.getContract("agreements");

            try {
                byte[] result = contract.evaluateTransaction("getPointTransaction", "ptTransaction0");
                System.out.println("result: " + new String(result));
            } catch (Exception e) {
                e.printStackTrace(System.out);
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    private Gateway connect() throws Exception {
        // Load a file system based wallet for managing identities.
        Path walletPath = Paths.get("wallet");
        Wallet wallet = Wallets.newFileSystemWallet(walletPath);
        // load a CCP
        Path networkConfigPath = Paths.get("..", "blockchain", "hlf2-network", "organizations", "peerOrganizations",
                "org1.example.com", "connection-org1.yaml");

        Gateway.Builder builder = Gateway.createBuilder();
        builder.identity(wallet, "appUser").networkConfig(networkConfigPath).discovery(true);
        return builder.connect();
    }

    // public static PublicKey get(String filename)
    // throws Exception {

    // byte[] keyBytes = Files.readAllBytes(Paths.get(filename));

    // X509EncodedKeySpec spec =
    // new X509EncodedKeySpec(keyBytes);
    // KeyFactory kf = KeyFactory.getInstance("RSA");
    // return kf.generatePublic(spec);
    // }
}

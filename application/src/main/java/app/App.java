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

        invokePeer();
    }

    private static void invokePeer() {

        try {
			//EnrollAdmin.main(null);
			//RegisterUser.main(null);
		} catch (Exception e) {
			System.err.println(e);
		}

		// connect to the network and invoke the smart contract
		try (Gateway gateway = connect()) {
			Network network = gateway.getNetwork("mychannel");
			Contract contract = network.getContract("Agreements");

			byte[] result;

			result = contract.evaluateTransaction("getPost", "POST1");
            System.out.println("result: " + new String(result));
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    private static Gateway connect() throws Exception{
		// Load a file system based wallet for managing identities.
		Path walletPath = Paths.get("wallet");
		Wallet wallet = Wallets.newFileSystemWallet(walletPath);
		// load a CCP
		Path networkConfigPath = Paths.get("..", "chaincode", "hlf2-network", "organizations", "peerOrganizations", "org1.example.com", "connection-org1.yaml");

		Gateway.Builder builder = Gateway.createBuilder();
		builder.identity(wallet, "appUser").networkConfig(networkConfigPath).discovery(true);
		return builder.connect();
	}
}

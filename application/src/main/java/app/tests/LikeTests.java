package app.tests;

import java.io.IOException;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.Wallet;

import app.backend.ContractFactory;
import app.backend.WalletFactory;
import app.repository.contracts.Transaction;
import app.tests.utils.TestClient;
import app.tests.utils.TestRunner;
import app.tests.utils.TestVoid;

public class LikeTests {
    private final Contract contract;

    public LikeTests(final Contract contract) {
        this.contract = contract;
    }

    public void benchmark() throws Exception {

        final TestRunner runner = new TestRunner("Runner 1");
        final var client = TestClient.createTestClient(contract);

        final String postKey = client.publishNewPost("testPost1");

        final var likeEntry = new Transaction.Entry(client.getPublicKeyString(), 100.00);
        TestVoid test = () -> {
            return client.publishNewLike(postKey, likeEntry);
        };
        runner.insertNewTest(test, 5);

        test = () -> {
            return client.fetchAllLikesByPostKey(postKey);
        };
        runner.insertNewTest(test, 1);

        final Thread thread = new Thread(runner);
        thread.start();
    }

    private void multiThreadWithoutDependencyTests() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, IOException {
        final Wallet wallet = WalletFactory.GetWallet("admin");

        final var contractCreation = new ContractFactory.Entity();
        contractCreation.userId = "appUser3";
        contractCreation.channel = "mychannel";
        contractCreation.contractName = "ForumAgreement";
        contractCreation.networkConfigPath = Paths.get("..", "blockchain", "hlf2-network", "organizations",
                "peerOrganizations", "org1.example.com", "connection-org1.yaml");

        final var contract = ContractFactory.CreateContract(wallet, contractCreation);

        final TestRunner runner1 = new TestRunner("Runner 1");
        final TestRunner runner2 = new TestRunner("Runner 2");

        final var client1 = TestClient.createTestClient(contract);
        final var client2 = TestClient.createTestClient(contract);

        final var runner1Tests = new ArrayList<TestVoid>();
        final var runner2Tests = new ArrayList<TestVoid>();

        final String postKey = client1.publishNewPost("testPost1");
        final String postKey2 = client2.publishNewPost("testPost1");

        final var likeEntry = new Transaction.Entry(client1.getPublicKeyString(), 100.00);
        TestVoid test = () -> {
            return client1.publishNewLike(postKey, likeEntry);
        };
        runner1.insertNewTest(test, 5);

        final var likeEntry2 = new Transaction.Entry(client2.getPublicKeyString(), 200.00);
        TestVoid test2 = () -> {
            return client2.publishNewLike(postKey, likeEntry2);
        };
        runner2.insertNewTest(test2, 5);

        final Thread thread1 = new Thread(runner1);
        final Thread thread2 = new Thread(runner2);

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
    }

}

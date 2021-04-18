package app.tests;

import java.io.IOException;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import org.hyperledger.fabric.gateway.ContractException;
import org.hyperledger.fabric.gateway.Wallet;

import app.backend.ContractFactory;
import app.backend.WalletFactory;
import app.tests.utils.Logger;
import app.tests.utils.TestClient;
import app.tests.utils.TestRunner;
import app.tests.utils.TestVoid;

public class PostTests {
    private final Logger logger;

    public PostTests() {
        this.logger = new Logger();
    }

    public void benchmark() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, ContractException {
        try {
            singleThreadTests();
            multiThreadWithoutDependencyTests();
            multiThreadWithDependencyTests();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private void singleThreadTests() throws Exception {
        
        final Wallet wallet = WalletFactory.GetWallet("admin");

        final var contractCreation = new ContractFactory.Entity();
        contractCreation.userId = "appUser3";
        contractCreation.channel = "mychannel";
        contractCreation.contractName = "ForumAgreement";
        contractCreation.networkConfigPath = Paths.get("..", "blockchain", "hlf2-network", "organizations",
                "peerOrganizations", "org1.example.com", "connection-org1.yaml");
        ;

        final var contract = ContractFactory.CreateContract(wallet, contractCreation);

        final TestRunner runner1 = new TestRunner("Runner 1");

        final var client1 = TestClient.createTestClient(contract);
        final var postKey = client1.publishNewPost("hahaha");

        final var actualPost = client1.fetchPostByPostKey(postKey);
        System.out.println("Actual post is: " + actualPost);

    }

    private void multiThreadWithDependencyTests() throws Exception {

        final Wallet wallet = WalletFactory.GetWallet("admin");

        final var contractCreation = new ContractFactory.Entity();
        contractCreation.userId = "appUser3";
        contractCreation.channel = "mychannel";
        contractCreation.contractName = "ForumAgreement";
        contractCreation.networkConfigPath = Paths.get("..", "blockchain", "hlf2-network", "organizations",
                "peerOrganizations", "org1.example.com", "connection-org1.yaml");
        ;

        final var contract = ContractFactory.CreateContract(wallet, contractCreation);

        final TestRunner runner1 = new TestRunner("Runner 1");
        final TestRunner runner2 = new TestRunner("Runner 2");

        final var client1 = TestClient.createTestClient(contract);
        final var client2 = TestClient.createTestClient(contract);

        final var runner1Tests = new ArrayList<TestVoid>();
        final var runner2Tests = new ArrayList<TestVoid>();

        runner1Tests.add((TestVoid) () -> {
            return client1.publishNewPost("I am client one");
        });

        for (final var test : runner1Tests) {
            runner1.insertNewTest(test, 5);
        }

        runner2Tests.add((TestVoid) () -> {
            return client2.publishNewPost("I am client two");
        });

        for (final var test : runner2Tests) {
            runner2.insertNewTest(test, 5);
        }

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

    private void multiThreadWithoutDependencyTests()
            throws IOException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, ContractException {

    }
}

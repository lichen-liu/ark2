package app.tests.simple;

import java.io.IOException;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;

import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.Wallet;

import app.backend.ContractFactory;
import app.backend.WalletFactory;
import app.tests.Test;
import app.tests.util.Logger;
import app.tests.util.TestClient;
import app.tests.util.TestRunner;
import app.tests.util.TestVoid;

public class LikeTests implements Test {
    private final Contract contract;

    public LikeTests(final Contract contract) {
        this.contract = contract;
    }

    @Override
    public void runTest(final Logger logger) {
        try {
            singleThreadLikingAPostTest(contract);
            twoThreadLikingTheSamePostTest();
        } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
    }

    private void singleThreadLikingAPostTest(final Contract contract)
            throws InvalidAlgorithmParameterException, NoSuchAlgorithmException {
        final TestRunner runner = new TestRunner("Runner 1");
        final var client = TestClient.createTestClient(contract);

        final String postKey = client.publishNewPost("testPost1");

        TestVoid test = () -> {
            return client.publishNewLike(postKey);
        };
        runner.insertNewTest(test, 5);

        test = () -> {
            return client.fetchLikesByPostKey(postKey);
        };
        runner.insertNewTest(test, 1);

        final Thread thread = new Thread(runner);
        thread.start();
    }

    private void twoThreadLikingTheSamePostTest()
            throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, IOException {
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

        final String postKey = client1.publishNewPost("testPost1");

        final TestVoid test = () -> {
            return client1.publishNewLike(postKey);
        };
        runner1.insertNewTest(test, 4);
        final TestVoid test2 = () -> {
            return client2.publishNewLike(postKey);
        };
        runner2.insertNewTest(test2, 4);

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

    public Contract getContract() {
        return contract;
    }
}

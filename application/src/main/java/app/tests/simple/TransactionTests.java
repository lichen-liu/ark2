package app.tests.simple;

import java.io.IOException;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

import org.hyperledger.fabric.gateway.ContractException;
import org.hyperledger.fabric.gateway.Wallet;

import app.backend.ContractFactory;
import app.backend.WalletFactory;
import app.repository.data.Payment;
import app.repository.data.PointTransaction;
import app.tests.Test;
import app.tests.util.Logger;
import app.tests.util.TestClient;
import app.tests.util.TestRunner;
import app.tests.util.TestVoid;
import app.util.ByteUtils;

public class TransactionTests extends Test {

    public TransactionTests() {
        super("TransactionTests");
    }

    @Override
    public void runTest() throws Exception {
        singleThreadTests();
        // multiThreadWithoutDependencyTests();
        // multiThreadWithDependencyTests();
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
        final TestRunner runner = new TestRunner("Runner 1");

        final var client1 = TestClient.createTestClient(contract);
        final var client2 = TestClient.createTestClient(contract);
        final var client3 = TestClient.createTestClient(contract);

        final var client1Id = ByteUtils.toAsciiString(client1.getPublicKey().getEncoded());
        final var client2Id = ByteUtils.toAsciiString(client2.getPublicKey().getEncoded());
        final var client3Id = ByteUtils.toAsciiString(client3.getPublicKey().getEncoded());

        final Payment transaction = new Payment();
        transaction.reference = "reference";
        transaction.payer = new PointTransaction.Entry(client1Id, (double) 20);
        transaction.payees = Arrays.asList(new PointTransaction.Entry(client2Id, (double) 10),
                new PointTransaction.Entry(client3Id, (double) 10));
        runner.insertNewTest((TestVoid) () -> {
            return client2.publishNewTransaction(transaction);
        }, 1);

        final Payment transaction2 = new Payment();
        transaction2.reference = "reference";
        transaction2.payer = new PointTransaction.Entry(client2Id, (double) 10);
        transaction2.payees = Arrays.asList(new PointTransaction.Entry(client1Id, (double) 10));
        runner.insertNewTest((TestVoid) () -> {
            return client2.publishNewTransaction(transaction2);
        }, 1);

        final Payment transaction3 = new Payment();
        transaction3.reference = "reference";
        transaction3.payer = new PointTransaction.Entry(client3Id, (double) 30);
        transaction3.payees = Arrays.asList(new PointTransaction.Entry(client1Id, (double) 30));
        runner.insertNewTest((TestVoid) () -> {
            return client2.publishNewTransaction(transaction3);
        }, 1);

        final Thread thread = new Thread(runner);

        thread.start();

        try {
            thread.join();
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }

        logger.print(contract.evaluateTransaction("computePointAmountByUserId", client1Id));
        logger.print(contract.evaluateTransaction("computePointAmountByUserId", client2Id));
        logger.print(contract.evaluateTransaction("computePointAmountByUserId", client3Id));
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

        final var client1Id = ByteUtils.toAsciiString(client1.getPublicKey().getEncoded());
        final var client2Id = ByteUtils.toAsciiString(client2.getPublicKey().getEncoded());

        final var runner1Tests = new ArrayList<TestVoid>();
        final var runner2Tests = new ArrayList<TestVoid>();

        final Payment transaction = new Payment();
        transaction.reference = "reference";
        transaction.payer = new PointTransaction.Entry(client1Id, (double) 100);
        transaction.payees = Arrays.asList(new PointTransaction.Entry(client2Id, (double) 100));
        runner1Tests.add((TestVoid) () -> {
            return client1.publishNewTransaction(transaction);
        });

        final Payment transaction2 = new Payment();
        transaction2.reference = "reference";
        transaction2.payer = new PointTransaction.Entry(client1Id, (double) 200);
        transaction2.payees = Arrays.asList(new PointTransaction.Entry(client2Id, (double) 200));
        runner1Tests.add((TestVoid) () -> {
            return client1.publishNewTransaction(transaction2);
        });

        for (final var test : runner1Tests) {
            runner1.insertNewTest(test, 1);
        }

        final Payment transaction3 = new Payment();
        transaction3.reference = "reference";
        transaction3.payer = new PointTransaction.Entry(client2Id, (double) 5);
        transaction3.payees = Arrays.asList(new PointTransaction.Entry(client1Id, (double) 5));

        runner2Tests.add((TestVoid) () -> {
            return client2.publishNewTransaction(transaction3);
        });

        final Payment transaction4 = new Payment();
        transaction4.reference = "reference";
        transaction4.payer = new PointTransaction.Entry(client2Id, (double) 10);
        transaction4.payees = Arrays.asList(new PointTransaction.Entry(client1Id, (double) 10));
        runner2Tests.add((TestVoid) () -> {
            return client2.publishNewTransaction(transaction4);
        });

        for (final var test : runner2Tests) {
            runner2.insertNewTest(test, 1);
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

        logger.print(contract.evaluateTransaction("computePointAmountByUserId", client1Id));
        logger.print(contract.evaluateTransaction("computePointAmountByUserId", client2Id));
    }

    public Logger getLogger() {
        return logger;
    }

    private void multiThreadWithoutDependencyTests()
            throws IOException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, ContractException {
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
        final var client3 = TestClient.createTestClient(contract);
        final var client4 = TestClient.createTestClient(contract);

        final var client1Id = ByteUtils.toAsciiString(client1.getPublicKey().getEncoded());
        final var client2Id = ByteUtils.toAsciiString(client2.getPublicKey().getEncoded());
        final var client3Id = ByteUtils.toAsciiString(client3.getPublicKey().getEncoded());
        final var client4Id = ByteUtils.toAsciiString(client4.getPublicKey().getEncoded());

        final var runner1Tests = new ArrayList<TestVoid>();
        final var runner2Tests = new ArrayList<TestVoid>();

        final Payment transaction = new Payment();
        transaction.reference = "reference";
        transaction.payer = new PointTransaction.Entry(client1Id, (double) 100);
        transaction.payees = Arrays.asList(new PointTransaction.Entry(client2Id, (double) 100));
        runner1Tests.add((TestVoid) () -> {
            return client1.publishNewTransaction(transaction);
        });

        for (final var test : runner1Tests) {
            runner1.insertNewTest(test, 5);
        }

        final Payment transaction2 = new Payment();
        transaction2.reference = "reference";
        transaction2.payer = new PointTransaction.Entry(client3Id, (double) 5);
        transaction2.payees = Arrays.asList(new PointTransaction.Entry(client4Id, (double) 5));
        runner2Tests.add((TestVoid) () -> {
            return client3.publishNewTransaction(transaction2);
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

        logger.print(contract.evaluateTransaction("computePointAmountByUserId", client2Id));
        logger.print(contract.evaluateTransaction("computePointAmountByUserId", client4Id));
    }
}

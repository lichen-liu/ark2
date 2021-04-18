package app.tests;

import org.hyperledger.fabric.gateway.Contract;

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

        String postKey = client.publishNewPost("testPost1");

        var likeEntry = new Transaction.Entry(client.getPublicKeyString(), 100.00);
        TestVoid test = () -> {return client.publishNewLike(postKey, likeEntry); };
        runner.insertNewTest(test, 5);
     
        test = () -> {return client.fetchAllLikesByPostKey(postKey); };
        runner.insertNewTest(test, 1);

        final Thread thread = new Thread(runner);
        thread.start();
    }
}

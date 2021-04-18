package app.tests;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hyperledger.fabric.gateway.Contract;

import app.repository.contracts.Transaction;
import app.tests.utils.TestClient;
import app.tests.utils.TestRunner;
import app.tests.utils.TestVoid;
import app.utils.GensonDeserializer;

public class LikeTests {
    private final int testId = 0;
    private final ObjectMapper objectMapper;
    private final Contract contract;

    public LikeTests(final Contract contract) {
        this.objectMapper = new ObjectMapper();
        this.contract = contract;
    }

    public void benchmark() throws Exception {

        final TestRunner runner = new TestRunner("Runner 1");
        final var client = TestClient.createTestClient(contract);

        String postKey = client.publishNewPost("testPost1");
        System.out.println("The post key is:" + postKey);

        var desiralizer = new GensonDeserializer();

        // var likeEntry = new Transaction.Entry(client.getPublicKeyString(), 100.00);
        // System.out.println("The publish like result is: " + new String(contract.submitTransaction("publishNewLike", "2021", postKey, desiralizer.transactionEntriesToJson(likeEntry),
        // "like", "big")));


        TestVoid test = () -> { return client.fetchAllPostKeys(); };
        runner.insertNewTest(test, 1);

        // //var likeEntry = new Transaction.Entry(client.getPublicKeyString(), 100.00);
        // //test = () -> {return client.publishNewLike(postKey, likeEntry); };
     
        // runner.insertNewTest(test, 1);

        var raw = new String(contract.evaluateTransaction("getAllLikeKeysByPostKey", "this post does not fucking exist"));
        var keys = desiralizer.toStringArray(raw);
        for(var key : keys){
            System.out.println("The like key is: " + key);
            System.out.println("The like content: " + new String(contract.evaluateTransaction("getLikeByKey", postKey)));
        }
        // test = () -> {return client.fetchAllLikesByPostKey(postKey); };
        // runner.insertNewTest(test, 1);

        // final Thread thread = new Thread(runner);
        // thread.start();

    }
}

package app.tests;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hyperledger.fabric.gateway.Contract;

import app.AppClient;
import app.repository.contracts.Transaction;
import app.repository.contracts.Transaction.Entry;
import app.tests.utils.TestClient;
import app.tests.utils.TestRunner;
import app.tests.utils.TestVoid;


public class LikeTests {
    private int testId = 0;
    private final ObjectMapper objectMapper;
    private Contract contract;

    public LikeTests(Contract contract) {
        this.objectMapper = new ObjectMapper();
        this.contract = contract;
    }

    public void benchmark() throws Exception {
        
        TestRunner runner = new TestRunner();

        var client = TestClient.createTestClient(contract);  

        TestVoid test = () -> { return client.fetchAllPostKeys(); };
        runner.InsertNewTest(test, 1);

        Thread thread = new Thread(runner);
        thread.start();
    }
}

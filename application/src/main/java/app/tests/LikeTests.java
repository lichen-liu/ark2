package app.tests;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hyperledger.fabric.gateway.Contract;

import app.tests.utils.TestClient;
import app.tests.utils.TestRunner;
import app.tests.utils.TestVoid;

public class LikeTests {
    private final int testId = 0;
    private final ObjectMapper objectMapper;
    private final Contract contract;

    public LikeTests(final Contract contract) {
        this.objectMapper = new ObjectMapper();
        this.contract = contract;
    }

    public void benchmark() throws Exception {

        final TestRunner runner = new TestRunner();

        final var client = TestClient.createTestClient(contract);

        final TestVoid test = () -> {
            return client.fetchAllPostKeys();
        };
        runner.InsertNewTest(test, 1);

        final Thread thread = new Thread(runner);
        thread.start();
    }
}

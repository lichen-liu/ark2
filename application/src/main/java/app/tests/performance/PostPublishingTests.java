package app.tests.performance;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;

import org.hyperledger.fabric.gateway.Contract;

import app.tests.Test;
import app.tests.util.Logger;
import app.tests.util.Logger.Builder;
import app.tests.util.TestClient;
import app.user.NamedService;

public class PostPublishingTests implements Test {
    private final Contract contract;
    private NamedService user = null;

    public PostPublishingTests(final Contract contract) {
        this.contract = contract;
    }

    @Override
    public Logger initLogger(final Builder builder) {
        return builder.create("PostPublishingTests");
    }

    @Override
    public boolean preTest(final Logger logger) {
        try {
            this.user = TestClient.createTestClient(contract);
        } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean runTest(final Logger logger) {
        return true;
    }

}

package app.tests.performance.write;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;

import org.hyperledger.fabric.gateway.Contract;

import app.tests.Test;
import app.tests.util.Logger;
import app.tests.util.TestClient;
import app.user.NamedService;

public class DislikePublishingTests implements Test {

    private final Contract contract;
    private String postKey;
    private NamedService user = null;
    private final int iterations;

    public DislikePublishingTests(final Contract contract, final int iterations) {
        this.contract = contract;
        this.iterations = iterations;
    }

    @Override
    public int numberIterations() {
        return iterations;
    }

    @Override
    public boolean pre(final Logger logger) {
        try {
            this.postKey = TestClient.createTestClient(contract).publishNewPost("_");
            this.user = TestClient.createTestClient(contract);
        } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public String testName() {
        return "DislikePublishingTests";
    }

    @Override
    public boolean runTest(final Logger logger, final int currentIteration) {
        String dislikeKey = null;
        do {
            dislikeKey = this.user.publishNewDislike(postKey);
            logger.printResult(dislikeKey);
        } while (dislikeKey == null);

        return true;
    }
}

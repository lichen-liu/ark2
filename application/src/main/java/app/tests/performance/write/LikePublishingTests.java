package app.tests.performance.write;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;

import org.hyperledger.fabric.gateway.Contract;

import app.tests.Test;
import app.tests.util.Logger;
import app.tests.util.TestClient;
import app.user.NamedService;

public class LikePublishingTests implements Test {

    private final Contract contract;
    private String postKey;
    private NamedService user = null;
    private final int iterations;

    @Override
    public String testName() {
        return "LikePublishingTests";
    }

    public LikePublishingTests(final Contract contract, final int iterations) {
        this.contract = contract;
        this.iterations = iterations;
    }

    @Override
    public int numberIterations() {
        return this.iterations;
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
    public boolean runTest(final Logger logger, final int currentIteration) {
        String likeKey = null;
        do {
            likeKey = this.user.publishNewLike(postKey);
            logger.printResult(likeKey);
        } while (likeKey == null);

        return true;
    }

}

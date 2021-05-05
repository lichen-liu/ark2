package app.tests.performance.write;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.hyperledger.fabric.gateway.Contract;

import app.tests.Test;
import app.tests.util.ContentGeneration;
import app.tests.util.Logger;
import app.tests.util.TestClient;
import app.user.NamedService;

public class PostPublishingTests implements Test {
    private final Contract contract;
    private NamedService user = null;
    private List<String> contents = null;
    private int iterations;

    public PostPublishingTests(final Contract contract, int iterations) {
        this.contract = contract;
        this.iterations = iterations;
    }

    @Override
    public int numberIterations() {
        return iterations;
    }

    @Override
    public String testName() {
        return "PostPublishingTests";
    }

    @Override
    public boolean pre(final Logger logger) {
        try {
            this.user = TestClient.createTestClient(contract);
            this.contents = new ArrayList<String>();
            for (int i = 0; i < numberIterations(); i++) {
                this.contents.add(ContentGeneration.randomString(100));
            }
        } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean runTest(final Logger logger, final int currentIteration) {
        final String content = this.contents.get(currentIteration);
        final String postKey = null;
        do {
            logger.printResult(this.user.publishNewPost(content));
            logger.printResult(postKey);
        } while (postKey == null);

        return true;
    }

}

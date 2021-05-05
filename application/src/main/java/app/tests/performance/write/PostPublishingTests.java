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

    public PostPublishingTests(final Contract contract) {
        this.contract = contract;
    }

    @Override
    public int numberIterations() {
        return 100;
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
        for (final var content : this.contents) {
            String postKey = null;
            do {
                postKey = this.user.publishNewPost(content);
                logger.printResult(postKey);
            } while (postKey == null);
        }

        return true;
    }

}

package app.tests.performance;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.hyperledger.fabric.gateway.Contract;

import app.tests.Test;
import app.tests.util.ContentGeneration;
import app.tests.util.Logger;
import app.tests.util.Logger.Builder;
import app.tests.util.TestClient;
import app.user.NamedService;

public class PostPublishingTests implements Test {
    private final Contract contract;
    private NamedService user = null;
    private List<String> contents = null;
    private final int numPosts;

    public PostPublishingTests(final Contract contract, final int numPosts) {
        this.contract = contract;
        this.numPosts = numPosts;
    }

    @Override
    public Logger initLogger(final Builder builder) {
        return builder.create("PostPublishingTests");
    }

    @Override
    public boolean preTest(final Logger logger, final int currentIteration) {
        try {
            this.user = TestClient.createTestClient(contract);
            this.contents = new ArrayList<String>();
            for (int i = 0; i < this.numPosts; i++) {
                this.contents.add(ContentGeneration.randomString(200));
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
            } while (postKey == null);
        }

        return true;
    }

}

package app.tests.performance.write;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;

import org.hyperledger.fabric.gateway.Contract;

import app.tests.Test;
import app.tests.util.ContentGeneration;
import app.tests.util.Logger;
import app.user.NamedService;
import app.user.ServiceProvider;

public class PostPublishingTests implements Test {
    private final Contract contract;
    private NamedService user = null;
    private List<String> contents = null;
    private final int iterations;
    private final KeyPair userKeyPair;

    public PostPublishingTests(final Contract contract, final int iterations, final KeyPair userKeyPair) {
        this.contract = contract;
        this.iterations = iterations;
        this.userKeyPair = userKeyPair;
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
        this.user = ServiceProvider.createNamedService(this.contract, userKeyPair.getPublic(),
                userKeyPair.getPrivate());
        this.contents = new ArrayList<String>();
        for (int i = 0; i < numberIterations(); i++) {
            this.contents.add(ContentGeneration.randomString(100));
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

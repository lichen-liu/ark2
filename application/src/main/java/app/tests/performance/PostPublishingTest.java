package app.tests.performance;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;

import org.hyperledger.fabric.gateway.Contract;

import app.tests.Testable;
import app.tests.util.ContentGeneration;
import app.tests.util.Logger;
import app.user.NamedService;
import app.user.ServiceProvider;

public class PostPublishingTest implements Testable {
    private final Contract contract;
    private NamedService user = null;
    private List<String> contents = null;
    private final KeyPair userKeyPair;

    public PostPublishingTest(final Contract contract, final KeyPair userKeyPair) {
        this.contract = contract;
        this.userKeyPair = userKeyPair;
    }

    @Override
    public boolean pre(final Logger logger, final int numberIteration) {
        this.user = ServiceProvider.createNamedService(this.contract, userKeyPair.getPublic(),
                userKeyPair.getPrivate());
        this.contents = new ArrayList<String>();
        for (int i = 0; i < numberIteration; i++) {
            this.contents.add(ContentGeneration.randomString(100));
        }
        return true;
    }

    @Override
    public boolean runTest(final Logger logger, final int currentIteration, final int numberIteration) {
        final String content = this.contents.get(currentIteration);
        String postKey = null;
        do {
            postKey = this.user.publishNewPost(content);
            logger.printResult(postKey);
        } while (postKey == null);

        return true;
    }
}

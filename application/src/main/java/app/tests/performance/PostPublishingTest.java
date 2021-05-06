package app.tests.performance;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.hyperledger.fabric.gateway.Contract;

import app.service.NamedService;
import app.service.ServiceProvider;
import app.tests.Testable;
import app.tests.util.ContentGeneration;
import app.tests.util.Logger;

public class PostPublishingTest implements Testable {
    private final Contract contract;
    private NamedService user = null;
    private List<String> contents = null;
    private final KeyPair userKeyPair;
    private final int iterationMultipler;

    public PostPublishingTest(final Contract contract, final KeyPair userKeyPair, final int iterationMultipler) {
        this.contract = contract;
        this.userKeyPair = userKeyPair;
        this.iterationMultipler = iterationMultipler;
    }

    @Override
    public Optional<Integer> requestNumberIterations(final int plannedNumberIterations) {
        return Optional.of(plannedNumberIterations * this.iterationMultipler);
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

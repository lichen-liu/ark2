package app.tests.performance;

import org.hyperledger.fabric.gateway.Contract;

import app.tests.Testable;
import app.tests.util.Logger;
import app.user.AnonymousService;
import app.user.ServiceProvider;

public class PostsFetchingTests implements Testable {
    private final Contract contract;
    private AnonymousService user = null;
    private final String userKey;
    private final int iterations;

    @Override
    public String testName() {
        return "PostsFetchingTests";
    }

    public PostsFetchingTests(final Contract contract, final int iterations, final String userKey) {
        this.contract = contract;
        this.userKey = userKey;
        this.iterations = iterations;
    }

    @Override
    public int numberIterations() {
        return this.iterations;
    }

    @Override
    public boolean pre(final Logger logger) {
        this.user = ServiceProvider.createAnonymousService(this.contract);

        return true;
    }

    @Override
    public boolean runTest(final Logger logger, final int currentIteration) {
        final var userPostKeys = this.user.fetchPostKeysByUserId(this.userKey);
        boolean isValid = true;
        for (final var userPostKey : userPostKeys) {
            if (!this.user.verifyPost(userPostKey, null).isValid()) {
                isValid = false;
            }
        }
        logger.printResult(isValid ? "Valid" : "Invalid");

        return true;
    }

}

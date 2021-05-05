package app.tests.performance.read.ids;

import org.hyperledger.fabric.gateway.Contract;

import app.tests.Test;
import app.tests.util.Logger;
import app.user.AnonymousService;
import app.user.ServiceProvider;

public class LikeKeysFetchingTests implements Test {
    private final Contract contract;
    private AnonymousService user = null;
    private final String postKey;
    private final int iterations;

    public LikeKeysFetchingTests(final Contract contract, final int iterations, final String postKey) {
        this.postKey = postKey;
        this.contract = contract;
        this.iterations = iterations;
    }

    @Override
    public int numberIterations() {
        return this.iterations;
    }

    @Override
    public String testName() {
        return "LikeKeysFetchingTests";
    }

    @Override
    public boolean pre(final Logger logger) {
        this.user = ServiceProvider.createAnonymousService(this.contract);

        return true;
    }

    @Override
    public boolean runTest(final Logger logger, final int currentIteration) {
        if (this.postKey == null) {
            final var result = this.user.fetchPostKeys();
            logger.printResult(result != null ? String.valueOf(result.length) : "null");
        } else {
            final var result = this.user.fetchPostKeysByUserId(this.postKey);
            logger.printResult(result != null ? String.valueOf(result.length) : "null");
        }

        return true;
    }
}

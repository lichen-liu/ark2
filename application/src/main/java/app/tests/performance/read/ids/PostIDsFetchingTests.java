package app.tests.performance.read.ids;

import javax.annotation.Nullable;

import org.hyperledger.fabric.gateway.Contract;

import app.tests.Test;
import app.tests.util.Logger;
import app.user.AnonymousService;
import app.user.ServiceProvider;

public class PostIDsFetchingTests implements Test {
    private final Contract contract;
    private AnonymousService user = null;
    private final String userKey;
    private final int iterations;

    public PostIDsFetchingTests(final Contract contract, final int iterations, final @Nullable String userKey) {
        this.userKey = userKey;
        this.contract = contract;
        this.iterations = iterations;
    }

    @Override
    public int numberIterations() {
        return this.iterations;
    }

    @Override
    public String testName() {
        return "PostIDsFetchingTests";
    }

    @Override
    public boolean pre(final Logger logger) {
        this.user = ServiceProvider.createAnonymousService(this.contract);

        return true;
    }

    @Override
    public boolean runTest(final Logger logger, final int currentIteration) {
        if (this.userKey == null) {
            logger.printResult(this.user.fetchPostKeys());
        } else {
            logger.printResult(this.user.fetchPostKeysByUserId(this.userKey));
        }

        return true;
    }
}

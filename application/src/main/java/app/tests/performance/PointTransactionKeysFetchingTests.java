package app.tests.performance;

import org.hyperledger.fabric.gateway.Contract;

import app.tests.Testable;
import app.tests.util.Logger;
import app.user.AnonymousService;
import app.user.ServiceProvider;

public class PointTransactionKeysFetchingTests implements Testable {
    private final Contract contract;
    private AnonymousService user = null;
    private final String userKey;
    private final int iterations;

    @Override
    public String testName() {
        return "PointTransactionKeysFetchingTests";
    }

    public PointTransactionKeysFetchingTests(final Contract contract, final int iterations, final String userKey) {
        this.userKey = userKey;
        this.contract = contract;
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
        final var result = this.user.computePointTransactionKeysByUserId(this.userKey);
        logger.printResult(result != null ? String.valueOf(result.length) : "null");

        return true;
    }
}

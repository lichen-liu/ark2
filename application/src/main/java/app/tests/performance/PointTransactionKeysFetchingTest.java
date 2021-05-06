package app.tests.performance;

import org.hyperledger.fabric.gateway.Contract;

import app.tests.util.Logger;

public class PointTransactionKeysFetchingTest extends FetchingByUserKeyTestTemplate {
    public PointTransactionKeysFetchingTest(final Contract contract, final String userKey) {
        super(contract, userKey);
    }

    @Override
    public boolean runTest(final Logger logger, final int currentIteration, final int numberIteration) {
        final var result = this.getService().computePointTransactionKeysByUserId(this.getUserKey());
        logger.printResult(result != null ? String.valueOf(result.length) : "null");

        return true;
    }
}

package app.tests.performance;

import org.hyperledger.fabric.gateway.Contract;

import app.tests.util.Logger;

public class PointBalanceFetchingTest extends FetchingByUserKeyTestTemplate {
    public PointBalanceFetchingTest(final Contract contract, final String userKey) {
        super(contract, userKey);
    }

    @Override
    public boolean runTest(final Logger logger, final int currentIteration, final int numberIteration) {
        final var result = this.getService().computePointBalanceByUserId(this.getUserKey());
        logger.printResult(result);

        return true;
    }
}

package app.tests.performance;

import org.hyperledger.fabric.gateway.Contract;

import app.tests.util.Logger;

public class PointTransactionsFetchingTest extends FetchingByUserKeyTestTemplate {
    public PointTransactionsFetchingTest(final Contract contract, final String userKey) {
        super(contract, userKey);
    }

    @Override
    public boolean runTest(final Logger logger, final int currentIteration, final int numberIteration) {
        final var userPointTransactionKeys = this.getService().computePointTransactionKeysByUserId(this.getUserKey());
        boolean isValid = true;
        for (final var userPointTransactionKey : userPointTransactionKeys) {
            if (!this.getService().verifyPointTransaction(userPointTransactionKey).isValid()) {
                isValid = false;
            }
        }
        logger.printResult(isValid ? "Valid" : "Invalid");

        return true;
    }
}

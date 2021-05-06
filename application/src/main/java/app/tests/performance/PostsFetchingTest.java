package app.tests.performance;

import org.hyperledger.fabric.gateway.Contract;

import app.tests.util.Logger;

public class PostsFetchingTest extends FetchingByUserKeyTestTemplate {
    public PostsFetchingTest(final Contract contract, final String userKey) {
        super(contract, userKey);
    }

    @Override
    public boolean runTest(final Logger logger, final int currentIteration, final int numberIteration) {
        final var userPostKeys = this.getService().fetchPostKeysByUserId(this.getUserKey());
        boolean isValid = true;
        for (final var userPostKey : userPostKeys) {
            if (!this.getService().verifyPost(userPostKey, null).isValid()) {
                isValid = false;
            }
        }
        logger.printResult(isValid ? "Valid" : "Invalid");

        return true;
    }
}

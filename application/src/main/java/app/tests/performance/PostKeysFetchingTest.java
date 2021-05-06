package app.tests.performance;

import javax.annotation.Nullable;

import org.hyperledger.fabric.gateway.Contract;

import app.tests.util.Logger;

public class PostKeysFetchingTest extends FetchingByUserKeyTestTemplate {
    public PostKeysFetchingTest(final Contract contract, final @Nullable String userKey) {
        super(contract, userKey);
    }

    @Override
    public boolean runTest(final Logger logger, final int currentIteration, final int numberIteration) {
        if (this.getUserKey() == null) {
            final var result = this.getService().fetchPostKeys();
            logger.printResult(result != null ? String.valueOf(result.length) : "null");
        } else {
            final var result = this.getService().fetchPostKeysByUserId(this.getUserKey());
            logger.printResult(result != null ? String.valueOf(result.length) : "null");
        }

        return true;
    }
}

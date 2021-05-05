package app.tests.performance;

import java.util.concurrent.BlockingQueue;

import org.hyperledger.fabric.gateway.Contract;

import app.tests.util.Logger;

public class LikesFetchingTest extends FetchingByPostKeyTestTemplate {
    @Override
    public String testName() {
        return "LikesFetchingTest";
    }

    public LikesFetchingTest(final Contract contract, final BlockingQueue<String> likedPostKeyQueue) {
        super(contract, likedPostKeyQueue);
    }

    @Override
    public boolean runTest(final Logger logger, final int currentIteration, final int numberIteration) {
        final var postLikeKeys = this.getService().fetchLikeKeysByPostKey(this.getPostKey());
        boolean isValid = true;
        for (final var postLikeKey : postLikeKeys) {
            if (!this.getService().verifyLike(postLikeKey).isValid()) {
                isValid = false;
            }
        }
        logger.printResult(isValid ? "Valid" : "Invalid");

        return true;
    }
}

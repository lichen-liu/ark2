package app.tests.performance;

import java.util.concurrent.BlockingQueue;

import org.hyperledger.fabric.gateway.Contract;

import app.tests.util.Logger;

public class DislikesFetchingTest extends FetchingByPostKeyTestTemplate {
    public DislikesFetchingTest(final Contract contract, final BlockingQueue<String> dislikedPostKeyQueue) {
        super(contract, dislikedPostKeyQueue);
    }

    @Override
    public boolean runTest(final Logger logger, final int currentIteration, final int numberIteration) {
        final var postDislikeKeys = this.getService().fetchDislikeKeysByPostKey(this.getPostKey());
        boolean isValid = true;
        for (final var postDislikeKey : postDislikeKeys) {
            if (!this.getService().verifyDislike(postDislikeKey).isValid()) {
                isValid = false;
            }
        }
        logger.printResult(postDislikeKeys.length + ". " + (isValid ? "Valid" : "Invalid"));

        return true;
    }
}

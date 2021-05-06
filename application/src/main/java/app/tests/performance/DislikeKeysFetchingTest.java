package app.tests.performance;

import java.util.concurrent.BlockingQueue;

import org.hyperledger.fabric.gateway.Contract;

import app.tests.util.Logger;

public class DislikeKeysFetchingTest extends FetchingByPostKeyTestTemplate {
    public DislikeKeysFetchingTest(final Contract contract, final BlockingQueue<String> dislikedPostKeyQueue) {
        super(contract, dislikedPostKeyQueue);
    }

    @Override
    public boolean runTest(final Logger logger, final int currentIteration, final int numberIteration) {
        final var result = this.getService().fetchDislikeKeysByPostKey(this.getPostKey());
        logger.printResult(result != null ? String.valueOf(result.length) : "null");

        return true;
    }
}

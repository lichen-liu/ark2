package app.tests.performance;

import java.util.concurrent.BlockingQueue;

import org.hyperledger.fabric.gateway.Contract;

import app.tests.util.Logger;

public class LikeKeysFetchingTest extends FetchingByPostKeyTestTemplate {
    @Override
    public String testName() {
        return "LikeKeysFetchingTest";
    }

    public LikeKeysFetchingTest(final Contract contract, final BlockingQueue<String> likedPostKeyQueue) {
        super(contract, likedPostKeyQueue);
    }

    @Override
    public boolean runTest(final Logger logger, final int currentIteration, final int numberIteration) {
        final var result = this.getService().fetchLikeKeysByPostKey(this.getPostKey());
        logger.printResult(result != null ? String.valueOf(result.length) : "null");

        return true;
    }
}

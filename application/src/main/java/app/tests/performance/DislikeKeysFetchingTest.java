package app.tests.performance;

import java.util.concurrent.BlockingQueue;

import org.hyperledger.fabric.gateway.Contract;

import app.tests.Testable;
import app.tests.util.Logger;
import app.user.AnonymousService;
import app.user.ServiceProvider;

public class DislikeKeysFetchingTest implements Testable {
    private final Contract contract;
    private AnonymousService user = null;
    final BlockingQueue<String> dislikedPostKeyQueue;
    private String postKey;

    @Override
    public String testName() {
        return "DislikeKeysFetchingTest";
    }

    public DislikeKeysFetchingTest(final Contract contract, final BlockingQueue<String> dislikedPostKeyQueue) {
        this.contract = contract;
        this.dislikedPostKeyQueue = dislikedPostKeyQueue;
    }

    @Override
    public boolean pre(final Logger logger, final int numberIteration) {
        this.user = ServiceProvider.createAnonymousService(this.contract);
        try {
            this.postKey = this.dislikedPostKeyQueue.take();
            final var isSuccessful = this.dislikedPostKeyQueue.offer(this.postKey);
            assert isSuccessful;
        } catch (final InterruptedException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public boolean runTest(final Logger logger, final int currentIteration, final int numberIteration) {
        final var result = this.user.fetchDislikeKeysByPostKey(this.postKey);
        logger.printResult(result != null ? String.valueOf(result.length) : "null");

        return true;
    }
}

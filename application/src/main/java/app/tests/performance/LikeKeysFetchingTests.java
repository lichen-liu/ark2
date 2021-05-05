package app.tests.performance;

import java.util.concurrent.BlockingQueue;

import org.hyperledger.fabric.gateway.Contract;

import app.tests.Testable;
import app.tests.util.Logger;
import app.user.AnonymousService;
import app.user.ServiceProvider;

public class LikeKeysFetchingTests implements Testable {
    private final Contract contract;
    private AnonymousService user = null;
    private final int iterations;
    final BlockingQueue<String> likedPostKeyQueue;
    private String postKey;

    @Override
    public String testName() {
        return "LikeKeysFetchingTests";
    }

    public LikeKeysFetchingTests(final Contract contract, final int iterations,
            final BlockingQueue<String> likedPostKeyQueue) {
        this.contract = contract;
        this.iterations = iterations;
        this.likedPostKeyQueue = likedPostKeyQueue;
    }

    @Override
    public int numberIterations() {
        return this.iterations;
    }

    @Override
    public boolean pre(final Logger logger) {
        this.user = ServiceProvider.createAnonymousService(this.contract);
        try {
            this.postKey = this.likedPostKeyQueue.take();
            final var isSuccessful = this.likedPostKeyQueue.offer(this.postKey);
            assert isSuccessful;
        } catch (final InterruptedException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public boolean runTest(final Logger logger, final int currentIteration) {
        final var result = this.user.fetchLikeKeysByPostKey(this.postKey);
        logger.printResult(result != null ? String.valueOf(result.length) : "null");

        return true;
    }
}

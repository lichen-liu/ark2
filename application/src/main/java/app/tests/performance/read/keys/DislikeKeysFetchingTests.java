package app.tests.performance.read.keys;

import java.util.concurrent.BlockingQueue;

import org.hyperledger.fabric.gateway.Contract;

import app.tests.Test;
import app.tests.util.Logger;
import app.user.AnonymousService;
import app.user.ServiceProvider;

public class DislikeKeysFetchingTests implements Test {
    private final Contract contract;
    private AnonymousService user = null;
    private final int iterations;
    final BlockingQueue<String> dislikedPostKeyQueue;
    private String postKey;

    @Override
    public String testName() {
        return "DislikeKeysFetchingTests";
    }

    public DislikeKeysFetchingTests(final Contract contract, final int iterations,
            final BlockingQueue<String> dislikedPostKeyQueue) {
        this.contract = contract;
        this.iterations = iterations;
        this.dislikedPostKeyQueue = dislikedPostKeyQueue;
    }

    @Override
    public int numberIterations() {
        return this.iterations;
    }

    @Override
    public boolean pre(final Logger logger) {
        this.user = ServiceProvider.createAnonymousService(this.contract);
        try {
            this.postKey = this.dislikedPostKeyQueue.take();
        } catch (final InterruptedException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public boolean runTest(final Logger logger, final int currentIteration) {
        final var result = this.user.fetchDislikeKeysByPostKey(this.postKey);
        logger.printResult(result != null ? String.valueOf(result.length) : "null");

        return true;
    }
}

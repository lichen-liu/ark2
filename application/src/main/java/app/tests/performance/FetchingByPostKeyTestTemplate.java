package app.tests.performance;

import java.util.concurrent.BlockingQueue;

import org.hyperledger.fabric.gateway.Contract;

import app.service.AnonymousService;
import app.service.ServiceProvider;
import app.tests.Testable;
import app.tests.util.Logger;

public abstract class FetchingByPostKeyTestTemplate implements Testable {
    private final Contract contract;
    private AnonymousService service = null;

    private final BlockingQueue<String> postKeyQueue;
    private String postKey;

    protected FetchingByPostKeyTestTemplate(final Contract contract, final BlockingQueue<String> postKeyQueue) {
        this.contract = contract;
        this.postKeyQueue = postKeyQueue;
    }

    @Override
    public boolean pre(final Logger logger, final int numberIteration) {
        this.service = ServiceProvider.createAnonymousService(this.contract);
        try {
            this.postKey = this.postKeyQueue.take();
            final var isSuccessful = this.postKeyQueue.offer(this.postKey);
            assert isSuccessful;
        } catch (final InterruptedException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    protected AnonymousService getService() {
        return service;
    }

    protected String getPostKey() {
        return postKey;
    }
}

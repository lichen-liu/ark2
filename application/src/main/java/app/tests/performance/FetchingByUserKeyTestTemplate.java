package app.tests.performance;

import org.hyperledger.fabric.gateway.Contract;

import app.tests.Testable;
import app.tests.util.Logger;
import app.user.AnonymousService;
import app.user.ServiceProvider;

public abstract class FetchingByUserKeyTestTemplate implements Testable {
    private final Contract contract;
    private AnonymousService service = null;
    private final String userKey;

    public FetchingByUserKeyTestTemplate(final Contract contract, final String userKey) {
        this.contract = contract;
        this.userKey = userKey;
    }

    @Override
    public boolean pre(final Logger logger, final int numberIteration) {
        this.service = ServiceProvider.createAnonymousService(this.contract);

        return true;
    }

    protected AnonymousService getService() {
        return service;
    }

    protected String getUserKey() {
        return userKey;
    }
}

package app.tests.performance;

import javax.annotation.Nullable;

import org.hyperledger.fabric.gateway.Contract;

import app.tests.Testable;
import app.tests.util.Logger;
import app.user.AnonymousService;
import app.user.ServiceProvider;

public class PostKeysFetchingTests implements Testable {
    private final Contract contract;
    private AnonymousService user = null;
    private final String userKey;

    @Override
    public String testName() {
        return "PostKeysFetchingTests";
    }

    public PostKeysFetchingTests(final Contract contract, final @Nullable String userKey) {
        this.userKey = userKey;
        this.contract = contract;
    }

    @Override
    public boolean pre(final Logger logger, final int numberIteration) {
        this.user = ServiceProvider.createAnonymousService(this.contract);

        return true;
    }

    @Override
    public boolean runTest(final Logger logger, final int currentIteration, final int numberIteration) {
        if (this.userKey == null) {
            final var result = this.user.fetchPostKeys();
            logger.printResult(result != null ? String.valueOf(result.length) : "null");
        } else {
            final var result = this.user.fetchPostKeysByUserId(this.userKey);
            logger.printResult(result != null ? String.valueOf(result.length) : "null");
        }

        return true;
    }
}

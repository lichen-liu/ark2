package app.tests.performance;

import org.hyperledger.fabric.gateway.Contract;

import app.tests.Testable;
import app.tests.util.Logger;
import app.user.AnonymousService;
import app.user.ServiceProvider;

public class PostsFetchingTest implements Testable {
    private final Contract contract;
    private AnonymousService user = null;
    private final String userKey;

    @Override
    public String testName() {
        return "PostsFetchingTest";
    }

    public PostsFetchingTest(final Contract contract, final String userKey) {
        this.contract = contract;
        this.userKey = userKey;
    }

    @Override
    public boolean pre(final Logger logger, final int numberIteration) {
        this.user = ServiceProvider.createAnonymousService(this.contract);

        return true;
    }

    @Override
    public boolean runTest(final Logger logger, final int currentIteration, final int numberIteration) {
        final var userPostKeys = this.user.fetchPostKeysByUserId(this.userKey);
        boolean isValid = true;
        for (final var userPostKey : userPostKeys) {
            if (!this.user.verifyPost(userPostKey, null).isValid()) {
                isValid = false;
            }
        }
        logger.printResult(isValid ? "Valid" : "Invalid");

        return true;
    }

}

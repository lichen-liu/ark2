package app.tests.performance;

import app.tests.Test;
import app.tests.util.Logger;
import app.tests.util.Logger.Builder;

public class DislikePublishingTests implements Test {

    @Override
    public Logger initLogger(final Builder builder) {
        return builder.create("DislikePublishingTests");
    }

    @Override
    public boolean runTest(final Logger logger, final int currentIteration) {
        return true;
    }

}

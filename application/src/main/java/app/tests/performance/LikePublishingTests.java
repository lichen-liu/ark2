package app.tests.performance;

import app.tests.Test;
import app.tests.util.Logger;
import app.tests.util.Logger.Builder;

public class LikePublishingTests implements Test {

    @Override
    public Logger initLogger(final Builder builder) {
        return builder.create("LikePublishingTests");
    }

    @Override
    public boolean runTest(final Logger logger, final int currentIteration) {
        return true;
        // TODO Auto-generated method stub

    }

}

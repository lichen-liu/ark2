package app.tests.performance;

import app.tests.Test;
import app.tests.util.Logger;

public class DislikePublishingTests implements Test {

    @Override
    public String testName() {
        return "DislikePublishingTests";
    }

    @Override
    public boolean runTest(final Logger logger, final int currentIteration) {
        return true;
    }

}

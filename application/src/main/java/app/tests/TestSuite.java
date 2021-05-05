package app.tests;

import java.util.List;

import app.tests.util.Logger;

public abstract class TestSuite {
    private final Logger.Builder loggerBuilder;

    protected TestSuite() {
        this("defaultTestSuite");
    }

    protected TestSuite(final String suiteName) {
        this.loggerBuilder = new Logger.Builder(suiteName);
    }

    public void launchTests() {
        init();

        run();

        finit();
    }

    protected void init() {
    }

    protected void finit() {
    }

    protected void preTestIterations(final Logger logger, final String testSessionName, final int maxNumberIterations) {
    }

    protected void postTestIterations(final Logger logger, final String testSessionName, final int maxNumberIterations,
            final int iteration) {
    }

    protected void preTestRun(final Logger logger, final String testSessionName, final int maxNumberIterations,
            final int currentIteration) {
    }

    protected void postTestRun(final Logger logger, final String testSessionName, final int maxNumberIterations,
            final int currentIteration) {
    }

    protected void run() {
        final var tests = setUpTests();
        for (final var test : tests) {
            final Logger logger = this.loggerBuilder.create(test.testName());
            final String testSessionName = logger.sessionName();
            final int numIterations = test.numberIterations();

            if (!test.pre(logger)) {
                logger.print("exit in pre");
                return;
            }

            boolean shouldContinue = true;
            int iteration = 0;

            preTestIterations(logger, testSessionName, numIterations);
            for (; iteration < numIterations && shouldContinue; iteration++) {
                preTestRun(logger, testSessionName, numIterations, iteration);
                shouldContinue = test.runTest(logger, iteration);
                postTestRun(logger, testSessionName, numIterations, iteration);
            }
            postTestIterations(logger, testSessionName, numIterations, iteration);

            if (!shouldContinue) {
                logger.print("exit in runTest");
                return;
            }

            if (!test.post(logger)) {
                logger.print("exit in post");
                return;
            }
        }
    }

    protected abstract List<? extends Test> setUpTests();
}

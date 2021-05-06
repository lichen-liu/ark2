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

    protected int defaultIterations() {
        return 1;
    }

    public void launchTests() {
        pre();

        run();

        post();
    }

    protected void pre() {
    }

    protected void post() {
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
            final int plannedNumberIterations = defaultIterations();
            final int numberIterations = test.requestNumberIterations(plannedNumberIterations)
                    .orElse(plannedNumberIterations);

            if (!test.pre(logger, numberIterations)) {
                logger.print("exit in pre");
                return;
            }

            boolean shouldContinue = true;
            int iteration = 0;

            preTestIterations(logger, testSessionName, numberIterations);
            for (; iteration < numberIterations && shouldContinue; iteration++) {
                preTestRun(logger, testSessionName, numberIterations, iteration);
                shouldContinue = test.runTest(logger, iteration, numberIterations);
                postTestRun(logger, testSessionName, numberIterations, iteration);
            }
            postTestIterations(logger, testSessionName, numberIterations, iteration);

            if (!shouldContinue) {
                logger.print("exit in runTest");
                return;
            }

            if (!test.post(logger, numberIterations)) {
                logger.print("exit in post");
                return;
            }
        }
    }

    protected abstract List<? extends Testable> setUpTests();
}

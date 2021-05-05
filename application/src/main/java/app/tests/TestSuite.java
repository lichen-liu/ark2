package app.tests;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;

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
        launchTestsImplementation();
    }

    protected final void launchTestsImplementation() {
        final var tests = setUpTests();
        for (final var test : tests) {
            final String testName = test.testName();
            final Logger logger = this.loggerBuilder.create(testName);
            final int numIterations = test.numberIterations();

            if (!test.pre(logger)) {
                logger.print("exit in pre");
                return;
            }

            boolean shouldContinue = true;
            int iteration = 0;
            final Stopwatch timer = Stopwatch.createStarted();
            for (; iteration < numIterations && shouldContinue; iteration++) {
                shouldContinue = test.runTest(logger, iteration);
            }
            timer.stop();
            logger.print(iteration + " Iterations / " + numIterations + " Total Iterations: "
                    + timer.elapsed(TimeUnit.MILLISECONDS) + "ms");

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

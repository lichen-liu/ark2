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
            final Logger generalLogger = this.loggerBuilder.create(testName + ":general");
            final int numIterations = test.numberIterations();

            if (!test.pre(generalLogger)) {
                generalLogger.print("exit in pre");
                return;
            }

            for (int i = 0; i < numIterations; i++) {
                final Logger logger = this.loggerBuilder.create(testName);
                if (!test.preTest(logger, i)) {
                    logger.print("Iteration " + i + ": exit in preTest");
                    return;
                }

                final Stopwatch timer = Stopwatch.createStarted();
                final boolean shouldContinue = test.runTest(logger, i);
                timer.stop();
                logger.print("Iteration " + i + ": " + timer.elapsed(TimeUnit.MILLISECONDS) + "ms");

                if (!shouldContinue) {
                    logger.print("Iteration " + i + ": exit in runTest");
                    return;
                }

                if (!test.postTest(logger, i)) {
                    logger.print("Iteration " + i + ": exit in postTest");
                    return;
                }
            }

            if (!test.post(generalLogger)) {
                generalLogger.print("exit in post");
                return;
            }
        }
    }

    protected abstract List<? extends Test> setUpTests();
}

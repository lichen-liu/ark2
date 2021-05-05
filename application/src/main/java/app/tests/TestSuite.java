package app.tests;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;

import app.tests.util.Logger;

public abstract class TestSuite {
    private final String suiteName;
    private final Logger.Builder loggerBuilder;

    protected TestSuite() {
        this("defaultTestSuite");
    }

    protected TestSuite(final String suiteName) {
        this.suiteName = suiteName;
        this.loggerBuilder = new Logger.Builder(suiteName);
    }

    public void launchTests() {
        launchTestsImplementation();
    }

    protected final void launchTestsImplementation() {
        final var tests = setUpTests();
        for (final var test : tests) {
            final Logger logger = test.initLogger(loggerBuilder);
            final int numIterations = test.numberIterations();

            if (!test.pre(logger)) {
                System.out.println(suiteName + ": exit in pre");
                return;
            }

            for (int i = 0; i < numIterations; i++) {
                if (!test.preTest(logger, i)) {
                    System.out.println(suiteName + ": Iteration " + i + ": exit in preTest");
                    return;
                }

                final Stopwatch timer = Stopwatch.createStarted();

                final boolean shouldContinue = test.runTest(logger, i);

                timer.stop();
                System.out.println(suiteName + ": Iteration " + i + ": " + timer.elapsed(TimeUnit.MILLISECONDS));
                if (!shouldContinue) {
                    System.out.println(suiteName + ": Iteration " + i + ": exit in runTest");
                    return;
                }

                if (!test.postTest(logger, i)) {
                    System.out.println(suiteName + ": Iteration " + i + ": exit in postTest");
                    return;
                }
            }

            if (!test.post(logger)) {
                System.out.println(suiteName + ": exit in post");
                return;
            }
        }
    }

    protected abstract List<? extends Test> setUpTests();
}

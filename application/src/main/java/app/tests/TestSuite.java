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
            test.preTest(logger);

            final Stopwatch timer = Stopwatch.createStarted();

            test.runTest(logger);

            timer.stop();
            System.out.println(suiteName + ": " + timer.elapsed(TimeUnit.MILLISECONDS));

            test.postTest(logger);
        }
    }

    protected abstract List<? extends Test> setUpTests();
}

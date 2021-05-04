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

    public final void launchTests() {
        final var tests = setUpTests();
        for (final var test : tests) {
            final Logger logger = test.initLogger(loggerBuilder);
            test.preTest(logger);
            test.runTest(logger);
            test.postTest(logger);
        }
    }

    protected abstract <T extends Test> List<T> setUpTests();
}

package app.tests;

import app.tests.util.Logger;

public interface Test {
    public default Logger initLogger(final Logger.Builder builder) {
        return null;
    }

    public default void preTest(final Logger logger) {
    }

    public abstract void runTest(Logger logger);

    public default void postTest(final Logger logger) {
    }
}

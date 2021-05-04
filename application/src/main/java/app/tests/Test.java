package app.tests;

import app.tests.util.Logger;

public interface Test {
    /**
     * Create a Logger from the Logger.Builder
     * 
     * @param builder
     * @return
     */
    public abstract Logger initLogger(final Logger.Builder builder);

    /**
     * Optional
     * 
     * @param logger
     */
    public default void preTest(final Logger logger) {
    }

    /**
     * Test to run
     * 
     * @param logger
     */
    public abstract void runTest(Logger logger);

    /**
     * Optional
     * 
     * @param logger
     */
    public default void postTest(final Logger logger) {
    }
}

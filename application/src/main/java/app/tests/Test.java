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
     * @return boolean, false to abort
     */
    public default boolean preTest(final Logger logger) {
        return true;
    }

    /**
     * Test to run
     * 
     * @param logger
     * @return boolean, false to abort
     */
    public abstract boolean runTest(Logger logger);

    /**
     * Optional
     * 
     * @param logger
     * @return boolean, false to abort
     */
    public default boolean postTest(final Logger logger) {
        return true;
    }
}

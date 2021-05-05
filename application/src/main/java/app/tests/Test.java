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
     * @return
     */
    public default int numberIterations() {
        return 1;
    }

    /**
     * Optional
     * 
     * @param logger
     * @return boolean, false to abort
     */
    public default boolean pre(final Logger logger) {
        return true;
    }

    /**
     * Optional
     * 
     * @param logger
     * @return boolean, false to abort
     */
    public default boolean post(final Logger logger) {
        return true;
    }

    /**
     * Optional
     * 
     * @param logger
     * @param currentIteration
     * @return boolean, false to abort
     */
    public default boolean preTest(final Logger logger, final int currentIteration) {
        return true;
    }

    /**
     * Test to run
     * 
     * @param logger
     * @param currentIteration
     * @return boolean, false to abort
     */
    public abstract boolean runTest(Logger logger, final int currentIteration);

    /**
     * Optional
     * 
     * @param logger
     * @param currentIteration
     * @return boolean, false to abort
     */
    public default boolean postTest(final Logger logger, final int currentIteration) {
        return true;
    }
}

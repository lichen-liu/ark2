package app.tests;

import app.tests.util.Logger;

public interface Test {
    /**
     * Required. The name of the test
     * 
     * Called once
     * 
     * @return
     */
    public abstract String testName();

    /**
     * Optional
     * 
     * Called once
     * 
     * @return
     */
    public default int numberIterations() {
        return 1;
    }

    /**
     * Optional
     * 
     * Called once
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
     * Called once
     * 
     * @param logger
     * @return boolean, false to abort
     */
    public default boolean post(final Logger logger) {
        return true;
    }

    /**
     * Required. Test to run
     * 
     * Called once per iteration
     * 
     * @param logger
     * @param currentIteration
     * @return boolean, false to abort
     */
    public abstract boolean runTest(Logger logger, final int currentIteration);
}

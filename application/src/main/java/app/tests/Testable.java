package app.tests;

import java.util.Optional;

import app.tests.util.Logger;

@FunctionalInterface
public interface Testable {
    /**
     * Optional. The name of the test.
     * 
     * Called once
     * 
     * @return, Default to be the name of the Testable implementation Class
     */
    public default String testName() {
        String name = this.getClass().getSimpleName();
        if ("".equals(name)) {
            name = this.getClass().getName();
        }
        return name;
    }

    /**
     * Optional. Bypass the default number of iterations from Test executioner.
     * 
     * Called once
     * 
     * @return Optional.empty() if using the default value from the Test executioner
     */
    public default Optional<Integer> requestNumberIterations() {
        return Optional.empty();
    }

    /**
     * Optional. pre() is invoked much later than the constructor. For delayed
     * setups
     * 
     * Called once
     * 
     * @param logger
     * @param numberIteration
     * @return boolean, false to abort
     */
    public default boolean pre(final Logger logger, final int numberIteration) {
        return true;
    }

    /**
     * Optional
     * 
     * Called once
     * 
     * @param logger
     * @param numberIteration
     * @return boolean, false to abort
     */
    public default boolean post(final Logger logger, final int numberIteration) {
        return true;
    }

    /**
     * Required. Test to run
     * 
     * Called once per iteration
     * 
     * @param logger
     * @param currentIteration
     * @param numberIteration
     * @return boolean, false to abort
     */
    public abstract boolean runTest(Logger logger, final int currentIteration, final int numberIteration);
}

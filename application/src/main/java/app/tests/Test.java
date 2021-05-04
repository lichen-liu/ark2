package app.tests;

import app.tests.util.Logger;

public abstract class Test {
    protected final Logger logger;

    protected Test() {
        this.logger = new Logger();
    }

    protected Test(final String name) {
        this.logger = new Logger(name);
    }

    protected Test(final String name, final int subname) {
        this.logger = new Logger(name, subname);
    }

    protected Test(final Logger logger) {
        this.logger = logger;
    }

    public abstract void runTest() throws Exception;
}

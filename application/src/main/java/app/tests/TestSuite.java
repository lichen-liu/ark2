package app.tests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;

import app.tests.util.Logger;

public abstract class TestSuite {
    private final Logger.Builder loggerBuilder;
    private final Map<String, Integer> testIterations = new HashMap<String, Integer>();
    private final Map<String, Long> testElapsedMilliSeconds = new HashMap<String, Long>();
    private final List<String> testSessionNames = new ArrayList<String>();

    protected TestSuite() {
        this("defaultTestSuite");
    }

    protected TestSuite(final String suiteName) {
        this.loggerBuilder = new Logger.Builder(suiteName);
    }

    public void launchTests() {
        runTests();
        processRuntimeStats();
    }

    protected final void runTests() {
        final var tests = setUpTests();
        for (final var test : tests) {
            final Logger logger = this.loggerBuilder.create(test.testName());
            final String testSessionName = logger.sessionName();
            final int numIterations = test.numberIterations();

            if (!test.pre(logger)) {
                logger.print("exit in pre");
                return;
            }

            boolean shouldContinue = true;
            int iteration = 0;
            final Stopwatch timer = Stopwatch.createStarted();
            for (; iteration < numIterations && shouldContinue; iteration++) {
                shouldContinue = test.runTest(logger, iteration);
            }
            timer.stop();
            final long timeElapsedMS = timer.elapsed(TimeUnit.MILLISECONDS);

            this.testSessionNames.add(testSessionName);
            this.testIterations.put(testSessionName, iteration);
            this.testElapsedMilliSeconds.put(testSessionName, timeElapsedMS);

            logger.print(iteration + " Iterations / " + numIterations + " Total Iterations: " + timeElapsedMS + "ms");

            if (!shouldContinue) {
                logger.print("exit in runTest");
                return;
            }

            if (!test.post(logger)) {
                logger.print("exit in post");
                return;
            }
        }
    }

    protected final void processRuntimeStats() {
        System.out.println("\n===================================================");
        for (final var testSessionName : this.testSessionNames) {
            final int iterations = this.testIterations.get(testSessionName);
            final long ms = this.testElapsedMilliSeconds.get(testSessionName);
            System.out.println(testSessionName + ": " + iterations + " Iterations: " + ms + " ms: " + ms / iterations
                    + " ms/iteration");
        }
        System.out.println("===================================================\n");
    }

    protected abstract List<? extends Test> setUpTests();
}

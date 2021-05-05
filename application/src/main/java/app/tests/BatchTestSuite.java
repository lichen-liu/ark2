package app.tests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;

import app.tests.util.Logger;

public abstract class BatchTestSuite extends TestSuite {
    private final Map<String, Integer> testIterations = new HashMap<String, Integer>();
    private final Map<String, Long> testElapsedMilliSeconds = new HashMap<String, Long>();
    private final List<String> testSessionNames = new ArrayList<String>();

    private Stopwatch timer = null;

    protected BatchTestSuite() {
    }

    protected BatchTestSuite(final String suiteName) {
        super(suiteName + "Batch");
    }

    @Override
    protected void finit() {
        System.out.println("\n===================================================");
        for (final var testSessionName : this.testSessionNames) {
            final int iterations = this.testIterations.get(testSessionName);
            final long ms = this.testElapsedMilliSeconds.get(testSessionName);
            System.out.println(testSessionName + ": " + iterations + " Iterations: " + ms + " ms: " + ms / iterations
                    + " ms/iteration");
        }
        System.out.println("===================================================\n");
    }

    @Override
    protected void preTestIterations(final Logger logger, final String testSessionName, final int maxNumberIterations) {
        timer = Stopwatch.createStarted();
    }

    @Override
    protected void postTestIterations(final Logger logger, final String testSessionName, final int maxNumberIterations,
            final int iteration) {
        timer.stop();
        final long timeElapsedMS = timer.elapsed(TimeUnit.MILLISECONDS);

        this.testSessionNames.add(testSessionName);
        this.testIterations.put(testSessionName, iteration);
        this.testElapsedMilliSeconds.put(testSessionName, timeElapsedMS);
        logger.print(iteration + " Iterations / " + maxNumberIterations + " Total Iterations: " + timeElapsedMS + "ms");
    }
}

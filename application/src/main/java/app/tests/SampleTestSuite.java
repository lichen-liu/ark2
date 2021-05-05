package app.tests;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.base.Stopwatch;

import app.tests.util.Logger;

public abstract class SampleTestSuite extends TestSuite {
    private final LinkedHashMap<String, List<Long>> testElapsedMilliSeconds = new LinkedHashMap<String, List<Long>>();

    private Stopwatch timer = null;

    protected SampleTestSuite() {
    }

    protected SampleTestSuite(final String suiteName) {
        super(suiteName + "Sample");
    }

    @Override
    protected void preTestRun(final Logger logger, final String testSessionName, final int maxNumberIterations,
            final int currentIteration) {
        timer = Stopwatch.createStarted();
    }

    @Override
    protected void postTestRun(final Logger logger, final String testSessionName, final int maxNumberIterations,
            final int currentIteration) {
        timer.stop();
        final long timeElapsedMS = timer.elapsed(TimeUnit.MILLISECONDS);

        this.testElapsedMilliSeconds.computeIfAbsent(testSessionName, key -> new ArrayList<Long>());
        this.testElapsedMilliSeconds.computeIfPresent(testSessionName, (key, list) -> {
            list.add(timeElapsedMS);
            return list;
        });
    }

    @Override
    protected void finit() {
        System.out.println("\n===================================================");
        for (final var testSessionEntry : this.testElapsedMilliSeconds.entrySet()) {
            final var testSessionName = testSessionEntry.getKey();
            final var elapseds = testSessionEntry.getValue();
            final int iterations = elapseds.size();
            final long totalMs = elapseds.stream().mapToLong(Long::valueOf).sum();
            final long minMs = elapseds.stream().mapToLong(Long::valueOf).min().orElse(0L);
            final long maxMs = elapseds.stream().mapToLong(Long::valueOf).max().orElse(0L);
            System.out.println(testSessionName + ": " + iterations + " Iterations: " + totalMs + " ms: "
                    + totalMs / iterations + " ms/iteration: " + minMs + " min ms: " + maxMs + " max ms");
        }
        System.out.println("===================================================\n");

        final List<String> csvData = this.testElapsedMilliSeconds.entrySet().stream()
                .map(entry -> entry.getValue().stream().map(elapsed -> entry.getKey() + "," + elapsed.toString()))
                .flatMap(Function.identity()).collect(Collectors.toList());

        for (final var row : csvData) {
            System.out.println(row);
        }
    }
}

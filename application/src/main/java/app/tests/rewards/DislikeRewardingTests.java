package app.tests.rewards;

import org.hyperledger.fabric.gateway.Contract;

import app.tests.Testable;
import app.tests.simulation.OnePostManyDislikeSimulationTests;
import app.tests.simulation.Simulation;
import app.tests.util.Logger;

public class DislikeRewardingTests implements Testable {
    private Simulation onePostManyDislikeSimulation;
    private final int iterations;
    private final Contract contract;

    @Override
    public String testName() {
        return "DislikeRewardingTests";
    }

    public DislikeRewardingTests(final Contract contract, final int iterations) {
        this.contract = contract;
        this.iterations = iterations;
    }

    @Override
    public int numberIterations() {
        return iterations;
    }

    @Override
    public boolean pre(final Logger logger) {
        try {
            onePostManyDislikeSimulation = new OnePostManyDislikeSimulationTests(contract);
        } catch (final Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean runTest(final Logger logger, final int currentIteration) {
        onePostManyDislikeSimulation.runTest();
        // Why false?
        return false;
    }
}

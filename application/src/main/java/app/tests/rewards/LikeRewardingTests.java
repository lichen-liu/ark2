package app.tests.rewards;

import org.hyperledger.fabric.gateway.Contract;

import app.tests.Testable;
import app.tests.simulation.OnePostManyLikeSimulationTests;
import app.tests.simulation.Simulation;
import app.tests.util.Logger;

public class LikeRewardingTests implements Testable {
    private Simulation onePostManyLikeSimulation;
    private final int iterations;

    private final Contract contract;

    @Override
    public String testName() {
        return "LikeRewardingTests";
    }

    public LikeRewardingTests(final Contract contract, final int iterations) {
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
            onePostManyLikeSimulation = new OnePostManyLikeSimulationTests(contract);
        } catch (final Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean runTest(final Logger logger, final int currentIteration) {
        onePostManyLikeSimulation.runTest();
        // Why false?
        return false;
    }

}

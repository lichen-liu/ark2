package app.tests.rewards;

import org.hyperledger.fabric.gateway.Contract;

import app.tests.Testable;
import app.tests.simulation.OnePostManyDislikeSimulationTests;
import app.tests.simulation.Simulation;
import app.tests.util.Logger;

public class DislikeRewardingTest implements Testable {
    private Simulation onePostManyDislikeSimulation;
    private final Contract contract;

    public DislikeRewardingTest(final Contract contract) {
        this.contract = contract;
    }

    @Override
    public boolean pre(final Logger logger, final int numberIteration) {
        try {
            onePostManyDislikeSimulation = new OnePostManyDislikeSimulationTests(contract);
        } catch (final Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean runTest(final Logger logger, final int currentIteration, final int numberIteration) {
        onePostManyDislikeSimulation.runTest();
        // Why false?
        return false;
    }
}

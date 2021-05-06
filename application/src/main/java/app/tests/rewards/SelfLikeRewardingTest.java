package app.tests.rewards;

import org.hyperledger.fabric.gateway.Contract;

import app.tests.Testable;
import app.tests.simulation.OnePostManyDislikeSimulationTests;
import app.tests.simulation.SelfLikePostSimulationTests;
import app.tests.simulation.Simulation;
import app.tests.util.Logger;

public class SelfLikeRewardingTest implements Testable {
    private Simulation selfLikePostSimulationTests;
    private final Contract contract;

    public SelfLikeRewardingTest(final Contract contract) {
        this.contract = contract;
    }

    @Override
    public boolean pre(final Logger logger, final int numberIteration) {
        try {
            selfLikePostSimulationTests = new SelfLikePostSimulationTests(contract);
        } catch (final Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean runTest(final Logger logger, final int currentIteration, final int numberIteration) {
        selfLikePostSimulationTests.runTest();
        return true;
    }

    @Override
    public boolean post(final Logger logger, final int currentIteration) {
        selfLikePostSimulationTests.finish();
        return true;
    }
}

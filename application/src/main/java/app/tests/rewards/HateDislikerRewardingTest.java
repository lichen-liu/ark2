package app.tests.rewards;

import org.hyperledger.fabric.gateway.Contract;

import app.tests.Testable;
import app.tests.simulation.HateDislikerSimulationTests;
import app.tests.simulation.Simulation;
import app.tests.util.Logger;

public class HateDislikerRewardingTest implements Testable {
    private Simulation hateDislikerSimulationTests;
    private final Contract contract;

    public HateDislikerRewardingTest(final Contract contract) {
        this.contract = contract;
    }

    @Override
    public boolean pre(final Logger logger, final int numberIteration) {
        try {
            hateDislikerSimulationTests = new HateDislikerSimulationTests(contract);
        } catch (final Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean runTest(final Logger logger, final int currentIteration, final int numberIteration) {
        hateDislikerSimulationTests.runTest(logger);
        return true;
    }

    @Override
    public boolean post(final Logger logger, final int currentIteration) {
        hateDislikerSimulationTests.finish();
        return true;
    }
}

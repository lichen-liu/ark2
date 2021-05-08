package app.tests.rewards;

import java.util.List;

import org.hyperledger.fabric.gateway.Contract;

import app.tests.Testable;
import app.tests.simulation.OnePostManyLikeSimulationTests;
import app.tests.simulation.Simulation;
import app.tests.util.Logger;

public class LikeRewardingTest implements Testable {
    private Simulation onePostManyLikeSimulation;

    private final Contract contract;

    public LikeRewardingTest(final Contract contract) {
        this.contract = contract;
    }

    @Override
    public boolean pre(final Logger logger, final int numberIteration) {
        try {
            onePostManyLikeSimulation = new OnePostManyLikeSimulationTests(contract);
        } catch (final Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean runTest(final Logger logger, final int currentIteration, final int numberIteration) {
        onePostManyLikeSimulation.runTest(logger);
        return true;
    }

    @Override
    public boolean post(final Logger logger, final int currentIteration) {
        // onePostManyLikeSimulation.finish();
        onePostManyLikeSimulation.saveLikerPointBalanceHistory(List.of(1, 5, 10, 25, 50));
        onePostManyLikeSimulation.saveAuthorPointBalanceHistory(List.of(0));
        onePostManyLikeSimulation.saveWorldPointBalanceHistory();
        return true;
    }
}

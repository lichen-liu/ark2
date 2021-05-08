package app.tests.rewards;

import java.util.List;

import org.hyperledger.fabric.gateway.Contract;

import app.tests.Testable;
import app.tests.simulation.RealisticLikeDislikeSimulation;
import app.tests.simulation.Simulation;
import app.tests.util.Logger;

public class RealisticLikeDislikeRewardingTest implements Testable {
    private Simulation realisticLikeDislikeSimulation;
    private final Contract contract;

    public RealisticLikeDislikeRewardingTest(final Contract contract) {
        this.contract = contract;
    }

    @Override
    public boolean pre(final Logger logger, final int numberIteration) {
        try {
            realisticLikeDislikeSimulation = new RealisticLikeDislikeSimulation(contract);
        } catch (final Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean runTest(final Logger logger, final int currentIteration, final int numberIteration) {
        realisticLikeDislikeSimulation.runTest(logger);
        return true;
    }

    @Override
    public boolean post(final Logger logger, final int currentIteration) {
        realisticLikeDislikeSimulation.finish();
        realisticLikeDislikeSimulation.saveLikerPointBalanceHistory(List.of(1, 5, 10, 25, 50));
        realisticLikeDislikeSimulation.saveAuthorPointBalanceHistory(List.of(0));
        realisticLikeDislikeSimulation.saveWorldPointBalanceHistory();
        return true;
    }
}

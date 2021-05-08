package app.tests.rewards;

import org.hyperledger.fabric.gateway.Contract;

import app.tests.Testable;
import app.tests.simulation.RealisticLikeDislikeSimulation;
import app.tests.util.Logger;

public class RealisticLikeDislikeRewardingTest implements Testable {
    private RealisticLikeDislikeSimulation realisticLikeDislikeSimulation;
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
        // realisticLikeDislikeSimulation.finish();

        final var authorKeys = realisticLikeDislikeSimulation.getInterestingAuthorKeys(3, 3);
        int count = 0;
        for (final var authorKey : authorKeys) {
            realisticLikeDislikeSimulation.saveCSVUserPointBalanceHistory("author" + count + ".csv", authorKey);
            count++;
        }

        realisticLikeDislikeSimulation.saveWorldPointBalanceHistory();
        return true;
    }
}

package app.tests.rewards;

import java.util.List;
import java.util.stream.Collectors;

import org.hyperledger.fabric.gateway.Contract;

import app.service.AnonymousAnalysisService.PointBalanceSnapshot;
import app.service.ServiceProvider;
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
        onePostManyLikeSimulation.runTest();
        return true;
    }

    @Override
    public boolean post(final Logger logger, final int currentIteration) {
        onePostManyLikeSimulation.finish();

        var likeHistory = onePostManyLikeSimulation.internalState.getLikeHistory();

        var OneLiker = (int) Math.ceil(likeHistory.size() * 0.01);
        var TwentyFiveLiker = (int) Math.ceil(likeHistory.size() * 0.25);
        var FiftyLiker = (int) Math.ceil(likeHistory.size() * 0.5);

        final List<String> OneLikerCsvData = ServiceProvider.createAnonymousAnalysisService(this.contract)
                .analyzePointBalanceHistoryByUserId(likeHistory.get(OneLiker).Item1).stream().map(snapshot -> snapshot.toCsvRow())
                .collect(Collectors.toList());

        final List<String> TwentyFiveCsvData = ServiceProvider.createAnonymousAnalysisService(this.contract)
                .analyzePointBalanceHistoryByUserId(likeHistory.get(TwentyFiveLiker).Item1).stream().map(snapshot -> snapshot.toCsvRow())
                .collect(Collectors.toList());

        final List<String> FiftyLikerCsvData = ServiceProvider.createAnonymousAnalysisService(this.contract)
                .analyzePointBalanceHistoryByUserId(likeHistory.get(FiftyLiker).Item1).stream().map(snapshot -> snapshot.toCsvRow())
                .collect(Collectors.toList());

        return true;

        return true;
    }
}

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

        final List<String> worldEconomyCsvData = ServiceProvider.createAnonymousAnalysisService(this.contract)
                .analyzePointBalanceHistoryByUserId(null).stream().map(PointBalanceSnapshot::toCsvRow)
                .collect(Collectors.toList());
        worldEconomyCsvData.add(0, PointBalanceSnapshot.CsvRowTitle());
        for (final var row : worldEconomyCsvData) {
            System.out.println(row);
        }

        return true;
    }
}

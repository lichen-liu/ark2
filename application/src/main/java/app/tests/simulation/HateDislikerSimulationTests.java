package app.tests.simulation;

import org.hyperledger.fabric.gateway.Contract;

import app.tests.simulation.SimulationState.Tuple;
import app.tests.util.Logger;

public class HateDislikerSimulationTests extends Simulation {
    public HateDislikerSimulationTests(final Contract contract) throws Exception {
        super(contract);
    };

    @Override
    public void runTest(final Logger logger) {
        // Run tests - Just do a lot of likes to the economic system
        String key = null;
        do {
            key = triggerADislike();
            logger.printResult(key);
        } while (key == null);
    }

    @Override
    protected void buildState(final SimulationState state) throws Exception {
        // Build forum state
        state.authors = state.createClients(1);
        state.likers = state.createClients(1);

        final var thepost = state.authors.get(0).publishNewPost("post1");

        if (thepost == null)
            throw new Exception("post is null");

        state.posts.add(thepost);

        state.postHistory.add(new Tuple<String, String>(state.authors.get(0).getPublicKeyString(), thepost));

        state.postProbMap.put(state.posts.get(0), 100);

        state.likerProbMap.put(state.likers.get(0), 1);

        final var it = state.postProbMap.entrySet().iterator();
        while (it.hasNext()) {
            final var pair = it.next();
            state.postPool.addItem(pair.getKey(), pair.getValue());
        }

        final var it2 = state.likerProbMap.entrySet().iterator();
        while (it2.hasNext()) {
            final var pair = it2.next();
            state.likerPool.addItem(pair.getKey(), pair.getValue());
        }
    }
}

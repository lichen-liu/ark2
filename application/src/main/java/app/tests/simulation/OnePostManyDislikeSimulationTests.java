package app.tests.simulation;

import org.hyperledger.fabric.gateway.Contract;

import app.tests.simulation.SimulationState.Policy;
import app.tests.simulation.SimulationState.Tuple;
import app.tests.util.Logger;

public class OnePostManyDislikeSimulationTests extends Simulation {

    public OnePostManyDislikeSimulationTests(final Contract contract) throws Exception {
        super(contract);
    };

    @Override
    public void runTest(final Logger logger) {
        // Run tests - Just do a lot of likes to the economic system
        String key = null;
        do {
            key = TriggerADislike();
            logger.printResult(key);
        } while (key == null);
    }

    @Override
    protected SimulationState getState() throws Exception {

        final var state = new SimulationState(this.contract, Policy.RoundRobin);

        // Build forum state
        state.authors = state.createClients(1);
        state.likers = state.createClients(1000);

        var thepost = state.authors.get(0).publishNewPost("post1");
        state.posts.add(thepost);

        state.postHistory.add(new Tuple<String, String>(state.authors.get(0).getPublicKeyString(), thepost));

        state.postProbMap.put(state.posts.get(0), 0);

        for (final var liker : state.likers) {
            state.likerProbMap.put(liker, 0);
        }

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

        return state;
    }
}

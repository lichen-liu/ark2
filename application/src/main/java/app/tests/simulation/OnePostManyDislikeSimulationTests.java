package app.tests.simulation;

import org.hyperledger.fabric.gateway.Contract;

public class OnePostManyDislikeSimulationTests extends Simulation {

    public OnePostManyDislikeSimulationTests(final Contract contract) throws Exception {
        super(contract);
    };

    @Override
    public void runTest() {
        // Run tests - Just do a lot of likes to the economic system
        while (true) {
            if (!TriggerADislike())
                continue;
            break;
        }

    }

    @Override
    protected SimulationState getState() throws Exception {

        final var state = new SimulationState(this.contract);

        // Build forum state
        state.authors = state.createClients(1);
        state.likers = state.createClients(1000);

        state.posts.add(state.authors.get(0).publishNewPost("post1"));

        state.postProbMap.put(state.posts.get(0), 100);

        for (final var liker : state.likers) {
            state.likerProbMap.put(liker, 1);
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

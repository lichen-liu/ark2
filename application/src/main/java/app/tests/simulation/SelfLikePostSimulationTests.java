package app.tests.simulation;

import org.hyperledger.fabric.gateway.Contract;

public class SelfLikePostSimulationTests extends Simulation {

    public SelfLikePostSimulationTests(final Contract contract) throws Exception {
        super(contract);
    };

    @Override
    public void runTest() {
        // Run tests - Just do a lot of likes to the economic system
        while (true) {
            if (!TriggerALike())
                continue;
            break;
        }
    }

    @Override
    protected SimulationState getState() throws Exception {

        final var state = new SimulationState(this.contract);

        // Build forum state
        state.authors = state.createClients(1);
        state.likers = state.authors;

        final var thepost = state.authors.get(0).publishNewPost("post1");

        if (thepost == null)
            throw new Exception("post is null");

        state.posts.add(thepost);

        state.postProbMap.put(state.posts.get(0), 100);

        state.likerProbMap.put(state.authors.get(0), 1);

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

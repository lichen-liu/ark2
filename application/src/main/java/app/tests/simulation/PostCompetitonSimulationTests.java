package app.tests.simulation;

public class PostCompetitonSimulationTests extends Simulation {

    public PostCompetitonSimulationTests() throws Exception {
        super();
    };

    @Override
    protected void runTest() {
        // Run tests - Just do a lot of likes to the economic system
        final var howManyLikesDoYouWantHuh = 20;
        int i = 0;
        while (i < howManyLikesDoYouWantHuh) {
            TriggerALike();
            ++i;
        }
    }

    @Override
    protected SimulationState getState() throws Exception {

        final var state = new SimulationState();

        // Build forum state
        state.authors = state.createClients(3);
        state.likers = state.createClients(7);

        state.posts.add(state.authors.get(0).publishNewPost("post1"));
        state.posts.add(state.authors.get(1).publishNewPost("post2"));
        state.posts.add(state.authors.get(2).publishNewPost("post3"));

        state.postProbMap.put(state.posts.get(0), 2);
        state.postProbMap.put(state.posts.get(1), 3);
        state.postProbMap.put(state.posts.get(2), 5);

        state.likerProbMap.put(state.likers.get(0), 1);
        state.likerProbMap.put(state.likers.get(1), 1);
        state.likerProbMap.put(state.likers.get(2), 5);
        state.likerProbMap.put(state.likers.get(3), 5);
        state.likerProbMap.put(state.likers.get(4), 20);
        state.likerProbMap.put(state.likers.get(5), 20);
        state.likerProbMap.put(state.likers.get(6), 48);

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

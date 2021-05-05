package app.tests.simulation;

import org.hyperledger.fabric.gateway.Contract;

public abstract class Simulation {
    private final SimulationWriter writer;
    public final SimulationState internalState;
    public final Contract contract;

    public Simulation(final Contract contract) throws Exception {
        this.writer = new SimulationWriter(this.getClass().getSimpleName());
        this.contract = contract;
        this.internalState = getState();
    }

    public Boolean TriggerALike() {
        final var postKey = internalState.postPool.draw();
        final var liker = internalState.likerPool.draw();
        if (liker.publishNewLike(postKey) == null) {
            return false;
        }
        ;

        return true;
    }

    public Boolean TriggerADislike() {
        final var postKey = internalState.postPool.draw();
        final var liker = internalState.likerPool.draw();
        if (liker.publishNewDislike(postKey) == null) {
            return false;
        }
        ;

        return true;
    }

    public Boolean TriggerANewPost(final int postProb) {
        final var author = internalState.authorPool.draw();
        final var postKey = author.publishNewPost("-");

        if (postKey == null) {
            return false;
        }

        internalState.posts.add(postKey);
        internalState.postProbMap.put(postKey, postProb);

        return true;
    }

    public void finish() {
        System.out.println("Test finished, writing benchmark states into the disk...");
        try {
            writer.SetTitle(this.getClass().getSimpleName());
            writer.saveAuthors(internalState.authors, internalState.authorProbMap);
            writer.saveLikers(internalState.likers, internalState.likerProbMap);
            writer.savePosts(internalState.posts, internalState.postProbMap);
        } catch (final Exception e) {
            e.printStackTrace();
        }

        writer.finish();
    };

    protected abstract SimulationState getState() throws Exception;

    public abstract void runTest();
}

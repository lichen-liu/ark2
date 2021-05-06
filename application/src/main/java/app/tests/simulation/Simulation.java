package app.tests.simulation;

import org.hyperledger.fabric.gateway.Contract;

public abstract class Simulation {
    private final SimulationWriter writer;
    public final SimulationState internalState;
    public final Contract contract;

    public Simulation(final Contract contract) throws Exception {
        this.contract = contract;
        this.internalState = getState();
        this.writer = new SimulationWriter(this.getClass().getSimpleName(), this.internalState);
    }

    public Boolean TriggerALike() {
        final var postKey = internalState.postPool.draw();
        final var liker = internalState.likerPool.draw();
        if (liker.publishNewLike(postKey) == null) {
            return false;
        };

        internalState.insertLikeHistory(liker.getPublicKeyString(), postKey);
        return true;
    }

    public Boolean TriggerADislike() {
        final var postKey = internalState.postPool.draw();
        final var liker = internalState.likerPool.draw();
        if (liker.publishNewDislike(postKey) == null) {
            return false;
        };

        internalState.insertDislikeHistory(liker.getPublicKeyString(), postKey);
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

        internalState.insertPostHistory(author.getPublicKeyString(), postKey);
        return true;
    }

    public void finish() {
        System.out.println("Test finished, writing benchmark states into the disk...");
        try {
            writer.SetTitle(this.getClass().getSimpleName());
            writer.saveAuthors(internalState.authors, internalState.authorProbMap);
            writer.saveLikers(internalState.likers, internalState.likerProbMap);
            writer.savePosts(internalState.posts, internalState.postProbMap);
            writer.saveLikeHistory();
            writer.saveDislikeHistory();
            writer.savePostHistory();
        } catch (final Exception e) {
            e.printStackTrace();
        }

        writer.finish();
    };

    protected abstract SimulationState getState() throws Exception;

    public abstract void runTest();
}

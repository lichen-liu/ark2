package app.tests.simulation;

public abstract class Simulation {
    private final SimulationWriter writer;
    private final SimulationState internalState;

    public Simulation() throws Exception {
        this.writer = new SimulationWriter(this.getClass().getSimpleName());
        this.internalState = getState();
    }

    public void TriggerALike() {
        final var postKey = internalState.postPool.draw();
        final var liker = internalState.likerPool.draw();
        liker.publishNewLike(postKey);
    }

    public void TriggerADislike() {
        final var postKey = internalState.postPool.draw();
        final var liker = internalState.likerPool.draw();
        liker.publishNewDislike(postKey);
    }

    public void TriggerANewPost(final int postProb) {
        final var author = internalState.authorPool.draw();
        final var postKey = author.publishNewPost("-");
        internalState.posts.add(postKey);
        internalState.postProbMap.put(postKey, postProb);
    }

    public void run() {
        runTest();

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

    protected abstract void runTest();
}

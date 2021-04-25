package app.tests.benchmarks;

public abstract class Benchmark {
    private BenchmarkWriter writer;
    private BenchmarkState internalState;

    public Benchmark() throws Exception{
        this.writer = new BenchmarkWriter(this.getClass().getSimpleName());
        this.internalState = getState();
    }

    public void TriggerALike(){
        var postKey = internalState.postPool.draw();
        var liker = internalState.likerPool.draw();
        liker.publishNewLike(postKey);
    }

    public void TriggerADislike(){
        var postKey = internalState.postPool.draw();
        var liker = internalState.likerPool.draw();
        liker.publishNewDislike(postKey); 
    }

    public void TriggerANewPost(int postProb){
        var author = internalState.authorPool.draw();
        var postKey = author.publishNewPost("-"); 
        internalState.posts.add(postKey);
        internalState.postProbMap.put(postKey, postProb);
    }

    public void run(){
        runTest();

        System.out.println("Test finished, writing benchmark states into the disk...");
        try{
            writer.SetTitle(this.getClass().getSimpleName());
            writer.saveAuthors(internalState.authors, internalState.authorProbMap);
            writer.saveLikers(internalState.likers, internalState.likerProbMap);
            writer.savePosts(internalState.posts, internalState.postProbMap);
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        writer.finish(); 
    };

    protected abstract BenchmarkState getState() throws Exception;

    protected abstract void runTest();
}

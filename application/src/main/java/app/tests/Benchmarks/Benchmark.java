package app.tests.Benchmarks;

public abstract class Benchmark extends BenchmarkState{
    private BenchmarkWriter writer;

    public Benchmark(){
        this.writer = new BenchmarkWriter(this.getClass().getSimpleName());
    }

    public void TriggerALike(){
        var postKey = postPool.draw();
        var liker = likerPool.draw();
        liker.publishNewLike(postKey);
    }

    public void TriggerADislike(){
        var postKey = postPool.draw();
        var liker = likerPool.draw();
        liker.publishNewDislike(postKey); 
    }

    public void TriggerANewPost(int postProb){
        var author = authorPool.draw();
        var postKey = author.publishNewPost("-"); 
        posts.add(postKey);
        postProbMap.put(postKey, postProb);
    }

    public void Run(){
        RunTest();

        System.out.println("Test finished, writing benchmark states into the disk...");
        try{
            writer.SetTitle(this.getClass().getSimpleName());
            writer.saveAuthors(authors, authorProbMap);
            writer.saveLikers(likers, likerProbMap);
            writer.savePosts(posts, postProbMap);
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        writer.finish(); 
    };

    protected abstract void RunTest();
}

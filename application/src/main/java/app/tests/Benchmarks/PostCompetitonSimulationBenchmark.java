package app.tests.benchmarks;
public class PostCompetitonSimulationBenchmark extends Benchmark {

    public PostCompetitonSimulationBenchmark() throws Exception {
        super();
    };

    @Override
    protected void runTest() {
        //Run tests - Just do a lot of likes to the economic system
        var howManyLikesDoYouWantHuh = 20;
        int i = 0;
        while(i < howManyLikesDoYouWantHuh){
            TriggerALike();
            ++ i;
        }
    }

    @Override 
    protected BenchmarkState getState() throws Exception{

        var state = new BenchmarkState();
    
        //Build forum state
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
    
        var it = state.postProbMap.entrySet().iterator();
        while (it.hasNext()) {
            var pair = it.next();
            state.postPool.addItem(pair.getKey(), pair.getValue());
        }
    
        var it2 = state.likerProbMap.entrySet().iterator();
        while (it2.hasNext()) {
            var pair = it2.next();
            state.likerPool.addItem(pair.getKey(), pair.getValue());
        }
        
        return state;
    }
}

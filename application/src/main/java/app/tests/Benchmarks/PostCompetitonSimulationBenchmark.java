package app.tests.Benchmarks;

import org.hyperledger.fabric.gateway.Contract;

public class PostCompetitonSimulationBenchmark extends Benchmark {

    public PostCompetitonSimulationBenchmark() {super();};

    @Override
    protected void RunTest() {
        Contract contract;
        try {
            contract = getContract();
        } catch(Exception e) {
            e.printStackTrace();
            return;
        }
    
        //Build forum state
        try {
            authors = createClients(contract , 3);
            likers = createClients(contract , 7);
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        posts.add(authors.get(0).publishNewPost("post1"));
        posts.add(authors.get(1).publishNewPost("post2"));
        posts.add(authors.get(2).publishNewPost("post3"));
    
        postProbMap.put(posts.get(0), 2);
        postProbMap.put(posts.get(1), 3);
        postProbMap.put(posts.get(2), 5);
    
        likerProbMap.put(likers.get(0), 1);
        likerProbMap.put(likers.get(1), 1);
        likerProbMap.put(likers.get(2), 5);
        likerProbMap.put(likers.get(3), 5);
        likerProbMap.put(likers.get(4), 20);
        likerProbMap.put(likers.get(5), 20);
        likerProbMap.put(likers.get(6), 48);
    
        var it = postProbMap.entrySet().iterator();
        while (it.hasNext()) {
            var pair = it.next();
            postPool.addItem(pair.getKey(), pair.getValue());
        }
    
        var it2 = likerProbMap.entrySet().iterator();
        while (it2.hasNext()) {
            var pair = it2.next();
            likerPool.addItem(pair.getKey(), pair.getValue());
        }  
    
        //Run tests - Just do a lot of likes to the economic system
        var howManyLikesDoYouWantHuh = 10;
        int i = 0;
        while(i < howManyLikesDoYouWantHuh){
            TriggerALike();
            ++ i;
        }
    }
}

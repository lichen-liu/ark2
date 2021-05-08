package app.tests.simulation;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.hyperledger.fabric.gateway.Contract;

import app.tests.util.Logger;

public class RealisticLikeDislikeSimulation extends Simulation {
    
    private Integer dice = 10;
    private Integer currentDice = 0;
    private Set<Integer> triggerPost = new HashSet<Integer> () {
        {
            add(0);
        }
    } ;
    private Set<Integer> triggerDislike = new HashSet<Integer> () {
        {
            add(4);
            add(7);
        }
    } ;

    public RealisticLikeDislikeSimulation(final Contract contract) throws Exception {
        super(contract);
    };

    private Random r = new Random();
    private double standardDeviation = 100.0;

    @Override
    public void runTest(final Logger logger) {
        // Run tests - Just do a lot of likes to the economic system
        String key = null;
        do {

            if(triggerPost.contains(currentDice)){
                Double pick = Math.abs(r.nextGaussian() * standardDeviation);
                key = TriggerANewPost(pick.intValue());
                logger.printResult(key);
            }else if(triggerDislike.contains(currentDice)) {
                key = TriggerADislike();
                logger.printResult(key);
            } else {
                key = TriggerALike();
                logger.printResult(key);
            } 

            currentDice = (++currentDice)%dice;

        } while (key == null);
    }

    @Override
    protected SimulationState getState() throws Exception {

        final var state = new SimulationState(this.contract);

        // Build forum state
        state.authors = state.createClients(100);
        state.likers = state.createClients(100);

        Random r = new Random();
        for (final var author : state.authors) {
            state.authorProbMap.put(author, 1);
        }

        final var it = state.authorProbMap.entrySet().iterator();
        while (it.hasNext()) {
            final var pair = it.next();
            state.authorPool.addItem(pair.getKey(), pair.getValue());
        }

        for (final var liker : state.likers) {
            Double pick = Math.abs(r.nextGaussian() * standardDeviation);
            state.likerProbMap.put(liker, pick.intValue());
        }

        final var it2 = state.likerProbMap.entrySet().iterator();
        while (it2.hasNext()) {
            final var pair = it2.next();
            state.likerPool.addItem(pair.getKey(), pair.getValue());
        }

        return state;
    }
}

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
    private Integer standardDeviation = 100;

    @Override
    public void runTest(final Logger logger) {
        // Run tests - Just do a lot of likes to the economic system
        String key = null;
        do {

            if(triggerPost.contains(currentDice)){
                key = TriggerANewPost((int)(r.nextGaussian() * standardDeviation));
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

        state.likerProbMap.put(state.likers.get(0), 1);

        final var it2 = state.likerProbMap.entrySet().iterator();
        while (it2.hasNext()) {
            final var pair = it2.next();
            state.likerPool.addItem(pair.getKey(), pair.getValue());
        }

        return state;
    }
}

package app.tests.rewards;

import org.hyperledger.fabric.gateway.Contract;

import app.tests.Test;
import app.tests.simulation.OnePostManyLikeSimulationTests;
import app.tests.simulation.Simulation;
import app.tests.util.Logger;

public class LikeRewardingTests implements Test{

    private Simulation onePostManyLikeSimulation;
    private final int iterations;

    private final Contract contract;

    public LikeRewardingTests(final Contract contract, final int iterations){
        this.contract = contract;
        this.iterations = iterations;
    }

    @Override
    public String testName() {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public int numberIterations() {
        return iterations;
    }

    @Override
    public boolean pre(final Logger logger) {
        try {
            onePostManyLikeSimulation = new OnePostManyLikeSimulationTests(contract);
        } catch (final Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean runTest(final Logger logger, final int currentIteration) {
        // TODO Auto-generated method stub
        onePostManyLikeSimulation.runTest();
        return false;
    }
    
}

package app.tests.rewards;

import org.hyperledger.fabric.gateway.Contract;

import app.tests.Test;
import app.tests.simulation.OnePostManyDislikeSimulationTests;
import app.tests.simulation.Simulation;
import app.tests.util.Logger;

public class DislikeRewardingTests implements Test {
    private Simulation onePostManyDislikeSimulation;
    private final int iterations;
    private final Contract contract;

    public DislikeRewardingTests(final Contract contract, int iterations){
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
            onePostManyDislikeSimulation = new OnePostManyDislikeSimulationTests(contract);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean runTest(Logger logger, int currentIteration) {
        // TODO Auto-generated method stub
        onePostManyDislikeSimulation.runTest();
        return false;
    }
}

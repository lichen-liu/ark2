package app.tests;

import org.hyperledger.fabric.gateway.Contract;

import app.tests.simple.LikeTests;
import app.tests.simple.PostTests;
import app.tests.simple.TransactionTests;

public class TestScheduler {
    public static TestSuite getPerformanceTestSuite() {
        return new TestSuite() {
            @Override
            public void launch() {
                System.out.println("Performance Test Suite");
            }
        };
    }

    public static TestSuite getRewardsTestSuite() {
        return new TestSuite() {
            @Override
            public void launch() {
                System.out.println("Rewards Test Suite");
            }
        };
    }

    public static TestSuite getSimpleTestSuite(final Contract contract) {
        return new TestSuite() {
            @Override
            public void launch() {
                try {
                    new PostTests().runTest();
                    new LikeTests(contract).runTest();
                    new TransactionTests().runTest();
                } catch (final Exception e) {
                    e.printStackTrace();
                }

            }
        };
    }

    public static TestSuite getSimulationTestSuite() {
        return new TestSuite() {
            @Override
            public void launch() {
                try {
                    new app.tests.simulation.PostCompetitonSimulationTests().run();
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }
}

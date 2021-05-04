package app.tests;

import java.util.List;

import org.hyperledger.fabric.gateway.Contract;

import app.tests.simple.LikeTests;
import app.tests.simple.PostTests;
import app.tests.simple.TransactionTests;

public class TestSchedules {
    public static TestSuite getPerformanceTestSuite() {
        return new TestSuite() {
            @Override
            protected List<? extends Test> setUpTests() {
                return null;
            }
        };
    }

    public static TestSuite getRewardsTestSuite() {
        return new TestSuite() {
            @Override
            protected List<? extends Test> setUpTests() {
                return null;
            }
        };
    }

    public static TestSuite getSimpleTestSuite(final Contract contract) {
        return new TestSuite() {
            @Override
            protected List<? extends Test> setUpTests() {
                return List.of(new PostTests(), new LikeTests(contract), new TransactionTests());
            }
        };
    }

    public static TestSuite getSimulationTestSuite() {
        return new TestSuite() {
            @Override
            protected List<? extends Test> setUpTests() {
                return null;
            }
        };
    }
}

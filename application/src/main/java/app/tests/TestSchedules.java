package app.tests;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.hyperledger.fabric.gateway.Contract;

import app.tests.performance.read.ids.PostIDsFetchingTests;
import app.tests.performance.write.DislikePublishingTests;
import app.tests.performance.write.LikePublishingTests;
import app.tests.performance.write.PostPublishingTests;
import app.tests.simple.LikeTests;
import app.tests.simple.PostTests;
import app.tests.simple.TransactionTests;
import app.util.ByteUtils;
import app.util.Cryptography;

public class TestSchedules {
    public static TestSuite getPerformanceTestSuite(final Contract contract) {
        final int iterations = 100;
        return new TestSuite("PerformanceTestSuite") {
            @Override
            protected List<? extends Test> setUpTests() {
                KeyPair userKeyPair = null;
                try {
                    userKeyPair = Cryptography.generateRandomKeyPair();
                } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }

                final List<Test> tests = new ArrayList<Test>();
                tests.add(new PostPublishingTests(contract, iterations, userKeyPair));
                tests.add(new LikePublishingTests(contract, iterations));
                tests.add(new DislikePublishingTests(contract, iterations));
                tests.add(new PostIDsFetchingTests(contract, iterations, null));
                tests.add(new PostIDsFetchingTests(contract, iterations,
                        ByteUtils.toAsciiString(userKeyPair.getPublic().getEncoded())));

                return tests;
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
                return List.of(new PostTests(contract), new LikeTests(contract), new TransactionTests(contract));
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

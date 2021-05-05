package app.tests;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.hyperledger.fabric.gateway.Contract;

import app.tests.performance.read.keys.DislikeKeysFetchingTests;
import app.tests.performance.read.keys.LikeKeysFetchingTests;
import app.tests.performance.read.keys.PointTransactionKeysFetchingTests;
import app.tests.performance.read.keys.PostKeysFetchingTests;
import app.tests.performance.write.DislikePublishingTests;
import app.tests.performance.write.LikePublishingTests;
import app.tests.performance.write.PostPublishingTests;
import app.tests.rewards.DislikeRewardingTests;
import app.tests.rewards.LikeRewardingTests;
import app.tests.simple.LikeTests;
import app.tests.simple.PostTests;
import app.tests.simple.TransactionTests;
import app.util.ByteUtils;
import app.util.Cryptography;

public class TestSchedules {
    public static TestSuite getPerformanceTestSuite(final Contract contract) {
        final int iterations = 100;
        return new SampleTestSuite("Performance") {
            @Override
            protected List<? extends Test> setUpTests() {
                KeyPair postAuthorKeyPair = null;
                try {
                    postAuthorKeyPair = Cryptography.generateRandomKeyPair();
                } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                final String userKey = ByteUtils.toAsciiString(postAuthorKeyPair.getPublic().getEncoded());

                final BlockingQueue<String> likedPostKeyQueue = new ArrayBlockingQueue<String>(1);
                final BlockingQueue<String> dislikedPostKeyQueue = new ArrayBlockingQueue<String>(1);

                final List<Test> tests = new ArrayList<Test>();
                tests.add(new PostPublishingTests(contract, iterations, postAuthorKeyPair));
                tests.add(new LikePublishingTests(contract, iterations, likedPostKeyQueue, postAuthorKeyPair));
                tests.add(new DislikePublishingTests(contract, iterations, dislikedPostKeyQueue, postAuthorKeyPair));
                tests.add(new PostKeysFetchingTests(contract, iterations, null));
                tests.add(new PostKeysFetchingTests(contract, iterations, userKey));
                tests.add(new LikeKeysFetchingTests(contract, iterations, likedPostKeyQueue));
                tests.add(new DislikeKeysFetchingTests(contract, iterations, dislikedPostKeyQueue));
                tests.add(new PointTransactionKeysFetchingTests(contract, iterations, userKey));

                return tests;
            }
        };
    }

    public static TestSuite getRewardsTestSuite(final Contract contract) {
        final int iterations = 100;

        return new TestSuite() {
            @Override
            protected List<? extends Test> setUpTests() {
                final List<Test> tests = new ArrayList<Test>();
                tests.add(new LikeRewardingTests(contract, iterations));
                tests.add(new DislikeRewardingTests(contract, iterations));
                return tests;
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

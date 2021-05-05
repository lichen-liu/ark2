package app.tests;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.hyperledger.fabric.gateway.Contract;

import app.tests.performance.DislikeKeysFetchingTest;
import app.tests.performance.DislikePublishingTest;
import app.tests.performance.DislikesFetchingTest;
import app.tests.performance.LikeKeysFetchingTest;
import app.tests.performance.LikePublishingTest;
import app.tests.performance.LikesFetchingTest;
import app.tests.performance.PointBalanceFetchingTest;
import app.tests.performance.PointTransactionKeysFetchingTest;
import app.tests.performance.PostKeysFetchingTest;
import app.tests.performance.PostPublishingTest;
import app.tests.performance.PostsFetchingTest;
import app.tests.rewards.DislikeRewardingTest;
import app.tests.rewards.LikeRewardingTest;
import app.tests.simple.LikeTest;
import app.tests.simple.PostTest;
import app.tests.simple.TransactionTest;
import app.util.ByteUtils;
import app.util.Cryptography;

public class TestSchedules {
    public static TestSuite getPerformanceTestSuite(final Contract contract) {
        final int iterations = 100;
        return new SampleTestSuite("Performance") {
            @Override
            protected int defaultIterations() {
                return iterations;
            }

            @Override
            protected List<? extends Testable> setUpTests() {
                KeyPair postAuthorKeyPair = null;
                try {
                    postAuthorKeyPair = Cryptography.generateRandomKeyPair();
                } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                final String userKey = ByteUtils.toAsciiString(postAuthorKeyPair.getPublic().getEncoded());

                final BlockingQueue<String> likedPostKeyQueue = new ArrayBlockingQueue<String>(1);
                final BlockingQueue<String> dislikedPostKeyQueue = new ArrayBlockingQueue<String>(1);

                final List<Testable> tests = new ArrayList<Testable>();
                // Write
                tests.add(new PostPublishingTest(contract, postAuthorKeyPair));
                tests.add(new LikePublishingTest(contract, likedPostKeyQueue, postAuthorKeyPair));
                tests.add(new DislikePublishingTest(contract, dislikedPostKeyQueue, postAuthorKeyPair));
                // Pure Read
                tests.add(new PostKeysFetchingTest(contract, null));
                tests.add(new PostKeysFetchingTest(contract, userKey));
                tests.add(new LikeKeysFetchingTest(contract, likedPostKeyQueue));
                tests.add(new DislikeKeysFetchingTest(contract, dislikedPostKeyQueue));
                tests.add(new PointTransactionKeysFetchingTest(contract, userKey));
                tests.add(new PointBalanceFetchingTest(contract, userKey));
                // Read and Verification
                tests.add(new PostsFetchingTest(contract, userKey));
                tests.add(new LikesFetchingTest(contract, likedPostKeyQueue));
                tests.add(new DislikesFetchingTest(contract, dislikedPostKeyQueue));

                return tests;
            }
        };
    }

    public static TestSuite getRewardsTestSuite(final Contract contract) {
        final int iterations = 100;

        return new TestSuite() {
            @Override
            protected int defaultIterations() {
                return iterations;
            }

            @Override
            protected List<? extends Testable> setUpTests() {
                final List<Testable> tests = new ArrayList<Testable>();
                tests.add(new LikeRewardingTest(contract));
                tests.add(new DislikeRewardingTest(contract));
                return tests;
            }
        };
    }

    public static TestSuite getSimpleTestSuite(final Contract contract) {
        return new TestSuite() {
            @Override
            protected List<? extends Testable> setUpTests() {
                return List.of(new PostTest(contract), new LikeTest(contract), new TransactionTest(contract));
            }
        };
    }

    public static TestSuite getSimulationTestSuite() {
        return new TestSuite() {
            @Override
            protected List<? extends Testable> setUpTests() {
                return null;
            }
        };
    }
}

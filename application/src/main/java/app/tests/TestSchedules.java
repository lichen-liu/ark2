package app.tests;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.hyperledger.fabric.gateway.Contract;

import app.tests.performance.DislikeKeysFetchingTests;
import app.tests.performance.DislikePublishingTests;
import app.tests.performance.LikeKeysFetchingTests;
import app.tests.performance.LikePublishingTests;
import app.tests.performance.PointBalanceFetchingTests;
import app.tests.performance.PointTransactionKeysFetchingTests;
import app.tests.performance.PostKeysFetchingTests;
import app.tests.performance.PostPublishingTests;
import app.tests.performance.PostsFetchingTests;
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
                tests.add(new PostPublishingTests(contract, iterations, postAuthorKeyPair));
                tests.add(new LikePublishingTests(contract, iterations, likedPostKeyQueue, postAuthorKeyPair));
                tests.add(new DislikePublishingTests(contract, iterations, dislikedPostKeyQueue, postAuthorKeyPair));
                // Pure Read
                tests.add(new PostKeysFetchingTests(contract, iterations, null));
                tests.add(new PostKeysFetchingTests(contract, iterations, userKey));
                tests.add(new LikeKeysFetchingTests(contract, iterations, likedPostKeyQueue));
                tests.add(new DislikeKeysFetchingTests(contract, iterations, dislikedPostKeyQueue));
                tests.add(new PointTransactionKeysFetchingTests(contract, iterations, userKey));
                tests.add(new PointBalanceFetchingTests(contract, iterations, userKey));
                // Read and Verification
                tests.add(new PostsFetchingTests(contract, iterations, userKey));

                return tests;
            }
        };
    }

    public static TestSuite getRewardsTestSuite(final Contract contract) {
        final int iterations = 100;

        return new TestSuite() {
            @Override
            protected List<? extends Testable> setUpTests() {
                final List<Testable> tests = new ArrayList<Testable>();
                tests.add(new LikeRewardingTests(contract, iterations));
                tests.add(new DislikeRewardingTests(contract, iterations));
                return tests;
            }
        };
    }

    public static TestSuite getSimpleTestSuite(final Contract contract) {
        return new TestSuite() {
            @Override
            protected List<? extends Testable> setUpTests() {
                return List.of(new PostTests(contract), new LikeTests(contract), new TransactionTests(contract));
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

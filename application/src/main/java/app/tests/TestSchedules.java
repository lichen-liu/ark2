package app.tests;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.hyperledger.fabric.gateway.Contract;

import app.tests.performance.DislikeKeysFetchingTest;
import app.tests.performance.DislikePublishingTest;
import app.tests.performance.DislikesFetchingTest;
import app.tests.performance.LikeKeysFetchingTest;
import app.tests.performance.LikePublishingTest;
import app.tests.performance.LikesFetchingTest;
import app.tests.performance.PointBalanceFetchingTest;
import app.tests.performance.PointTransactionKeysFetchingTest;
import app.tests.performance.PointTransactionsFetchingTest;
import app.tests.performance.PostKeysFetchingTest;
import app.tests.performance.PostPublishingTest;
import app.tests.performance.PostsFetchingTest;
import app.tests.rewards.DislikeRewardingTest;
import app.tests.rewards.HateDislikerRewardingTest;
import app.tests.rewards.LikeRewardingTest;
import app.tests.rewards.SelfLikeRewardingTest;
import app.tests.simple.LikeTest;
import app.tests.simple.PostTest;
import app.tests.simple.TransactionTest;
import app.util.ByteUtils;
import app.util.Cryptography;

public class TestSchedules {
    public static TestSuite getPerformanceTestSuite(final Contract contract, final Path performanceFileDir) {
        final int iterations = 600;
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
                tests.add(new PointTransactionsFetchingTest(contract, userKey));

                return tests;
            }

            @Override
            protected void post() {
                final List<String> csvData = this.getPerformanceSampleData().entrySet().stream().map(
                        entry -> entry.getValue().stream().map(elapsed -> entry.getKey() + "," + elapsed.toString()))
                        .flatMap(Function.identity()).collect(Collectors.toList());
                csvData.add(0, "test,elapsed_ms");

                System.out.println("\n===================================================");
                for (final var row : csvData) {
                    System.out.println(row);
                }
                System.out.println("===================================================\n");

                super.post();

                performanceFileDir.toFile().mkdirs();
                final String timestamp = ZonedDateTime.now(ZoneOffset.UTC)
                        .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
                final Path filePath = Paths.get(performanceFileDir.toString(), "perf_" + timestamp + ".csv");
                try {
                    Files.write(filePath, csvData, StandardCharsets.UTF_8);
                    System.out.println("Successfully wrote performance results into " + filePath.toString());
                } catch (final IOException e) {
                    System.out.println("Failed to write performance results into " + filePath.toString());
                    e.printStackTrace();
                }
            }
        };
    }

    public static TestSuite getLikeRewardsTestSuite(final Contract contract) {
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
                return tests;
            }
        };
    }

    public static TestSuite getDislikeRewardsTestSuite(final Contract contract) {
        final int iterations = 100;

        return new TestSuite() {
            @Override
            protected int defaultIterations() {
                return iterations;
            }

            @Override
            protected List<? extends Testable> setUpTests() {
                final List<Testable> tests = new ArrayList<Testable>();
                tests.add(new DislikeRewardingTest(contract));
                return tests;
            }
        };
    }

    public static TestSuite getSelfLikeRewardsTestSuite(final Contract contract) {
        final int iterations = 100;

        return new TestSuite() {
            @Override
            protected int defaultIterations() {
                return iterations;
            }

            @Override
            protected List<? extends Testable> setUpTests() {
                final List<Testable> tests = new ArrayList<Testable>();
                tests.add(new SelfLikeRewardingTest(contract));
                return tests;
            }
        };
    }

    public static TestSuite getHateDisLikeRewardsTestSuite(final Contract contract) {
        final int iterations = 100;

        return new TestSuite() {
            @Override
            protected int defaultIterations() {
                return iterations;
            }

            @Override
            protected List<? extends Testable> setUpTests() {
                final List<Testable> tests = new ArrayList<Testable>();
                tests.add(new HateDislikerRewardingTest(contract));
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

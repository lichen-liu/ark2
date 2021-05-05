package app.tests.performance.write;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.BlockingQueue;

import org.hyperledger.fabric.gateway.Contract;

import app.tests.Testable;
import app.tests.util.Logger;
import app.tests.util.TestClient;
import app.user.NamedService;
import app.user.ServiceProvider;

public class LikePublishingTests implements Testable {
    private final Contract contract;
    private String postKey;
    private NamedService user = null;
    private final int iterations;
    final BlockingQueue<String> likedPostKeyQueue;
    final KeyPair postAuthorKeyPair;

    @Override
    public String testName() {
        return "LikePublishingTests";
    }

    public LikePublishingTests(final Contract contract, final int iterations,
            final BlockingQueue<String> likedPostKeyQueue, final KeyPair postAuthorKeyPair) {
        this.contract = contract;
        this.iterations = iterations;
        this.likedPostKeyQueue = likedPostKeyQueue;
        this.postAuthorKeyPair = postAuthorKeyPair;
    }

    @Override
    public int numberIterations() {
        return this.iterations;
    }

    @Override
    public boolean pre(final Logger logger) {
        try {
            this.postKey = ServiceProvider.createNamedService(contract, this.postAuthorKeyPair.getPublic(),
                    this.postAuthorKeyPair.getPrivate()).publishNewPost("_");
            assert this.postKey != null;
            final var isSuccessful = this.likedPostKeyQueue.offer(this.postKey);
            assert isSuccessful;

            this.user = TestClient.createTestClient(contract);
        } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean runTest(final Logger logger, final int currentIteration) {
        String likeKey = null;
        do {
            likeKey = this.user.publishNewLike(postKey);
            logger.printResult(likeKey);
        } while (likeKey == null);

        return true;
    }

}

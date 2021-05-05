package app.tests.performance;

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

public class DislikePublishingTests implements Testable {
    private final Contract contract;
    private String postKey;
    private NamedService user = null;
    final BlockingQueue<String> dislikedPostKeyQueue;
    final KeyPair postAuthorKeyPair;

    @Override
    public String testName() {
        return "DislikePublishingTests";
    }

    public DislikePublishingTests(final Contract contract, final BlockingQueue<String> dislikedPostKeyQueue,
            final KeyPair postAuthorKeyPair) {
        this.contract = contract;
        this.dislikedPostKeyQueue = dislikedPostKeyQueue;
        this.postAuthorKeyPair = postAuthorKeyPair;
    }

    @Override
    public boolean pre(final Logger logger, final int numberIteration) {
        try {
            this.postKey = ServiceProvider.createNamedService(contract, this.postAuthorKeyPair.getPublic(),
                    this.postAuthorKeyPair.getPrivate()).publishNewPost("_");
            assert this.postKey != null;
            final var isSuccessful = this.dislikedPostKeyQueue.offer(this.postKey);
            assert isSuccessful;

            this.user = TestClient.createTestClient(contract);
        } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean runTest(final Logger logger, final int currentIteration, final int numberIteration) {
        String dislikeKey = null;
        do {
            dislikeKey = this.user.publishNewDislike(postKey);
            logger.printResult(dislikeKey);
        } while (dislikeKey == null);

        return true;
    }
}

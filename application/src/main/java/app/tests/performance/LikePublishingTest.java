package app.tests.performance;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;

import org.hyperledger.fabric.gateway.Contract;

import app.service.NamedService;
import app.service.ServiceProvider;
import app.tests.Testable;
import app.tests.util.Logger;
import app.tests.util.TestClient;

public class LikePublishingTest implements Testable {
    private final Contract contract;
    private String postKey;
    private NamedService user = null;
    private final BlockingQueue<String> likedPostKeyQueue;
    private final KeyPair postAuthorKeyPair;
    private final int iterationMultipler;

    public LikePublishingTest(final Contract contract, final BlockingQueue<String> likedPostKeyQueue,
            final KeyPair postAuthorKeyPair, final int iterationMultipler) {
        this.contract = contract;
        this.likedPostKeyQueue = likedPostKeyQueue;
        this.postAuthorKeyPair = postAuthorKeyPair;
        this.iterationMultipler = iterationMultipler;
    }

    @Override
    public Optional<Integer> requestNumberIterations(final int plannedNumberIterations) {
        return Optional.of(plannedNumberIterations * this.iterationMultipler);
    }

    @Override
    public boolean pre(final Logger logger, final int numberIteration) {
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
    public boolean runTest(final Logger logger, final int currentIteration, final int numberIteration) {
        String likeKey = null;
        do {
            likeKey = this.user.publishNewLike(postKey);
            logger.printResult(likeKey);
        } while (likeKey == null);

        return true;
    }
}

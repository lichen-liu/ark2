package app.tests.performance;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;

import org.hyperledger.fabric.gateway.Contract;

import app.tests.Test;
import app.tests.util.Logger;
import app.tests.util.TestClient;
import app.user.NamedService;

public class LikePublishingTests implements Test {

    private final Contract contract;
    private String postKey;
    private NamedService user = null;

    @Override
    public String testName() {
        return "LikePublishingTests";
    }

    public LikePublishingTests(final Contract contract) {
        this.contract = contract;
    }

    @Override
    public boolean preTest(final Logger logger, final int currentIteration) {
        try {
            this.user = TestClient.createTestClient(contract);
            this.postKey = user.publishNewPost("_");
            
        } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean runTest(final Logger logger, final int currentIteration) {
      
        postKey = this.user.publishNewLike(postKey);
        logger.print(postKey);
        return true;

    }

}

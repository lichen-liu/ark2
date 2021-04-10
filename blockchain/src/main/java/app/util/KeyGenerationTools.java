package app.util;

import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.CompositeKey;

public class KeyGenerationTools {
    public enum ObjectType {
        POST, POINT_TRANSACTION, LIKE
    }

    public static String generateKeyForPost(final String userId, final String signature, final String salt) {
        final CompositeKey compositeKey = new CompositeKey(ObjectType.POST.name(), userId, signature, salt);
        return compositeKey.toString();
    }

    public static String generateKeyForPost(final ChaincodeStub stub, final String userId, final String signature) {
        String postId;
        int attempt = 0;
        do {
            postId = generateKeyForPost(userId, signature, String.valueOf(attempt));
            attempt++;
        } while (ChaincodeStubTools.isKeyExisted(stub, postId));

        return postId;
    }
}

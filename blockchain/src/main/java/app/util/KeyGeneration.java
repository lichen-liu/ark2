package app.util;

import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.CompositeKey;

public interface KeyGeneration {
    /**
     * Generate a composite key. The composite key is usually formed as:
     * 
     * <pre>
     * new CompositeKey(objectTypeName, keyForFiltering, deterministicRandomString, nonceValueForUniqueness);
     * </pre>
     * 
     * @param salt extra field in the composite key for uniqueness
     * @return composite key in String
     */
    public CompositeKey generateCompositeKey(final String salt);

    public default String generateKey(final String salt) {
        return generateCompositeKey(salt).toString();
    }

    public default String generateKey(final ChaincodeStub stub) {
        String key;
        int attempt = 0;
        do {
            key = generateKey(String.valueOf(attempt));
            attempt++;
        } while (ChaincodeStubTools.isKeyExisted(stub, key));

        return key;
    }
}

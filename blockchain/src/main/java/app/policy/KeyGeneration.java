package app.policy;

import java.util.function.Predicate;

import org.hyperledger.fabric.shim.ledger.CompositeKey;

import app.util.ChaincodeStubTools.Key;

public interface KeyGeneration {
    /**
     * Generate a composite key. The composite key is usually formed as:
     * 
     * <pre>
     * new CompositeKey(objectTypeName, keyForFiltering, deterministicRandomString, nonceValueForUniqueness);
     * </pre>
     * 
     * @param salt extra field in the composite key for uniqueness
     * @return CompositeKey
     */
    public abstract CompositeKey generateKey(final String salt);

    public default Key generateKey(final Predicate<Key> isKeyInvalid) {
        Key key = null;
        int attempt = 0;
        do {
            key = new Key(generateKey(String.valueOf(attempt)));
            attempt++;
        } while (isKeyInvalid.test(key));

        return key;
    }
}

package app.policy;

import java.util.function.Predicate;

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
    public abstract String generateKey(final String salt);

    public default String generateKey(final Predicate<String> isKeyInvalid) {
        String key;
        int attempt = 0;
        do {
            key = generateKey(String.valueOf(attempt));
            attempt++;
        } while (isKeyInvalid.test(key));

        return key;
    }
}

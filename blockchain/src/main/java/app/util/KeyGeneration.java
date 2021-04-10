package app.util;

import org.hyperledger.fabric.shim.ChaincodeStub;

public interface KeyGeneration {
    public String getObjectTypeName();

    /**
     * Generate a composite key
     * 
     * @param salt extra field in the composite key for uniqueness
     * @return composite key in String
     */
    public String generateKey(final String salt);

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

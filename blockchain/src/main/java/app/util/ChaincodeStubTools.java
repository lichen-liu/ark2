package app.util;

import java.util.Base64;
import java.util.stream.StreamSupport;

import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.CompositeKey;

import app.policy.KeyGeneration;

public class ChaincodeStubTools {
    public static boolean isKeyExisted(final ChaincodeStub stub, final Key key) {
        return !stub.getStringState(key.getCCKeyString()).isEmpty();
    }

    public static String tryGetStringStateByKey(final ChaincodeStub stub, final Key key) {
        final String state = stub.getStringState(key.getCCKeyString());
        if (state.isEmpty()) {
            final String errorMessage = String.format("State %s does not exist", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, errorMessage);
        }
        return state;
    }

    public static long getNumberStatesByPartialCompositeKey(final ChaincodeStub stub,
            final CompositeKey partialCompositeKey) {
        final var keyValueIterator = stub.getStateByPartialCompositeKey(partialCompositeKey);
        return StreamSupport.stream(keyValueIterator.spliterator(), false).count();
    }

    public static Key generateKey(final ChaincodeStub stub, final KeyGeneration object) {
        return object.generateKey(key -> ChaincodeStubTools.isKeyExisted(stub, key));
    }

    public static void putStringState(final ChaincodeStub stub, final Key key, final String object) {
        stub.putStringState(key.getCCKeyString(), object);
    }

    public static class Key {
        private final String key;

        private Key(final String key) {
            this.key = key;
        }

        public Key(final CompositeKey key) {
            this.key = key.toString();
        }

        public static Key createFromBase64UrlKeyString(final String base64UrlKeyString)
                throws IllegalArgumentException {
            final byte[] keyBytes = Base64.getUrlDecoder().decode(base64UrlKeyString);
            return new Key(new String(keyBytes));
        }

        public static Key createFromCCKeyString(final String ccKeyString) {
            return new Key(ccKeyString);
        }

        public String getBase64UrlKeyString() {
            return Base64.getUrlEncoder().encodeToString(this.key.getBytes());
        }

        private String getCCKeyString() {
            return this.key;
        }

        public String getObjectTypeString() {
            final CompositeKey compositeKey = CompositeKey.parseCompositeKey(this.key);
            return compositeKey.getObjectType();
        }
    }
}

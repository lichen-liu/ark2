package app;

import java.util.Base64;
import java.util.stream.StreamSupport;

import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.CompositeKey;

import app.policy.BytesState;
import app.policy.KeyGeneration;
import app.policy.Serde;

public class ChaincodeStubTools {
    public static boolean isKeyExisted(final ChaincodeStub stub, final Key key) {
        return stub.getState(key.getCCKeyString()).length != 0;
    }

    public static <T> T tryGetBytesStateByKey(final ChaincodeStub stub, final Key key, final Serde serde,
            final Class<T> stateType) {
        final byte[] stateBytes = stub.getState(key.getCCKeyString());
        if (stateBytes.length == 0) {
            final String errorMessage = String.format("State %s does not exist", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, errorMessage);
        }
        return serde.deserialize(stateBytes, stateType);
    }

    public static long getNumberStatesByPartialCompositeKey(final ChaincodeStub stub,
            final CompositeKey partialCompositeKey) {
        final var keyValueIterator = stub.getStateByPartialCompositeKey(partialCompositeKey);
        return StreamSupport.stream(keyValueIterator.spliterator(), false).count();
    }

    public static Key generateKey(final ChaincodeStub stub, final KeyGeneration object) {
        return object.generateKey(key -> ChaincodeStubTools.isKeyExisted(stub, key));
    }

    public static void putBytesState(final ChaincodeStub stub, final Key key, final Serde serde,
            final BytesState object) {
        stub.putState(key.getCCKeyString(), serde.serialize(object));
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
            return Base64.getUrlEncoder().withoutPadding().encodeToString(this.key.getBytes());
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

package app.util;

import java.util.UUID;

import org.hyperledger.fabric.shim.ledger.CompositeKey;

public class KeyGenerationTools {
    public enum ObjectType {
        POST, POINT_TRANSACTION, LIKE
    }

    public static String generateKey(final ObjectType objectType) {
        final CompositeKey compositeKey = new CompositeKey(objectType.name(), generateUuid());
        return compositeKey.toString();
    }

    private static String generateUuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}

package app.util;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.hyperledger.fabric.shim.ledger.CompositeKey;

public class Key {
    private final String key;

    private Key(final String key) {
        this.key = key;
    }

    public Key(final CompositeKey key) {
        this.key = key.toString();
    }

    public static Key createFromHexKeyString(final String hexKeyString) throws DecoderException {
        final byte[] keyBytes = Hex.decodeHex(hexKeyString);
        return new Key(new String(keyBytes));
    }

    public static Key createFromCCKeyString(final String ccKeyString) {
        return new Key(ccKeyString);
    }

    public String getHexKeyString() {
        return Hex.encodeHexString(this.key.getBytes());
    }

    public String getCCKeyString() {
        return this.key;
    }
}

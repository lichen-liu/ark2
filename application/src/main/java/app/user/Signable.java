package app.user;

import java.security.PrivateKey;

import app.util.ByteUtils;

public interface Signable extends Identifiable {
    public abstract PrivateKey getPrivateKey();

    public default String getPrivateKeyString() {
        return ByteUtils.toAsciiString(getPrivateKey().getEncoded());
    }
}
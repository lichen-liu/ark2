package app.user;

import java.security.PublicKey;

import app.util.ByteUtils;

@FunctionalInterface
public interface Identifiable {
    public abstract PublicKey getPublicKey();

    public default String getPublicKeyString() {
        return ByteUtils.toAsciiString(getPublicKey().getEncoded());
    }
}
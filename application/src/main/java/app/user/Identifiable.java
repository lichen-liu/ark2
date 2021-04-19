package app.user;

import java.security.PublicKey;

import app.utils.ByteUtils;

public interface Identifiable {
    public abstract PublicKey getPublicKey();

    public default String getPublicKeyString() {
        return ByteUtils.toHexString(getPublicKey().getEncoded());
    }
}
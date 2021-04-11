package app.util;

public interface SignatureVerification {
    public abstract String getHashString();

    public abstract String getSignatureString();

    public abstract String getPublicKeyString();

    public default boolean isMatchingSignature() {
        return true;
    }
}

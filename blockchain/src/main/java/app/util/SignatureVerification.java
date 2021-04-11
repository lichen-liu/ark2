package app.util;

public interface SignatureVerification {
    public abstract String getHashString();

    public abstract String getSignatureString();

    public abstract String getPublicKey();

    public default boolean isMatchingSignature() {
        return true;
    }
}

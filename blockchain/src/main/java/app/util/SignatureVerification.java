package app.util;

public interface SignatureVerification {
    public abstract String getExpectedSignatureContent();

    public abstract String getSignatureForVerification();

    public abstract String getVerificationKey();

    public default boolean isMatchingSignature() {
        return true;
    }
}

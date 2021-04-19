package app.repository;

import java.security.NoSuchAlgorithmException;

import app.utils.ByteUtils;

public class Hash {
    public static byte[] generateLikeHash(final String timestamp, final String postKey, final String publicKeyString)
            throws NoSuchAlgorithmException {
        return ByteUtils.getSHA(String.join("", timestamp, postKey, publicKeyString));
    }

    public static byte[] generatePostHash(final String timestamp, final String content, final String publicKeyString)
            throws NoSuchAlgorithmException {
        return ByteUtils.getSHA(String.join("", timestamp, content, publicKeyString));
    }

    public static byte[] generatePointTransactionHash(final String timestamp, final String payerId,
            final String payerAmount, final String issuerId) throws NoSuchAlgorithmException {
        return ByteUtils.getSHA(String.join("", timestamp, payerId, payerAmount, issuerId));
    }
}

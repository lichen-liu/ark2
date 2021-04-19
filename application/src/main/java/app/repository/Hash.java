package app.repository;

import java.security.NoSuchAlgorithmException;

import app.utils.ByteUtils;

public class Hash {
    public static byte[] GenerateLikeHash(final String timestamp, final String postKey, final String publicKeyString)
            throws NoSuchAlgorithmException {
        return ByteUtils.getSHA(String.join("", timestamp, postKey, publicKeyString));
    }

    public static byte[] GeneratePostHash(final String timestamp, final String content, final String publicKeyString)
            throws NoSuchAlgorithmException {
        return ByteUtils.getSHA(String.join("", timestamp, content, publicKeyString));
    }

    public static byte[] GeneratePointTransactionHash(final String timestamp, final String payerId,
            final String payerAmount, final String issuerId) throws NoSuchAlgorithmException {
        return ByteUtils.getSHA(String.join("", timestamp, payerId, payerAmount, issuerId));
    }
}

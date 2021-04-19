package app.utils;

import java.security.NoSuchAlgorithmException;

public class Hash {
    public static byte[] GenerateLikeHash(String timestamp, String postKey, String publicKeyString) throws NoSuchAlgorithmException {
        return ByteUtils.getSHA(String.join("", timestamp, postKey, publicKeyString));
    }

    public static byte[] GeneratePostHash(String timestamp, String content, String publicKeyString) throws NoSuchAlgorithmException {
        return ByteUtils.getSHA(String.join("", timestamp, content, publicKeyString));
    }

    public static byte[] GeneratePointTransactionHash(String timestamp, String payerId, String payerAmount, String publicKeyString, String reference) throws NoSuchAlgorithmException{
        return ByteUtils.getSHA(String.join("", timestamp, payerId, payerAmount, publicKeyString, reference));
    }
}

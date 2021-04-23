package app.repository;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import app.repository.data.PointTransaction;
import app.util.ByteUtils;

public class Hash {
    public static byte[] generateLikeHash(final String timestamp, final String postKey, final String publicKeyString)
            throws NoSuchAlgorithmException {
        return ByteUtils.getSHA(String.join("", timestamp, postKey, publicKeyString));
    }

    public static byte[] generatePostHash(final String timestamp, final String content, final String publicKeyString)
            throws NoSuchAlgorithmException {
        return ByteUtils.getSHA(String.join("", timestamp, content, publicKeyString));
    }

    public static byte[] generatePointTransactionHash(final String timestamp, final String issuerId,
            final PointTransaction.Entry[] payerEntries) throws NoSuchAlgorithmException {
        final String payerEntriesString = String.join("",
                Arrays.asList(payerEntries).stream()
                        .map(payerEntry -> String.join("", payerEntry.userId, payerEntry.pointAmount.toString()))
                        .toArray(String[]::new));

        return ByteUtils.getSHA(String.join("", timestamp, issuerId, payerEntriesString));
    }
}

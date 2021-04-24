package app.repository;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.hyperledger.fabric.gateway.Contract;

import app.repository.data.Dislike;
import app.repository.data.PointTransaction;
import app.util.ByteUtils;
import app.util.Cryptography;
import app.util.GensonDeserializer;

public class DislikeRepository extends ReadableRepository<Dislike> {

    public DislikeRepository(final Contract contract) {
        super(Dislike.class);
        this.deserializer = new GensonDeserializer();
        this.contract = contract;
    }

    public String insertNewDislike(final String postKey, final double pointAmount, final PublicKey publicKey,
            final PrivateKey privateKey) throws Exception {
        final String timestamp = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);
        final String publicKeyString = ByteUtils.toAsciiString(publicKey.getEncoded());

        final byte[] dislikeHash = Hash.generateDislikeHash(timestamp, postKey, publicKeyString);
        final byte[] dislikeSignature = Cryptography.sign(privateKey, dislikeHash);

        final var dislikerPayerEntry = new PointTransaction.Entry(publicKeyString, pointAmount);
        final String authorUserId = new PostRepository(this.contract).selectObjectsByCustomKeys(postKey).get(0).userId;
        final var authorPayerEntry = new PointTransaction.Entry(authorUserId, pointAmount);
        final byte[] pointTransactionHash = Hash.generatePointTransactionHash(timestamp, publicKeyString,
                new PointTransaction.Entry[] { dislikerPayerEntry, authorPayerEntry });
        final byte[] pointTransactionSignature = Cryptography.sign(privateKey, pointTransactionHash);

        return new String(contract.submitTransaction("publishNewDislike", timestamp, postKey, dislikerPayerEntry.userId,
                dislikerPayerEntry.pointAmount.toString(), ByteUtils.toAsciiString(dislikeSignature),
                ByteUtils.toAsciiString(pointTransactionSignature)));
    }

    @Override
    public String toString() {
        return "DislikeRepository []";
    }

    @Override
    protected String getAllKeysQuery() {
        throw new UnsupportedOperationException("This operation is not supported");
    }

    @Override
    protected String getKeysByCustomKeysQuery() {
        return "getAllDislikeKeysByPostKey";
    }

    @Override
    protected String getObjectByKeyQuery() {
        return "getDislikeByKey";
    }
}
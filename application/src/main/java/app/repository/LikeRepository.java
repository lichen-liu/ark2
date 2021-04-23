package app.repository;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeoutException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.ContractException;

import app.repository.data.Like;
import app.repository.data.PointTransaction;
import app.util.ByteUtils;
import app.util.Cryptography;
import app.util.GensonDeserializer;

public class LikeRepository extends ReadableRepository<Like> {

    public LikeRepository(final Contract contract) {
        super(Like.class);
        this.deserializer = new GensonDeserializer();
        this.contract = contract;
    }

    public String insertNewLike(final String postKey, final double pointAmount, final PublicKey publicKey,
            final PrivateKey privateKey)
            throws InvalidKeyException, NoSuchAlgorithmException, SignatureException, ContractException,
            TimeoutException, InterruptedException, JsonParseException, JsonMappingException, IOException {

        final String timestamp = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);
        final String publicKeyString = ByteUtils.toAsciiString(publicKey.getEncoded());

        final byte[] likeHash = Hash.generateLikeHash(timestamp, postKey, publicKeyString);
        final byte[] likeSignature = Cryptography.sign(privateKey, likeHash);

        final var payerEntry = new PointTransaction.Entry(publicKeyString, pointAmount);
        final byte[] pointTransactionHash = Hash.generatePointTransactionHash(timestamp, publicKeyString,
                new PointTransaction.Entry[] { payerEntry });
        final byte[] pointTransactionSignature = Cryptography.sign(privateKey, pointTransactionHash);

        return new String(contract.submitTransaction("publishNewLike", timestamp, postKey,
                this.deserializer.transactionEntryToJson(payerEntry), ByteUtils.toAsciiString(likeSignature),
                ByteUtils.toAsciiString(pointTransactionSignature)));
    }

    @Override
    protected String getAllKeysQuery() {
        throw new UnsupportedOperationException("This operation is not supported");
    }

    @Override
    protected String getKeysByCustomKeysQuery() {
        return "getAllLikeKeysByPostKey";
    }

    @Override
    protected String getObjectByKeyQuery() {
        return "getLikeByKey";
    }
}
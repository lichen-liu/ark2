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

import app.repository.contracts.Transaction;
import app.utils.ByteUtils;
import app.utils.GensonDeserializer;
import app.utils.NewPostSignature;

public class LikeRepository extends ReadableRepository {

    public LikeRepository(final Contract contract) {
        this.deserializer = new GensonDeserializer();
        this.contract = contract;
    }

    public String insertNewLike(final Contract contract, final String postKey, final Transaction.Entry likeInfo,
            final PublicKey publicKey, final PrivateKey privateKey)
            throws InvalidKeyException, NoSuchAlgorithmException, SignatureException, ContractException,
            TimeoutException, InterruptedException, JsonParseException, JsonMappingException, IOException {

        final var timestamp = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);
        final var like = this.deserializer.transactionEntriesToJson(likeInfo);
        final var publicKeyString = ByteUtils.toHexString(publicKey.getEncoded());

        final var hash = ByteUtils.getSHA(String.join("", timestamp, postKey, publicKeyString));
        final var likeHash = ByteUtils.getSHA(like);
        final var signature = NewPostSignature.sign(privateKey, hash);
        final var likeSignature = NewPostSignature.sign(privateKey, likeHash);

        return new String(contract.submitTransaction("publishNewLike", timestamp, postKey, like,
                ByteUtils.toHexString(signature), ByteUtils.toHexString(likeSignature)));
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
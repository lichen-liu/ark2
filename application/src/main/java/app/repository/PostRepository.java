package app.repository;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeoutException;

import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.ContractException;

import app.utils.ByteUtils;
import app.utils.GensonDeserializer;
import app.utils.NewPostSignature;

public class PostRepository extends ReadableRepository {

    public PostRepository(final Contract contract) {
        this.deserializer = new GensonDeserializer();
        this.contract = contract;
    }

    public String insertNewPost(final Contract contract, final String content, final PublicKey publicKey,
            final PrivateKey privateKey) throws InvalidKeyException, NoSuchAlgorithmException, SignatureException,
            ContractException, TimeoutException, InterruptedException {

        final var timestamp = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);
        final var publicKeyString = ByteUtils.toHexString(publicKey.getEncoded());
        final var hash = ByteUtils.getSHA(String.join("", timestamp, content, publicKeyString));
        final var signature = NewPostSignature.sign(privateKey, hash);

        return new String(contract.submitTransaction("publishNewPost", timestamp, content, publicKeyString,
                ByteUtils.toHexString(signature)));
    }

    @Override
    protected String getAllKeysQuery() {
        return "getAllPostKeys";
    }

    @Override
    protected String getKeysByCustomKeysQuery() {
        return "getAllPostKeysByUserId";
    }

    @Override
    protected String getObjectByKeyQuery() {
        return "getPostByKey";
    }
}
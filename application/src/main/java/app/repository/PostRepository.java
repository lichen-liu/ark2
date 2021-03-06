package app.repository;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.hyperledger.fabric.gateway.Contract;

import app.util.ByteUtils;
import app.util.Cryptography;

public class PostRepository extends ReadableRepository<Post> {

    public PostRepository(final Contract contract) {
        super(Post.class);
        this.deserializer = new GensonDeserializer();
        this.contract = contract;
    }

    public String insertNewPost(final String content, final PublicKey publicKey, final PrivateKey privateKey)
            throws Exception {

        final String timestamp = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);
        final String publicKeyString = ByteUtils.toAsciiString(publicKey.getEncoded());
        final byte[] hash = Hash.generatePostHash(timestamp, content, publicKeyString);
        final byte[] signature = Cryptography.sign(privateKey, hash);

        return new String(contract.submitTransaction("publishNewPost", timestamp, content, publicKeyString,
                ByteUtils.toAsciiString(signature)));
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
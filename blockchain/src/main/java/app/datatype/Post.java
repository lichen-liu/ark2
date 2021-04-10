package app.datatype;

import com.owlike.genson.annotation.JsonProperty;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;
import org.hyperledger.fabric.shim.ledger.CompositeKey;

import app.util.KeyGeneration;

@DataType
public final class Post implements KeyGeneration {
    @Property()
    private final String timestamp;

    @Property
    private final String content;

    /**
     * userId who creates the post
     */
    @Property
    private final String userId;

    /**
     * sign(privateKey, hash(timestamp, content, userId))
     */
    @Property
    private final String signature;

    public String getTimestamp() {
        return timestamp;
    }

    public String getContent() {
        return content;
    }

    public String getUserId() {
        return userId;
    }

    public String getSignature() {
        return signature;
    }

    public Post(@JsonProperty("timestamp") final String timestamp, @JsonProperty("content") final String content,
            @JsonProperty("userId") final String userId, @JsonProperty("signature") final String signature) {
        this.timestamp = timestamp;
        this.content = content;
        this.userId = userId;
        this.signature = signature;
    }

    @Override
    public CompositeKey generateCompositeKey(final String salt) {
        return new CompositeKey(getObjectTypeName(), userId, signature, salt);
    }

    public static String getObjectTypeName() {
        return "POST";
    }
}
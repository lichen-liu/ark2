package app.datatype;

import com.owlike.genson.annotation.JsonProperty;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;
import org.hyperledger.fabric.shim.ledger.CompositeKey;

import app.policy.ComparableByRelativeOrder;
import app.policy.ComparableByTimestamp;
import app.policy.KeyGeneration;
import app.policy.SignatureVerification;

@DataType
public final class Post
        implements KeyGeneration, ComparableByTimestamp, ComparableByRelativeOrder, SignatureVerification {
    @Property()
    private final String timestamp;

    @Property
    private final String content;

    /**
     * userId who creates the post, the public key for the signature
     */
    @Property
    private final String userId;

    /**
     * sign(privateKey, hash(timestamp, content, userId))
     */
    @Property
    private final String signature;

    @Property
    private final long relativeOrder;

    @Override
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

    @Override
    public long getRelativeOrder() {
        return relativeOrder;
    }

    public Post(@JsonProperty("timestamp") final String timestamp, @JsonProperty("content") final String content,
            @JsonProperty("userId") final String userId, @JsonProperty("signature") final String signature,
            @JsonProperty("relativeOrder") final long relativeOrder) {
        this.timestamp = timestamp;
        this.content = content;
        this.userId = userId;
        this.signature = signature;
        this.relativeOrder = relativeOrder;
    }

    @Override
    public String generateKey(final String salt) {
        return new CompositeKey(getObjectTypeName(), userId, String.valueOf(relativeOrder), salt).toString();
    }

    public static String getObjectTypeName() {
        return Post.class.getSimpleName();
    }

    @Override
    public String getExpectedSignatureContent() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getSignatureForVerification() {
        return getSignature();
    }

    @Override
    public String getVerificationKey() {
        return getUserId();
    }
}
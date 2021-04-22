package app;

import javax.annotation.Nullable;

import com.owlike.genson.annotation.JsonProperty;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;
import org.hyperledger.fabric.shim.ledger.CompositeKey;

import app.policy.ComparableByRelativeOrder;
import app.policy.ComparableByTimestamp;
import app.policy.KeyGeneration;

@DataType
public final class Like implements KeyGeneration, ComparableByTimestamp, ComparableByRelativeOrder {
    @Property
    private final String timestamp;

    @Property
    private final String postKey;

    /**
     * userId who likes the post, the public key for the signature
     */
    @Property
    private final String userId;

    /**
     * sign(privateKey, hash(timestamp, postKey, userId))
     */
    @Property
    private final String signature;

    @Property
    private @Nullable String pointTransactionKey;

    @Property
    private final long relativeOrder;

    @Override
    public String getTimestamp() {
        return timestamp;
    }

    public String getPostKey() {
        return postKey;
    }

    public String getUserId() {
        return userId;
    }

    public String getSignature() {
        return signature;
    }

    public void setPointTransactionKey(final String pointTransactionKey) {
        this.pointTransactionKey = pointTransactionKey;
    }

    public String getPointTransactionKey() {
        return pointTransactionKey;
    }

    @Override
    public long getRelativeOrder() {
        return relativeOrder;
    }

    public Like(@JsonProperty("timestamp") final String timestamp, @JsonProperty("postKey") final String postKey,
            @JsonProperty("userId") final String userId, @JsonProperty("signature") final String signature,
            @JsonProperty("pointTransactionKey") @Nullable final String pointTransactionKey,
            @JsonProperty("relativeOrder") final long relativeOrder) {
        this.timestamp = timestamp;
        this.postKey = postKey;
        this.userId = userId;
        this.signature = signature;
        this.pointTransactionKey = pointTransactionKey;
        this.relativeOrder = relativeOrder;
    }

    @Override
    public CompositeKey generateKey(final String salt) {
        return new CompositeKey(getObjectTypeName(), postKey, String.valueOf(relativeOrder), salt);
    }

    @Override
    public boolean isMatchingObjectType(final String objectType) {
        return getObjectTypeName().equals(objectType);
    }

    public static String getObjectTypeName() {
        return Like.class.getSimpleName();
    }
}

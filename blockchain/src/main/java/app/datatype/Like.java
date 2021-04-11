package app.datatype;

import com.owlike.genson.annotation.JsonProperty;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;
import org.hyperledger.fabric.shim.ledger.CompositeKey;

import app.util.ComparableByRelativeOrder;
import app.util.ComparableByTimestamp;
import app.util.KeyGeneration;

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

    @Property
    private final String pointTransactionKey;

    /**
     * sign(privateKey, hash(timestamp, postKey, userId, pointTransactionKey))
     */
    @Property
    private final String signature;

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

    public String getPointTransactionKey() {
        return pointTransactionKey;
    }

    public String getSignature() {
        return signature;
    }

    @Override
    public long getRelativeOrder() {
        return relativeOrder;
    }

    public Like(@JsonProperty("timestamp") final String timestamp, @JsonProperty("postKey") final String postKey,
            @JsonProperty("userId") final String userId,
            @JsonProperty("pointTransactionKey") final String pointTransactionKey,
            @JsonProperty("signature") final String signature,
            @JsonProperty("relativeOrder") final long relativeOrder) {
        this.timestamp = timestamp;
        this.postKey = postKey;
        this.userId = userId;
        this.pointTransactionKey = pointTransactionKey;
        this.signature = signature;
        this.relativeOrder = relativeOrder;
    }

    @Override
    public String generateKey(final String salt) {
        return new CompositeKey(getObjectTypeName(), postKey, signature, salt).toString();
    }

    public static String getObjectTypeName() {
        return "LIKE";
    }
}

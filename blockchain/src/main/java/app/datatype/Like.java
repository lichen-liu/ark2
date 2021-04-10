package app.datatype;

import com.owlike.genson.annotation.JsonProperty;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

@DataType
public final class Like {
    @Property
    private final String timestamp;

    @Property
    private final String postKey;

    /**
     * userId who likes the post
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

    public Like(@JsonProperty("timestamp") final String timestamp, @JsonProperty("postKey") final String postKey,
            @JsonProperty("userId") final String userId,
            @JsonProperty("pointTransactionKey") final String pointTransactionKey,
            @JsonProperty("signature") final String signature) {
        this.timestamp = timestamp;
        this.postKey = postKey;
        this.userId = userId;
        this.pointTransactionKey = pointTransactionKey;
        this.signature = signature;
    }
}

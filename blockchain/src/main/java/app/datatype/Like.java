package app.datatype;

import com.owlike.genson.annotation.JsonProperty;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

@DataType
public final class Like {
    @Property
    private final String timestamp;

    @Property
    private final String postId;

    /**
     * userId who likes the post
     */
    @Property
    private final String userId;

    @Property
    private final String pointTransactionId;

    /**
     * sign(privateKey, hash(timestamp, postId, userId, pointTransactionId))
     */
    @Property
    private final String signature;

    public String getTimestamp() {
        return timestamp;
    }

    public String getPostId() {
        return postId;
    }

    public String getUserId() {
        return userId;
    }

    public String getPointTransactionId() {
        return pointTransactionId;
    }

    public String getSignature() {
        return signature;
    }

    public Like(@JsonProperty("timestamp") final String timestamp, @JsonProperty("postId") final String postId,
            @JsonProperty("userId") final String userId,
            @JsonProperty("pointTransactionId") final String pointTransactionId,
            @JsonProperty("signature") final String signature) {
        this.timestamp = timestamp;
        this.postId = postId;
        this.userId = userId;
        this.pointTransactionId = pointTransactionId;
        this.signature = signature;
    }
}

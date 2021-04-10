package app;

import com.owlike.genson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

@DataType
public final class Post {
    @Property()
    private final String postId;

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

    public String getPostId() {
        return postId;
    }

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

    public Post(@JsonProperty("postId") String postId, @JsonProperty("timestamp") String timestamp,
            @JsonProperty("content") String content, @JsonProperty("userId") String userId,
            @JsonProperty("signature") String signature) {
        this.postId = postId;
        this.timestamp = timestamp;
        this.content = content;
        this.userId = userId;
        this.signature = signature;
    }
}
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
     * sign(privateKey, hash(timestamp, content))
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

    public String getSignature() {
        return signature;
    }

    public Post(@JsonProperty("postId") String postId, @JsonProperty("timestamp") String timestamp,
            @JsonProperty("content") String content, @JsonProperty("signature") String signature) {
        this.postId = postId;
        this.timestamp = timestamp;
        this.content = content;
        this.signature = signature;
    }
}
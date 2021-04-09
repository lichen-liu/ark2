package app;

import com.owlike.genson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

@DataType
public final class Post {

    @Property()
    private final String id;

    @Property()
    private final String timestamp;

    @Property
    private final String content;

    @Property
    private final String signature;

    public String getPostId() {
        return id;
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

    public Post(@JsonProperty("postId") final String id, @JsonProperty("timestamp") final String timestamp,
            @JsonProperty("content") final String content, @JsonProperty("signature") final String signature) {
        this.id = id;
        this.timestamp = timestamp;
        this.content = content;
        this.signature = signature;
    }
}
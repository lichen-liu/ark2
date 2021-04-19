package app.repository.data;

import lombok.ToString;

@ToString(callSuper=true, includeFieldNames=true)
public class Post {
    public String timestamp;

    public String content;

    /**
     * userId who creates the post, the public key for the signature
     */
    public String userId;

    /**
     * sign(privateKey, hash(timestamp, content, userId))
     */
    public String signature;
}

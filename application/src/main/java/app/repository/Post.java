package app.repository;

import lombok.ToString;

@ToString(includeFieldNames = true)
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

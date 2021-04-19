package app.repository.data;

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

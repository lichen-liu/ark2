package app.repository.data;

import lombok.ToString;

@ToString(includeFieldNames=true)
public class Like {
    public String timestamp;

    public String postKey;

    /**
     * userId who likes the post, the public key for the signature
     */
    public String userId;

    /**
     * sign(privateKey, hash(timestamp, postKey, userId))
     */
    public String signature;

    public String pointTransactionKey;

    public long relativeOrder;
}

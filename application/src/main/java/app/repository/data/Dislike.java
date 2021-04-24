package app.repository.data;

import lombok.ToString;

@ToString(includeFieldNames = true)
public final class Dislike {

    public String timestamp;

    public String postKey;

    /**
     * userId who dislikes the post, the public key for the signature
     */

    public String userId;

    /**
     * sign(private, hash(timestamp, postKey, userId))
     */

    public String signature;

    public String pointTransactionKey;

    public long relativeOrder;
}

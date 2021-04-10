package app;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.owlike.genson.Genson;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.CompositeKey;

import app.datatype.Like;
import app.datatype.PointTransaction;
import app.datatype.PointTransactionElement;
import app.datatype.Post;

@Contract(name = "Agreements", info = @Info(title = "Agreements contract", description = "A java chaincode example", version = "0.0.1-SNAPSHOT"))

@Default
public final class ForumRepository implements ContractInterface {
    private final Genson genson = new Genson();

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void initLedger(final Context ctx) {
        final ChaincodeStub stub = ctx.getStub();

        final Post post = new Post("timestamp", "content1", "author1", "signature1");
        final PointTransaction pointTransaction = new PointTransaction("now", new PointTransactionElement("user0", 100),
                "ref", "sig", new PointTransactionElement[] { new PointTransactionElement("user1", 50),
                        new PointTransactionElement("user2", 50) });
        final Like like = new Like("future", "1", "user0", "id0", "donaldtrump");

        // mock API
        stub.putStringState("post_id_0", genson.serialize(post));
        stub.putStringState("point_transaction_id_0", genson.serialize(pointTransaction));
        stub.putStringState("like_id_0", genson.serialize(like));

        // real API
        this.publishNewPost(ctx, "future", "I am smart", "user007", "signature(user007)");
        // stub.invokeChaincodeWithStringArgs("publishNewPost", "future", "I am smart",
        // "user007", "signature(user007)");

        System.out.println("initLedger DONE!");
    }

    // @Transaction()
    // public Post createPost(final Context ctx, final String key, final String id,
    // final String timestamp,
    // final String content, final String signature) {
    // ChaincodeStub stub = ctx.getStub();
    // String postState = tryGetStateByKey(stub, key);

    // if (!postState.isEmpty()) {
    // String errorMessage = String.format("Post %s already exists", id);
    // System.out.println(errorMessage);
    // throw new ChaincodeException(errorMessage, "Post already exists");
    // }

    // Post post = new Post(id, timestamp, content, signature);
    // postState = genson.serialize(post);
    // stub.putStringState(key, postState);

    // return post;
    // }

    // @Transaction()
    // public Post changePostContent(final Context ctx, final String id, final
    // String newContent) {
    // ChaincodeStub stub = ctx.getStub();
    // String postState = tryGetStateByKey(stub, id);

    // Post post = genson.deserialize(postState, Post.class);
    // Post newPost = new Post(post.getPostId(), post.getTimestamp(), newContent,
    // post.getSignature());
    // String newPostState = genson.serialize(newPost);

    // stub.putStringState(id, newPostState);

    // return newPost;
    // }

    // endregion

    @Transaction()
    public PointTransaction getPointTransaction(final Context ctx, final String key) {
        final ChaincodeStub stub = ctx.getStub();

        return genson.deserialize(tryGetStringByKey(stub, key), PointTransaction.class);
    }

    /* REVIEWED */
    /**
     * 
     * @param stub
     * @param key
     * @return
     */
    private String tryGetStringByKey(final ChaincodeStub stub, final String key) {
        final String state = stub.getStringState(key);
        if (state.isEmpty()) {
            final String errorMessage = String.format("State %s does not exist", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, "State not found");
        }
        return state;
    }

    /**
     * 
     * @param ctx
     * @param timestamp "2015-04-14T11:07:36.639Z"
     * 
     *                  <pre>
     *                  ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT)
     *                  </pre>
     * 
     * @param content
     * @param userId
     * @param signature
     * @return postKey
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String publishNewPost(final Context ctx, final String timestamp, final String content, final String userId,
            final String signature) {
        final ChaincodeStub stub = ctx.getStub();

        // TODO: verify signature
        final Post post = new Post(timestamp, content, userId, signature);
        final String postKey = post.generateKey(stub);

        stub.putStringState(postKey, genson.serialize(post));

        return postKey;
    }

    /**
     * Sorted by timestamp
     * 
     * @param ctx
     * @return postKeys
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String getAllPostKeys(final Context ctx) {
        final ChaincodeStub stub = ctx.getStub();
        final var keyValueIterator = stub.getStateByPartialCompositeKey(new CompositeKey(Post.getObjectTypeName()));
        List<String> postKeys = StreamSupport.stream(keyValueIterator.spliterator(), false)
                .sorted((keyValueLeft, keyValueRight) -> {
                    String leftPostTimestamp = genson.deserialize(keyValueLeft.getStringValue(), Post.class)
                            .getTimestamp();
                    String rightPostTimestamp = genson.deserialize(keyValueRight.getStringValue(), Post.class)
                            .getTimestamp();
                    return leftPostTimestamp.compareToIgnoreCase(rightPostTimestamp);
                }).map(keyVale -> keyVale.getKey()).collect(Collectors.toList());
        return genson.serialize(postKeys);
    }

    /**
     * Sorted by timestamp
     * 
     * @param ctx
     * @param userId
     * @return
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String getAllPostKeysByUserId(Context ctx, String userId) {
        final ChaincodeStub stub = ctx.getStub();
        final var keyValueIterator = stub.getStateByPartialCompositeKey(Post.getObjectTypeName(), userId);
        List<String> postKeys = StreamSupport.stream(keyValueIterator.spliterator(), false)
                .sorted((keyValueLeft, keyValueRight) -> {
                    String leftPostTimestamp = genson.deserialize(keyValueLeft.getStringValue(), Post.class)
                            .getTimestamp();
                    String rightPostTimestamp = genson.deserialize(keyValueRight.getStringValue(), Post.class)
                            .getTimestamp();
                    return leftPostTimestamp.compareToIgnoreCase(rightPostTimestamp);
                }).map(keyValue -> keyValue.getKey()).collect(Collectors.toList());
        return genson.serialize(postKeys);
    }

    /**
     * 
     * @param ctx
     * @param postKey
     * @return
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public Post getPostByKey(final Context ctx, final String postKey) {
        final ChaincodeStub stub = ctx.getStub();
        final String postString = this.tryGetStringByKey(stub, postKey);

        final Post post = genson.deserialize(postString, Post.class);

        return post;
    }
}

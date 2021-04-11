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
import app.util.ChaincodeStubTools;

@Contract(name = "Agreements", info = @Info(title = "Agreements contract", description = "A java chaincode example", version = "0.0.1-SNAPSHOT"))

@Default
public final class ForumRepository implements ContractInterface {
    private static final boolean shouldVerifyIntegrity = false;

    private final Genson genson = new Genson();

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void initLedger(final Context ctx) {
        final ChaincodeStub stub = ctx.getStub();

        // mock API
        final PointTransaction pointTransaction = new PointTransaction("now", new PointTransactionElement("user0", 100),
                "ref", "sig", new PointTransactionElement[] { new PointTransactionElement("user1", 50),
                        new PointTransactionElement("user2", 50) });
        stub.putStringState("point_transaction_id_0", genson.serialize(pointTransaction));
        final Like like = new Like("future", "1", "user0", "id0", "donaldtrump");
        stub.putStringState("like_id_0", genson.serialize(like));

        // real API
        this.publishNewPost(ctx, "future0", "I am smart", "user007", "signature(user007)");
        this.publishNewPost(ctx, "future1", "I am very smart", "user008", "signature(user008)");
        // stub.invokeChaincodeWithStringArgs("publishNewPost", "future", "I am smart",
        // "user007", "signature(user007)");

        System.out.println("initLedger DONE!");
    }

    /**
     * 
     * @param ctx
     * @param timestamp
     * 
     *                  <pre>
     *                  ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT); // "2015-04-14T11:07:36.639Z"
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

        final Post post = new Post(timestamp, content, userId, signature);
        if (shouldVerifyIntegrity) {
            if (!post.isMatchingSignature()) {
                final String errorMessage = genson.serialize(post) + " has non-matching signature";
                throw new ChaincodeException(errorMessage, errorMessage);
            }
        }
        final String postKey = post.generateKey(key -> ChaincodeStubTools.isKeyExisted(stub, key));

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
    public String[] getAllPostKeys(final Context ctx) {
        final ChaincodeStub stub = ctx.getStub();
        final var keyValueIterator = stub.getStateByPartialCompositeKey(new CompositeKey(Post.getObjectTypeName()));
        final List<String> postKeys = StreamSupport.stream(keyValueIterator.spliterator(), false)
                .sorted((keyValueLeft, keyValueRight) -> {
                    final var leftPost = genson.deserialize(keyValueLeft.getStringValue(), Post.class);
                    final var rightPost = genson.deserialize(keyValueRight.getStringValue(), Post.class);
                    return leftPost.compareToByTimestamp(rightPost);
                }).map(keyVale -> keyVale.getKey()).collect(Collectors.toList());
        return postKeys.toArray(String[]::new);
    }

    /**
     * Sorted by timestamp
     * 
     * @param ctx
     * @param userId
     * @return
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String[] getAllPostKeysByUserId(final Context ctx, final String userId) {
        final ChaincodeStub stub = ctx.getStub();
        final var keyValueIterator = stub.getStateByPartialCompositeKey(Post.getObjectTypeName(), userId);
        final List<String> postKeys = StreamSupport.stream(keyValueIterator.spliterator(), false)
                .sorted((keyValueLeft, keyValueRight) -> {
                    final var leftPost = genson.deserialize(keyValueLeft.getStringValue(), Post.class);
                    final var rightPost = genson.deserialize(keyValueRight.getStringValue(), Post.class);
                    return leftPost.compareToByTimestamp(rightPost);
                }).map(keyValue -> keyValue.getKey()).collect(Collectors.toList());
        return postKeys.toArray(String[]::new);
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
        final String postString = ChaincodeStubTools.tryGetStringStateByKey(stub, postKey);
        return genson.deserialize(postString, Post.class);
    }

    /**
     * 
     * @param ctx
     * @param postKey
     * @return
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String[] getAllLikeKeysByPostKey(final Context ctx, final String postKey) {
        final ChaincodeStub stub = ctx.getStub();
        final var keyValueIterator = stub.getStateByPartialCompositeKey(Like.getObjectTypeName(), postKey);
        final List<String> likeKeys = StreamSupport.stream(keyValueIterator.spliterator(), false)
                .sorted((keyValueLeft, keyValueRight) -> {
                    final var leftLike = genson.deserialize(keyValueLeft.getStringValue(), Like.class);
                    final var rightLike = genson.deserialize(keyValueRight.getStringValue(), Like.class);
                    return leftLike.compareToByTimestamp(rightLike);
                }).map(keyValue -> keyValue.getKey()).collect(Collectors.toList());
        return likeKeys.toArray(String[]::new);
    }

    /**
     * 
     * @param ctx
     * @param likeKey
     * @return
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public Like getLikeByKey(final Context ctx, final String likeKey) {
        final ChaincodeStub stub = ctx.getStub();
        final String likeString = ChaincodeStubTools.tryGetStringStateByKey(stub, likeKey);
        return genson.deserialize(likeString, Like.class);
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public double getPointAmountByUserId(final Context ctx, final String userId) {
        final ChaincodeStub stub = ctx.getStub();
        final double totalPointAmount = 0;
        // TODO
        return totalPointAmount;
    }
}

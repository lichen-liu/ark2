package app;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.owlike.genson.Genson;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.CompositeKey;

import app.datatype.Like;
import app.datatype.Post;
import app.util.ChaincodeStubTools;

public class ForumRepositoryCC {
    private static final boolean shouldVerifyIntegrity = false;

    private final Genson genson = new Genson();

    public String publishNewPost(final Context ctx, final String timestamp, final String content, final String userId,
            final String signature) throws Exception {
        final ChaincodeStub stub = ctx.getStub();

        final Post post = new Post(timestamp, content, userId, signature, this.determineRelativeOrderForPost(ctx));
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

    public String[] getAllPostKeys(final Context ctx) throws Exception {
        final ChaincodeStub stub = ctx.getStub();
        final var keyValueIterator = stub.getStateByPartialCompositeKey(new CompositeKey(Post.getObjectTypeName()));
        final List<String> postKeys = StreamSupport.stream(keyValueIterator.spliterator(), false)
                .sorted((keyValueLeft, keyValueRight) -> {
                    final var leftPost = genson.deserialize(keyValueLeft.getStringValue(), Post.class);
                    final var rightPost = genson.deserialize(keyValueRight.getStringValue(), Post.class);
                    return rightPost.compareToByRelativeOrder(leftPost);
                }).map(keyVale -> keyVale.getKey()).collect(Collectors.toList());
        keyValueIterator.close();
        return postKeys.toArray(String[]::new);
    }

    public String[] getAllPostKeysByUserId(final Context ctx, final String userId) throws Exception {
        final ChaincodeStub stub = ctx.getStub();
        final var keyValueIterator = stub.getStateByPartialCompositeKey(Post.getObjectTypeName(), userId);
        final List<String> postKeys = StreamSupport.stream(keyValueIterator.spliterator(), false)
                .sorted((keyValueLeft, keyValueRight) -> {
                    final var leftPost = genson.deserialize(keyValueLeft.getStringValue(), Post.class);
                    final var rightPost = genson.deserialize(keyValueRight.getStringValue(), Post.class);
                    return rightPost.compareToByRelativeOrder(leftPost);
                }).map(keyValue -> keyValue.getKey()).collect(Collectors.toList());
        keyValueIterator.close();
        return postKeys.toArray(String[]::new);
    }

    public Post getPostByKey(final Context ctx, final String postKey) {
        final ChaincodeStub stub = ctx.getStub();
        final String postString = ChaincodeStubTools.tryGetStringStateByKey(stub, postKey);
        return genson.deserialize(postString, Post.class);
    }

    public String[] getAllLikeKeysByPostKey(final Context ctx, final String postKey) throws Exception {
        final ChaincodeStub stub = ctx.getStub();
        final var keyValueIterator = stub.getStateByPartialCompositeKey(Like.getObjectTypeName(), postKey);
        final List<String> likeKeys = StreamSupport.stream(keyValueIterator.spliterator(), false)
                .sorted((keyValueLeft, keyValueRight) -> {
                    final var leftLike = genson.deserialize(keyValueLeft.getStringValue(), Like.class);
                    final var rightLike = genson.deserialize(keyValueRight.getStringValue(), Like.class);
                    return rightLike.compareToByRelativeOrder(leftLike);
                }).map(keyValue -> keyValue.getKey()).collect(Collectors.toList());
        keyValueIterator.close();
        return likeKeys.toArray(String[]::new);
    }

    public Like getLikeByKey(final Context ctx, final String likeKey) {
        final ChaincodeStub stub = ctx.getStub();
        final String likeString = ChaincodeStubTools.tryGetStringStateByKey(stub, likeKey);
        return genson.deserialize(likeString, Like.class);
    }

    public String[] getAllPointTransactionKeys(final Context ctx) throws Exception {
        return null;
    }

    public double getPointAmountByUserId(final Context ctx, final String userId) {
        final ChaincodeStub stub = ctx.getStub();
        final double totalPointAmount = 0;
        // TODO
        return totalPointAmount;
    }

    private long determineRelativeOrderForPost(final Context ctx) throws Exception {
        final String[] keys = this.getAllPostKeys(ctx);
        if (keys.length == 0) {
            return 0L;
        }
        final Post recentPost = this.getPostByKey(ctx, keys[0]);
        return Math.max(keys.length, recentPost.getRelativeOrder() + 1);
    }

    private long determineRelativeOrderForLike(final Context ctx, final String postKey) throws Exception {
        final String[] keys = this.getAllLikeKeysByPostKey(ctx, postKey);
        if (keys.length == 0) {
            return 0L;
        }
        final Like recentLike = this.getLikeByKey(ctx, keys[0]);
        return Math.max(keys.length, recentLike.getRelativeOrder() + 1);
    }
}

package app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.annotation.Nullable;

import com.owlike.genson.Genson;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.CompositeKey;
import org.hyperledger.fabric.shim.ledger.KeyValue;

import app.datatype.Like;
import app.datatype.PointTransaction;
import app.datatype.Post;
import app.policy.LikeRewarding;
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
        final String postKey = ChaincodeStubTools.generateKey(stub, post);

        stub.putStringState(postKey, genson.serialize(post));

        return postKey;
    }

    public String publishNewPointTransaction(final Context ctx, final String timestamp, final String payerEntryString,
            final String issuerUserId, final String signature, final String reference, final String payeeEntriesString)
            throws Exception {

        final ChaincodeStub stub = ctx.getStub();
        final var payerEntry = genson.deserialize(payerEntryString, PointTransaction.Entry.class);
        final var payeeEntries = genson.deserialize(payeeEntriesString, PointTransaction.Entry[].class);

        final PointTransaction.Tracking payerPointTransactionTracking = this
                .determinePointTransactionTrackingForUserId(ctx, payerEntry.getUserId());

        final var pointTransaction = new PointTransaction(timestamp, payerEntry, issuerUserId, signature, reference,
                payeeEntries, this.determineRelativeOrderForPointTransaction(ctx), payerPointTransactionTracking);

        final String pointTransactionKey = ChaincodeStubTools.generateKey(stub, pointTransaction);

        stub.putStringState(pointTransactionKey, genson.serialize(pointTransaction));

        return pointTransactionKey;
    }

    public String publishNewLike(final Context ctx, final String timestamp, final String postKey,
            final String payerEntryString, final String likeSignature, final String pointTransactionSignature)
            throws Exception {
        final ChaincodeStub stub = ctx.getStub();

        final var payerEntry = genson.deserialize(payerEntryString, PointTransaction.Entry.class);
        final Post post = this.getPostByKey(ctx, postKey);
        final KeyValue[] postLikesKeyValue = this.getAllLikesByPostKey(ctx, postKey);

        final long existingNumberLikes = postLikesKeyValue.length;
        final long existingTotalNumberLikes = ChaincodeStubTools.getNumberStatesByPartialCompositeKey(stub,
                new CompositeKey(Like.getObjectTypeName()));
        final LikeRewarding rewarding = new LikeRewarding(existingNumberLikes, existingTotalNumberLikes,
                payerEntry.getPointAmount());

        final List<PointTransaction.Entry> payeeEntries = new ArrayList<PointTransaction.Entry>();
        payeeEntries.add(new PointTransaction.Entry(post.getUserId(), rewarding.determineAuthorRewarding()));
        for (int idx = postLikesKeyValue.length - 1; idx >= 0; idx--) {
            final long likerRank = postLikesKeyValue.length - 1 - idx;
            if (!rewarding.isLikerRewarded(likerRank)) {
                break;
            }
            final Like currentLike = genson.deserialize(postLikesKeyValue[idx].getStringValue(), Like.class);
            payeeEntries.add(
                    new PointTransaction.Entry(currentLike.getUserId(), rewarding.determineLikerRewarding(likerRank)));
        }

        System.out.println("publishNewLike");
        System.out.println("payer: " + genson.serialize(payerEntry));
        System.out.println("payees: " + genson.serialize(payeeEntries));

        final var like = new Like(timestamp, postKey, payerEntry.getUserId(), likeSignature, null,
                this.determineRelativeOrderForLike(ctx, postKey));
        final String likeKey = ChaincodeStubTools.generateKey(stub, like);

        final String pointTransactionKey = this.publishNewPointTransaction(ctx, timestamp, payerEntryString,
                payerEntry.getUserId(), pointTransactionSignature, likeKey,
                genson.serialize(payeeEntries.toArray(PointTransaction.Entry[]::new)));
        like.setPointTransactionKey(pointTransactionKey);

        stub.putStringState(likeKey, genson.serialize(like));
        return likeKey;
    }

    public String[] getAllPostKeys(final Context ctx) throws Exception {
        final List<String> postKeys = Arrays.stream(this.getAllPosts(ctx, null)).map(keyValue -> keyValue.getKey())
                .collect(Collectors.toList());
        return postKeys.toArray(String[]::new);
    }

    public String[] getAllPostKeysByUserId(final Context ctx, final String userId) throws Exception {
        final List<String> postKeys = Arrays.stream(this.getAllPosts(ctx, userId)).map(keyValue -> keyValue.getKey())
                .collect(Collectors.toList());
        return postKeys.toArray(String[]::new);
    }

    private KeyValue[] getAllPosts(final Context ctx, @Nullable final String userId) throws Exception {
        final ChaincodeStub stub = ctx.getStub();
        final CompositeKey partialCompositeKey = userId == null ? new CompositeKey(Post.getObjectTypeName())
                : new CompositeKey(Post.getObjectTypeName(), userId);
        final var keyValueIterator = stub.getStateByPartialCompositeKey(partialCompositeKey);
        final List<KeyValue> postKeys = StreamSupport.stream(keyValueIterator.spliterator(), false)
                .sorted((leftKeyValue, rightKeyValue) -> {
                    final var leftPost = genson.deserialize(leftKeyValue.getStringValue(), Post.class);
                    final var rightPost = genson.deserialize(rightKeyValue.getStringValue(), Post.class);
                    return rightPost.compareToByRelativeOrder(leftPost);
                }).collect(Collectors.toList());
        keyValueIterator.close();
        return postKeys.toArray(KeyValue[]::new);
    }

    public Post getPostByKey(final Context ctx, final String postKey) {
        final ChaincodeStub stub = ctx.getStub();
        final String postString = ChaincodeStubTools.tryGetStringStateByKey(stub, postKey);
        return genson.deserialize(postString, Post.class);
    }

    public String[] getAllLikeKeysByPostKey(final Context ctx, final String postKey) throws Exception {
        final List<String> likeKeys = Arrays.stream(this.getAllLikesByPostKey(ctx, postKey))
                .map(keyValue -> keyValue.getKey()).collect(Collectors.toList());
        return likeKeys.toArray(String[]::new);
    }

    private KeyValue[] getAllLikesByPostKey(final Context ctx, final String postKey) throws Exception {
        final ChaincodeStub stub = ctx.getStub();
        final var keyValueIterator = stub.getStateByPartialCompositeKey(Like.getObjectTypeName(), postKey);
        final List<KeyValue> likes = StreamSupport.stream(keyValueIterator.spliterator(), false)
                .sorted((leftKeyValue, rightKeyValue) -> {
                    final var leftLike = genson.deserialize(leftKeyValue.getStringValue(), Like.class);
                    final var rightLike = genson.deserialize(rightKeyValue.getStringValue(), Like.class);
                    return rightLike.compareToByRelativeOrder(leftLike);
                }).collect(Collectors.toList());
        keyValueIterator.close();
        return likes.toArray(KeyValue[]::new);
    }

    public Like getLikeByKey(final Context ctx, final String likeKey) {
        final ChaincodeStub stub = ctx.getStub();
        final String likeString = ChaincodeStubTools.tryGetStringStateByKey(stub, likeKey);
        return genson.deserialize(likeString, Like.class);
    }

    public String[] getAllPointTransactionKeys(final Context ctx) throws Exception {
        final List<String> pointTransactionKeys = Arrays.stream(this.getAllPointTransactions(ctx, null))
                .map(keyValue -> keyValue.getKey()).collect(Collectors.toList());
        return pointTransactionKeys.toArray(String[]::new);
    }

    public String[] getAllPointTransactionKeysByPayerUserId(final Context ctx, final String payerUserId)
            throws Exception {
        final List<String> pointTransactionKeys = Arrays.stream(this.getAllPointTransactions(ctx, payerUserId))
                .map(keyValue -> keyValue.getKey()).collect(Collectors.toList());
        return pointTransactionKeys.toArray(String[]::new);
    }

    private KeyValue[] getAllPointTransactions(final Context ctx, @Nullable final String payerUserId) throws Exception {
        final ChaincodeStub stub = ctx.getStub();
        final CompositeKey partialCompositeKey = payerUserId == null
                ? new CompositeKey(PointTransaction.getObjectTypeName())
                : new CompositeKey(PointTransaction.getObjectTypeName(), payerUserId);
        final var keyValueIterator = stub.getStateByPartialCompositeKey(partialCompositeKey);
        final List<KeyValue> pointTransactionKeys = StreamSupport.stream(keyValueIterator.spliterator(), false)
                .sorted((leftKeyValue, rightKeyValue) -> {
                    final var leftPointTransaction = genson.deserialize(leftKeyValue.getStringValue(),
                            PointTransaction.class);
                    final var rightPointTransaction = genson.deserialize(rightKeyValue.getStringValue(),
                            PointTransaction.class);
                    return rightPointTransaction.compareToByRelativeOrder(leftPointTransaction);
                }).collect(Collectors.toList());
        keyValueIterator.close();
        return pointTransactionKeys.toArray(KeyValue[]::new);
    }

    public PointTransaction getPointTransactionByKey(final Context ctx, final String pointTransactionKey) {
        final ChaincodeStub stub = ctx.getStub();
        final String pointTransactionString = ChaincodeStubTools.tryGetStringStateByKey(stub, pointTransactionKey);
        return genson.deserialize(pointTransactionString, PointTransaction.class);
    }

    public double getPointAmountByUserId(final Context ctx, final String userId) throws Exception {
        double totalEarningPointAmount = 0;
        double totalSpendingPointAmount = 0;

        PointTransaction.Tracking nextTracking = this.determinePointTransactionTrackingForUserId(ctx, userId);

        while (nextTracking != null) {
            final var tracking = nextTracking;
            nextTracking = null;
            double earningPointAmount = 0;
            for (final String earningKey : tracking.getRecentEarningPointTransactionKeys()) {
                final var earningPointTransaction = this.getPointTransactionByKey(ctx, earningKey);
                for (final var earningPointTransactionPayee : earningPointTransaction.getPayeeEntries()) {
                    if (userId.equals(earningPointTransactionPayee.getUserId())) {
                        earningPointAmount += earningPointTransactionPayee.getPointAmount();
                    }
                }
            }
            double spendingPointAmount = 0;
            final String spendingKey = tracking.getRecentSpendingPointTransactionKey();
            if (spendingKey != null) {
                final var spendingPointTransaction = this.getPointTransactionByKey(ctx, spendingKey);
                final var spendingPointTransactionPayer = spendingPointTransaction.getPayerEntry();
                if (userId.equals(spendingPointTransactionPayer.getUserId())) {
                    spendingPointAmount += spendingPointTransactionPayer.getPointAmount();
                    nextTracking = spendingPointTransaction.getPayerPointTransactionTracking();
                }
            }

            totalEarningPointAmount += earningPointAmount;
            totalSpendingPointAmount += spendingPointAmount;
        }

        System.out.println("getPointAmountByUserId(" + userId + "): " + "totalEarningPointAmount="
                + totalEarningPointAmount + ", totalSpendingPointAmount=" + totalSpendingPointAmount);
        return totalEarningPointAmount - totalSpendingPointAmount;
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

    private long determineRelativeOrderForPointTransaction(final Context ctx) throws Exception {
        final String[] keys = this.getAllPointTransactionKeys(ctx);
        if (keys.length == 0) {
            return 0L;
        }
        final PointTransaction recentPointTransaction = this.getPointTransactionByKey(ctx, keys[0]);
        return Math.max(keys.length, recentPointTransaction.getRelativeOrder() + 1);
    }

    private PointTransaction.Tracking determinePointTransactionTrackingForUserId(final Context ctx, final String userId)
            throws Exception {
        final String[] spendingKeys = this.getAllPointTransactionKeysByPayerUserId(ctx, userId);
        final String recentSpendingPointTransactionKey = spendingKeys.length > 0 ? spendingKeys[0] : null;

        final List<String> recentEarningPointTransactionKeys = new ArrayList<String>();
        final KeyValue[] allPointTransactions = this.getAllPointTransactions(ctx, null);
        for (final KeyValue pointTransactionKV : allPointTransactions) {
            if (pointTransactionKV.getKey().equals(recentSpendingPointTransactionKey)) {
                break;
            }
            final var pointTransaction = genson.deserialize(pointTransactionKV.getStringValue(),
                    PointTransaction.class);
            if (Arrays.stream(pointTransaction.getPayeeEntries()).anyMatch(payee -> payee.getUserId().equals(userId))) {
                recentEarningPointTransactionKeys.add(pointTransactionKV.getKey());
            }
        }

        return new PointTransaction.Tracking(recentSpendingPointTransactionKey,
                recentEarningPointTransactionKeys.toArray(String[]::new));
    }

    public ForumRepositoryCC() {
    }
}

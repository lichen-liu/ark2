package app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.annotation.Nullable;

import com.owlike.genson.Genson;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.CompositeKey;
import org.hyperledger.fabric.shim.ledger.KeyValue;

import app.policy.LikeRewarding;
import app.util.ChaincodeStubTools;
import app.util.ChaincodeStubTools.Key;

public class ForumRepositoryCC {
    private static final boolean shouldVerifyIntegrity = false;

    // private final Genson genson = new
    // GensonBuilder().failOnMissingProperty(true).create();
    private final Genson genson = new Genson();

    public String publishNewPost(final Context ctx, final String timestamp, final String content, final String userId,
            final String signature) throws Exception {
        final ChaincodeStub stub = ctx.getStub();

        final Post post = new Post(timestamp, content, userId, signature);
        if (shouldVerifyIntegrity) {
            if (!post.isMatchingSignature()) {
                final String errorMessage = genson.serialize(post) + " has non-matching signature";
                throw new ChaincodeException(errorMessage, errorMessage);
            }
        }
        final Key postKey = ChaincodeStubTools.generateKey(stub, post);

        ChaincodeStubTools.putStringState(stub, postKey, genson.serialize(post));

        return postKey.getBase64UrlKeyString();
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

        final Key pointTransactionKey = ChaincodeStubTools.generateKey(stub, pointTransaction);

        ChaincodeStubTools.putStringState(stub, pointTransactionKey, genson.serialize(pointTransaction));

        return pointTransactionKey.getBase64UrlKeyString();
    }

    public String publishNewLike(final Context ctx, final String timestamp, final String postKey,
            final String payerEntryString, final String likeSignature, final String pointTransactionSignature)
            throws Exception {
        final ChaincodeStub stub = ctx.getStub();

        final var payerEntry = genson.deserialize(payerEntryString, PointTransaction.Entry.class);
        final Post post = this.getPostByKey(ctx, postKey);
        final KeyValue[] postLikesKeyValue = this.getAllLikesByPostKey(ctx, postKey);

        final LikeRewarding rewarding = new LikeRewarding(postLikesKeyValue.length, payerEntry.getPointAmount());

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
        final Key likeKey = ChaincodeStubTools.generateKey(stub, like);

        final String pointTransactionKey = this.publishNewPointTransaction(ctx, timestamp, payerEntryString,
                payerEntry.getUserId(), pointTransactionSignature, likeKey.getBase64UrlKeyString(),
                genson.serialize(payeeEntries.toArray(PointTransaction.Entry[]::new)));
        like.setPointTransactionKey(pointTransactionKey);

        ChaincodeStubTools.putStringState(stub, likeKey, genson.serialize(like));

        return likeKey.getBase64UrlKeyString();
    }

    public String[] getAllPostKeys(final Context ctx) throws Exception {
        final List<String> postKeys = Arrays.stream(this.getAllPosts(ctx, null))
                .map(keyValue -> Key.createFromCCKeyString(keyValue.getKey()).getBase64UrlKeyString())
                .collect(Collectors.toList());
        return postKeys.toArray(String[]::new);
    }

    public String[] getAllPostKeysByUserId(final Context ctx, final String userId) throws Exception {
        final List<String> postKeys = Arrays.stream(this.getAllPosts(ctx, userId))
                .map(keyValue -> Key.createFromCCKeyString(keyValue.getKey()).getBase64UrlKeyString())
                .collect(Collectors.toList());
        return postKeys.toArray(String[]::new);
    }

    public Post getPostByKey(final Context ctx, final String postKey) throws IllegalArgumentException {
        final ChaincodeStub stub = ctx.getStub();
        final Key key = Key.createFromBase64UrlKeyString(postKey);
        if (!Post.getObjectTypeName().equals(key.getObjectTypeString())) {
            throw new ChaincodeException("getPostByKey(): key is not a PostKey", key.getObjectTypeString());
        }
        final String postString = ChaincodeStubTools.tryGetStringStateByKey(stub, key);
        return genson.deserialize(postString, Post.class);
    }

    public String[] getAllLikeKeysByPostKey(final Context ctx, final String postKey) throws Exception {
        final List<String> likeKeys = Arrays.stream(this.getAllLikesByPostKey(ctx, postKey))
                .map(keyValue -> Key.createFromCCKeyString(keyValue.getKey()).getBase64UrlKeyString())
                .collect(Collectors.toList());
        return likeKeys.toArray(String[]::new);
    }

    public Like getLikeByKey(final Context ctx, final String likeKey) throws IllegalArgumentException {
        final ChaincodeStub stub = ctx.getStub();
        final Key key = Key.createFromBase64UrlKeyString(likeKey);
        if (!Like.getObjectTypeName().equals(key.getObjectTypeString())) {
            throw new ChaincodeException("getLikeByKey(): key is not a LikeKey", key.getObjectTypeString());
        }
        final String likeString = ChaincodeStubTools.tryGetStringStateByKey(stub, key);
        return genson.deserialize(likeString, Like.class);
    }

    public String[] getAllPointTransactionKeys(final Context ctx) throws Exception {
        final List<String> pointTransactionKeys = Arrays.stream(this.getAllPointTransactions(ctx, null))
                .map(keyValue -> Key.createFromCCKeyString(keyValue.getKey()).getBase64UrlKeyString())
                .collect(Collectors.toList());
        return pointTransactionKeys.toArray(String[]::new);
    }

    public String[] getAllPointTransactionKeysByPayerUserId(final Context ctx, final String payerUserId)
            throws Exception {
        final List<String> pointTransactionKeys = Arrays.stream(this.getAllPointTransactions(ctx, payerUserId))
                .map(keyValue -> Key.createFromCCKeyString(keyValue.getKey()).getBase64UrlKeyString())
                .collect(Collectors.toList());
        return pointTransactionKeys.toArray(String[]::new);
    }

    public String[] computeAllPointTransactionKeysByUserId(final Context ctx, final String userId) throws Exception {
        final Map<String, PointTransaction> relatedPointTransactions = new HashMap<String, PointTransaction>();

        PointTransaction.Tracking nextTracking = this.determinePointTransactionTrackingForUserId(ctx, userId);

        while (nextTracking != null) {
            final var tracking = nextTracking;
            nextTracking = null;
            for (final String earningKey : tracking.getRecentEarningPointTransactionKeys()) {
                final var earningPointTransaction = this.getPointTransactionByKey(ctx, earningKey);
                if (Arrays.stream(earningPointTransaction.getPayeeEntries()).anyMatch(
                        (earningPointTransactionPayee) -> userId.equals(earningPointTransactionPayee.getUserId()))) {
                    relatedPointTransactions.put(earningKey, earningPointTransaction);
                }
            }
            final String spendingKey = tracking.getRecentSpendingPointTransactionKey();
            if (spendingKey != null) {
                final var spendingPointTransaction = this.getPointTransactionByKey(ctx, spendingKey);
                final var spendingPointTransactionPayer = spendingPointTransaction.getPayerEntry();
                if (userId.equals(spendingPointTransactionPayer.getUserId())) {
                    relatedPointTransactions.put(spendingKey, spendingPointTransaction);
                    nextTracking = spendingPointTransaction.getPayerPointTransactionTracking();
                }
            }
        }

        final List<String> keys = relatedPointTransactions.entrySet().stream().sorted((leftEntry, rightEntry) -> {
            final var leftPointTransaction = leftEntry.getValue();
            final var rightPointTransaction = rightEntry.getValue();
            return leftPointTransaction.compareToByRelativeOrder(rightPointTransaction);
        }).map(entry -> entry.getKey()).collect(Collectors.toList());
        return keys.toArray(String[]::new);
    }

    public PointTransaction getPointTransactionByKey(final Context ctx, final String pointTransactionKey)
            throws IllegalArgumentException {
        final ChaincodeStub stub = ctx.getStub();
        final Key key = Key.createFromBase64UrlKeyString(pointTransactionKey);
        if (!PointTransaction.getObjectTypeName().equals(key.getObjectTypeString())) {
            throw new ChaincodeException("getPointTransactionByKey(): key is not a PointTransactionKey",
                    key.getObjectTypeString());
        }
        final String pointTransactionString = ChaincodeStubTools.tryGetStringStateByKey(stub, key);
        return genson.deserialize(pointTransactionString, PointTransaction.class);
    }

    public double computePointBalanceByUserId(final Context ctx, final String userId) throws Exception {
        double totalEarningPointAmount = 0;
        double totalSpendingPointAmount = 0;

        final String[] pointTransactionKeys = this.computeAllPointTransactionKeysByUserId(ctx, userId);

        for (final String pointTransactionKey : pointTransactionKeys) {
            final PointTransaction pointTransaction = this.getPointTransactionByKey(ctx, pointTransactionKey);

            final var payerEntry = pointTransaction.getPayerEntry();
            if (userId.equals(payerEntry.getUserId())) {
                totalSpendingPointAmount += payerEntry.getPointAmount();
            }
            for (final var payeeEntry : pointTransaction.getPayeeEntries()) {
                if (userId.equals(payeeEntry.getUserId())) {
                    totalEarningPointAmount += payeeEntry.getPointAmount();
                }
            }
        }

        return totalEarningPointAmount - totalSpendingPointAmount;
    }

    /**
     * Sorted by timestamp, most recent first
     * 
     * @param ctx
     * @param userId
     * @return KeyValue[], key CCKeyString
     * @throws Exception
     */
    private KeyValue[] getAllPosts(final Context ctx, @Nullable final String userId) throws Exception {
        final ChaincodeStub stub = ctx.getStub();
        final CompositeKey partialCompositeKey = userId == null ? new CompositeKey(Post.getObjectTypeName())
                : new CompositeKey(Post.getObjectTypeName(), userId);
        final var keyValueIterator = stub.getStateByPartialCompositeKey(partialCompositeKey);
        final List<KeyValue> postKeys = StreamSupport.stream(keyValueIterator.spliterator(), false)
                .sorted((leftKeyValue, rightKeyValue) -> {
                    final var leftPost = genson.deserialize(leftKeyValue.getStringValue(), Post.class);
                    final var rightPost = genson.deserialize(rightKeyValue.getStringValue(), Post.class);
                    return rightPost.compareToByTimestamp(leftPost);
                }).collect(Collectors.toList());
        keyValueIterator.close();
        return postKeys.toArray(KeyValue[]::new);
    }

    /**
     * Sorted by relativeOrder, most recent first
     * 
     * @param ctx
     * @param postKey
     * @return KeyValue[], key CCKeyString
     * @throws Exception
     */
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

    /**
     * Sorted by relativeOrder, most recent first
     * 
     * @param ctx
     * @param payerUserId
     * @return KeyValue[], key CCKeyString
     * @throws Exception
     */
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

    /**
     * Tracking.recentEarningPointTransactionKeys sorted by relativeOrder, most
     * recent first
     * 
     * @param ctx
     * @param userId
     * @return
     * @throws Exception
     */
    private PointTransaction.Tracking determinePointTransactionTrackingForUserId(final Context ctx, final String userId)
            throws Exception {
        final String[] spendingKeys = this.getAllPointTransactionKeysByPayerUserId(ctx, userId);
        final String recentSpendingPointTransactionKey = spendingKeys.length > 0 ? spendingKeys[0] : null;

        final List<String> recentEarningPointTransactionKeys = new ArrayList<String>();
        final KeyValue[] allPointTransactions = this.getAllPointTransactions(ctx, null);
        for (final KeyValue pointTransactionKV : allPointTransactions) {
            if (Key.createFromCCKeyString(pointTransactionKV.getKey()).getBase64UrlKeyString()
                    .equals(recentSpendingPointTransactionKey)) {
                break;
            }
            final var pointTransaction = genson.deserialize(pointTransactionKV.getStringValue(),
                    PointTransaction.class);
            if (Arrays.stream(pointTransaction.getPayeeEntries()).anyMatch(payee -> payee.getUserId().equals(userId))) {
                recentEarningPointTransactionKeys
                        .add(Key.createFromCCKeyString(pointTransactionKV.getKey()).getBase64UrlKeyString());
            }
        }

        return new PointTransaction.Tracking(recentSpendingPointTransactionKey,
                recentEarningPointTransactionKeys.toArray(String[]::new));
    }

    public ForumRepositoryCC() {
    }
}

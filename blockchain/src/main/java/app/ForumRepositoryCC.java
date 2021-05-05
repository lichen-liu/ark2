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

import app.ChaincodeStubTools.Key;
import app.policy.DislikeRewarding;
import app.policy.KeyGeneration;
import app.policy.LikeRewarding;

public class ForumRepositoryCC {
    // private final Genson genson = new
    // GensonBuilder().failOnMissingProperty(true).create();
    private final Genson genson = new Genson();

    public String publishNewPost(final Context ctx, final String timestamp, final String content, final String userId,
            final String signature) throws Exception {
        final ChaincodeStub stub = ctx.getStub();

        final Post post = new Post(timestamp, content, userId, signature);
        final Key postKey = ChaincodeStubTools.generateKey(stub, post);

        ChaincodeStubTools.putStringState(stub, postKey, genson.serialize(post));

        return postKey.getBase64UrlKeyString();
    }

    public String publishNewPointTransaction(final Context ctx, final String timestamp, final String issuerUserId,
            final String payerEntriesString, final String signature, final String reference,
            final String payeeEntriesString) throws Exception {

        final ChaincodeStub stub = ctx.getStub();
        final var payerEntries = genson.deserialize(payerEntriesString, PointTransaction.Entry[].class);
        final var payeeEntries = genson.deserialize(payeeEntriesString, PointTransaction.Entry[].class);

        final PointTransaction.Tracking[] payersPointTransactionTracking = this
                .determinePointTransactionTrackingForPayerUserIds(ctx, Arrays.asList(payerEntries).stream()
                        .map(payerEntry -> payerEntry.getUserId()).toArray(String[]::new));

        final var pointTransaction = new PointTransaction(timestamp, issuerUserId, payerEntries, signature, reference,
                payeeEntries, this.determineRelativeOrderForPointTransaction(ctx), payersPointTransactionTracking);

        final Key pointTransactionKey = ChaincodeStubTools.generateKey(stub, pointTransaction);

        ChaincodeStubTools.putStringState(stub, pointTransactionKey, genson.serialize(pointTransaction));

        return pointTransactionKey.getBase64UrlKeyString();
    }

    public String publishNewLike(final Context ctx, final String timestamp, final String postKey,
            final String likeUserId, final String likePointAmount, final String likeSignature,
            final String likePointTransactionSignature) throws Exception {
        final ChaincodeStub stub = ctx.getStub();

        final var payerEntry = new PointTransaction.Entry(likeUserId, Double.parseDouble(likePointAmount));
        final Post post = this.getByKey(ctx, postKey, Post.class);
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

        final String pointTransactionKey = this.publishNewPointTransaction(ctx, timestamp, payerEntry.getUserId(),
                genson.serialize(new PointTransaction.Entry[] { payerEntry }), likePointTransactionSignature,
                likeKey.getBase64UrlKeyString(), genson.serialize(payeeEntries.toArray(PointTransaction.Entry[]::new)));
        like.setPointTransactionKey(pointTransactionKey);

        ChaincodeStubTools.putStringState(stub, likeKey, genson.serialize(like));

        return likeKey.getBase64UrlKeyString();
    }

    public String publishNewDislike(final Context ctx, final String timestamp, final String postKey,
            final String dislikeUserId, final String dislikePointAmount, final String dislikeSignature,
            final String dislikePointTransactionSignature) throws Exception {
        final ChaincodeStub stub = ctx.getStub();

        final Post post = this.getByKey(ctx, postKey, Post.class);
        final var dislikePayerEntry = new PointTransaction.Entry(dislikeUserId, Double.parseDouble(dislikePointAmount));
        final var authorPenaltyPayerEntry = new PointTransaction.Entry(post.getUserId(),
                dislikePayerEntry.getPointAmount());

        final KeyValue[] postDislikesKeyValue = this.getAllDislikesByPostKey(ctx, postKey);

        final DislikeRewarding rewarding = new DislikeRewarding(postDislikesKeyValue.length,
                dislikePayerEntry.getPointAmount(), authorPenaltyPayerEntry.getPointAmount());

        final List<PointTransaction.Entry> dislikePayeeEntries = new ArrayList<PointTransaction.Entry>();
        dislikePayeeEntries.add(new PointTransaction.Entry(authorPenaltyPayerEntry.getUserId(),
                authorPenaltyPayerEntry.getPointAmount() - rewarding.determineAuthorPenalty()));
        for (int idx = postDislikesKeyValue.length - 1; idx >= 0; idx--) {
            final long dislikerRank = postDislikesKeyValue.length - 1 - idx;
            if (!rewarding.isDislikerRewarded(dislikerRank)) {
                break;
            }
            final Dislike currentDisLike = genson.deserialize(postDislikesKeyValue[idx].getStringValue(),
                    Dislike.class);
            dislikePayeeEntries.add(new PointTransaction.Entry(currentDisLike.getUserId(),
                    rewarding.determineDislikerRewarding(dislikerRank)));
        }

        final var dislike = new Dislike(timestamp, postKey, dislikePayerEntry.getUserId(), dislikeSignature, null,
                this.determineRelativeOrderForDislike(ctx, postKey));
        final Key dislikeKey = ChaincodeStubTools.generateKey(stub, dislike);

        final String dislikePointTransactionKey = this.publishNewPointTransaction(ctx, timestamp,
                dislikePayerEntry.getUserId(),
                genson.serialize(new PointTransaction.Entry[] { dislikePayerEntry, authorPenaltyPayerEntry }),
                dislikePointTransactionSignature, dislikeKey.getBase64UrlKeyString(),
                genson.serialize(dislikePayeeEntries.toArray(PointTransaction.Entry[]::new)));

        dislike.setPointTransactionKey(dislikePointTransactionKey);

        ChaincodeStubTools.putStringState(stub, dislikeKey, genson.serialize(dislike));

        return dislikeKey.getBase64UrlKeyString();
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

    public String[] getAllLikeKeysByPostKey(final Context ctx, final String postKey) throws Exception {
        final List<String> likeKeys = Arrays.stream(this.getAllLikesByPostKey(ctx, postKey))
                .map(keyValue -> Key.createFromCCKeyString(keyValue.getKey()).getBase64UrlKeyString())
                .collect(Collectors.toList());
        return likeKeys.toArray(String[]::new);
    }

    public String[] getAllDislikeKeysByPostKey(final Context ctx, final String postKey) throws Exception {
        final List<String> dislikeKeys = Arrays.stream(this.getAllDislikesByPostKey(ctx, postKey))
                .map(keyValue -> Key.createFromCCKeyString(keyValue.getKey()).getBase64UrlKeyString())
                .collect(Collectors.toList());
        return dislikeKeys.toArray(String[]::new);
    }

    public String[] getAllPointTransactionKeys(final Context ctx) throws Exception {
        final List<String> pointTransactionKeys = Arrays.stream(this.getAllPointTransactions(ctx, null))
                .map(keyValue -> Key.createFromCCKeyString(keyValue.getKey()).getBase64UrlKeyString())
                .collect(Collectors.toList());
        return pointTransactionKeys.toArray(String[]::new);
    }

    public String[] getAllPointTransactionKeysByIssuerUserId(final Context ctx, final String issuerUserId)
            throws Exception {
        final List<String> pointTransactionKeys = Arrays.stream(this.getAllPointTransactions(ctx, issuerUserId))
                .map(keyValue -> Key.createFromCCKeyString(keyValue.getKey()).getBase64UrlKeyString())
                .collect(Collectors.toList());
        return pointTransactionKeys.toArray(String[]::new);
    }

    public String[] computeAllPointTransactionKeysByUserId(final Context ctx, final String userId) throws Exception {
        final Map<String, PointTransaction> relatedPointTransactions = new HashMap<String, PointTransaction>();

        PointTransaction.Tracking nextTracking = this.determinePointTransactionTrackingForPayerUserIds(ctx,
                new String[] { userId })[0];

        while (nextTracking != null) {
            final PointTransaction.Tracking tracking = nextTracking;
            nextTracking = null;

            for (final String earningKey : tracking.getRecentEarningPointTransactionKeys()) {
                final var earningPointTransaction = this.getByKey(ctx, earningKey, PointTransaction.class);
                if (Arrays.stream(earningPointTransaction.getPayeeEntries()).anyMatch(
                        earningPointTransactionPayee -> userId.equals(earningPointTransactionPayee.getUserId()))) {
                    relatedPointTransactions.put(earningKey, earningPointTransaction);
                }
            }
            final String spendingKey = tracking.getRecentSpendingPointTransactionKey();
            if (spendingKey != null) {
                final var spendingPointTransaction = this.getByKey(ctx, spendingKey, PointTransaction.class);
                final var spendingPointTransactionPayers = spendingPointTransaction.getPayerEntries();
                for (int payerIndex = 0; payerIndex < spendingPointTransactionPayers.length; payerIndex++) {
                    final var spendingPointTransactionPayer = spendingPointTransactionPayers[payerIndex];
                    if (userId.equals(spendingPointTransactionPayer.getUserId())) {
                        relatedPointTransactions.put(spendingKey, spendingPointTransaction);
                        nextTracking = spendingPointTransaction.getPayersPointTransactionTracking()[payerIndex];
                        break;
                    }
                }
            }
        }

        return relatedPointTransactions.entrySet().stream().sorted((leftEntry, rightEntry) -> {
            final var leftPointTransaction = leftEntry.getValue();
            final var rightPointTransaction = rightEntry.getValue();
            return leftPointTransaction.compareToByRelativeOrder(rightPointTransaction);
        }).map(entry -> entry.getKey()).toArray(String[]::new);
    }

    public double computePointBalanceByUserId(final Context ctx, final String userId) throws Exception {
        double totalEarningPointAmount = 0;
        double totalSpendingPointAmount = 0;

        final String[] pointTransactionKeys = this.computeAllPointTransactionKeysByUserId(ctx, userId);

        for (final String pointTransactionKey : pointTransactionKeys) {
            final PointTransaction pointTransaction = this.getByKey(ctx, pointTransactionKey, PointTransaction.class);

            for (final var payerEntry : pointTransaction.getPayerEntries()) {
                if (userId.equals(payerEntry.getUserId())) {
                    totalSpendingPointAmount += payerEntry.getPointAmount();
                }
            }
            for (final var payeeEntry : pointTransaction.getPayeeEntries()) {
                if (userId.equals(payeeEntry.getUserId())) {
                    totalEarningPointAmount += payeeEntry.getPointAmount();
                }
            }
        }

        return totalEarningPointAmount - totalSpendingPointAmount;
    }

    public <T extends KeyGeneration> T getByKey(final Context ctx, final String keyString, final Class<T> contentClass)
            throws IllegalArgumentException {
        final ChaincodeStub stub = ctx.getStub();
        final Key key = Key.createFromBase64UrlKeyString(keyString);

        final String contentString = ChaincodeStubTools.tryGetStringStateByKey(stub, key);
        final T content = genson.deserialize(contentString, contentClass);
        if (!content.isMatchingObjectType(key.getObjectTypeString())) {
            throw new ChaincodeException(String.format("getByKey<%s>(): but key is of type %s",
                    contentClass.getSimpleName(), key.getObjectTypeString()));
        }
        return content;
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
     * @param postKey
     * @return KeyValue[], key CCKeyString
     * @throws Exception
     */
    private KeyValue[] getAllDislikesByPostKey(final Context ctx, final String postKey) throws Exception {
        final ChaincodeStub stub = ctx.getStub();
        final var keyValueIterator = stub.getStateByPartialCompositeKey(Dislike.getObjectTypeName(), postKey);
        final List<KeyValue> dislikes = StreamSupport.stream(keyValueIterator.spliterator(), false)
                .sorted((leftKeyValue, rightKeyValue) -> {
                    final var leftDislike = genson.deserialize(leftKeyValue.getStringValue(), Dislike.class);
                    final var rightDislike = genson.deserialize(rightKeyValue.getStringValue(), Dislike.class);
                    return rightDislike.compareToByRelativeOrder(leftDislike);
                }).collect(Collectors.toList());
        keyValueIterator.close();
        return dislikes.toArray(KeyValue[]::new);
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
        final Like recentLike = this.getByKey(ctx, keys[0], Like.class);
        return Math.max(keys.length, recentLike.getRelativeOrder() + 1);
    }

    private long determineRelativeOrderForDislike(final Context ctx, final String postKey) throws Exception {
        final String[] keys = this.getAllDislikeKeysByPostKey(ctx, postKey);
        if (keys.length == 0) {
            return 0L;
        }
        final Dislike recentDislike = this.getByKey(ctx, keys[0], Dislike.class);
        return Math.max(keys.length, recentDislike.getRelativeOrder() + 1);
    }

    private long determineRelativeOrderForPointTransaction(final Context ctx) throws Exception {
        final String[] keys = this.getAllPointTransactionKeys(ctx);
        if (keys.length == 0) {
            return 0L;
        }
        final PointTransaction recentPointTransaction = this.getByKey(ctx, keys[0], PointTransaction.class);
        return Math.max(keys.length, recentPointTransaction.getRelativeOrder() + 1);
    }

    /**
     * Tracking.recentEarningPointTransactionKeys sorted by relativeOrder, most
     * recent first
     * 
     * @param ctx
     * @param userId
     * @return PointTransaction.Tracking[] same order as String[] payersUserId
     * @throws Exception
     */
    private PointTransaction.Tracking[] determinePointTransactionTrackingForPayerUserIds(final Context ctx,
            final String[] payersUserId) throws Exception {
        final KeyValue[] allPointTransactions = this.getAllPointTransactions(ctx, null);
        final List<PointTransaction.Tracking> payersTracking = new ArrayList<PointTransaction.Tracking>();

        for (final String payerUserId : payersUserId) {
            String recentSpendingPointTransactionKey = null;
            final List<String> recentEarningPointTransactionKeys = new ArrayList<String>();

            for (final KeyValue pointTransactionKV : allPointTransactions) {
                final var pointTransaction = genson.deserialize(pointTransactionKV.getStringValue(),
                        PointTransaction.class);
                final var pointTransactionKeyString = Key.createFromCCKeyString(pointTransactionKV.getKey())
                        .getBase64UrlKeyString();

                if (Arrays.stream(pointTransaction.getPayerEntries())
                        .anyMatch(payer -> payer.getUserId().equals(payerUserId))) {
                    recentSpendingPointTransactionKey = pointTransactionKeyString;
                    break;
                }
                if (Arrays.stream(pointTransaction.getPayeeEntries())
                        .anyMatch(payee -> payee.getUserId().equals(payerUserId))) {
                    recentEarningPointTransactionKeys.add(pointTransactionKeyString);
                }
            }

            payersTracking.add(new PointTransaction.Tracking(recentSpendingPointTransactionKey,
                    recentEarningPointTransactionKeys.toArray(String[]::new)));
        }

        return payersTracking.toArray(PointTransaction.Tracking[]::new);
    }

    public ForumRepositoryCC() {
    }
}

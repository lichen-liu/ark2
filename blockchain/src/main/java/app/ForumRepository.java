package app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.LongSupplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.annotation.Nullable;

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
import org.hyperledger.fabric.shim.ledger.KeyValue;

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
    public void initLedger(final Context ctx) throws Exception {
        // final ChaincodeStub stub = ctx.getStub();
        // final PointTransaction pointTransaction = new PointTransaction("now", new
        // PointTransactionElement("user0", 100),
        // "ref", "sig", new PointTransactionElement[] { new
        // PointTransactionElement("user1", 50),
        // new PointTransactionElement("user2", 50) });
        // stub.putStringState("point_transaction_id_0",
        // genson.serialize(pointTransaction));

        // real API
        this.publishNewPost(ctx, "future0", "I am smart", "user007", "signature(user007)");
        this.publishNewPost(ctx, "future1", "I am very smart", "user008", "signature(user008)");

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
     * @throws Exception
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
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

    /**
     * 
     * @param ctx
     * @param timestamp
     * 
     *                      <pre>
     *                      ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT); // "2015-04-14T11:07:36.639Z"
     *                      </pre>
     * 
     * @param payerElement
     * @param issuerUserId
     * @param reference
     * @param signature
     * @param payeeElements
     * @return
     * @throws Exception
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String publishNewPointTransaction(final Context ctx, final String timestamp,
            final PointTransactionElement payerElement, final String issuerUserId, final String reference,
            final String signature, final PointTransactionElement[] payeeElements) throws Exception {

        final ChaincodeStub stub = ctx.getStub();
        final String payerUserId = payerElement.getUserId();
        final String[] spendingKeys = this.getAllPointTransactionKeysByPayerUserId(ctx, payerUserId);
        final String recentSpendingPointTransactionKey = spendingKeys.length > 0 ? spendingKeys[0] : null;

        final List<String> recentEarningPointTransactionKeys = new ArrayList<String>();
        final KeyValue[] allPointTransactions = this.getAllPointTransactions(ctx, null);
        for (final KeyValue pointTransactionKV : allPointTransactions) {
            if (pointTransactionKV.getKey().equals(recentSpendingPointTransactionKey)) {
                break;
            }
            final var pointTransaction = genson.deserialize(pointTransactionKV.getStringValue(),
                    PointTransaction.class);
            if (Arrays.stream(pointTransaction.getPayeeElements())
                    .anyMatch(payee -> payee.getUserId().equals(payerUserId))) {
                recentEarningPointTransactionKeys.add(pointTransactionKV.getKey());
            }
        }

        final LongSupplier determineRelativeOrderForPointTransaction = () -> {
            if (allPointTransactions.length == 0) {
                return 0L;
            }
            final PointTransaction recentPointTransaction = genson.deserialize(allPointTransactions[0].getStringValue(),
                    PointTransaction.class);
            return Math.max(allPointTransactions.length, recentPointTransaction.getRelativeOrder() + 1);
        };

        final var pointTransaction = new PointTransaction(timestamp, payerElement, issuerUserId, reference, signature,
                payeeElements, determineRelativeOrderForPointTransaction.getAsLong(), recentSpendingPointTransactionKey,
                recentEarningPointTransactionKeys.toArray(String[]::new));

        final String pointTransactionKey = pointTransaction
                .generateKey(key -> ChaincodeStubTools.isKeyExisted(stub, key));

        stub.putStringState(pointTransactionKey, genson.serialize(pointTransaction));

        return pointTransactionKey;
    }

    /**
     * Sorted by relativeOrder, most recent first
     * 
     * @param ctx
     * @return postKeys
     * @throws Exception
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
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

    /**
     * Sorted by relativeOrder, most recent first
     * 
     * @param ctx
     * @param userId
     * @return
     * @throws Exception
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
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
     * Sorted by relativeOrder, most recent first
     * 
     * @param ctx
     * @param postKey
     * @return
     * @throws Exception
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
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

    /**
     * Sorted by relativeOrder, most recent first
     * 
     * @param ctx
     * @return
     * @throws Exception
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String[] getAllPointTransactionKeys(final Context ctx) throws Exception {
        final List<String> pointTransactionKeys = Arrays.stream(this.getAllPointTransactions(ctx, null))
                .map(keyValue -> keyValue.getKey()).collect(Collectors.toList());
        return pointTransactionKeys.toArray(String[]::new);
    }

    /**
     * Sorted by relativeOrder, most recent first
     * 
     * @param ctx
     * @param payerUserId
     * @return
     * @throws Exception
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String[] getAllPointTransactionKeysByPayerUserId(final Context ctx, final String payerUserId)
            throws Exception {
        final List<String> pointTransactionKeys = Arrays.stream(this.getAllPointTransactions(ctx, payerUserId))
                .map(keyValue -> keyValue.getKey()).collect(Collectors.toList());
        return pointTransactionKeys.toArray(String[]::new);
    }

    /**
     * Sorted by relativeOrder, most recent first
     * 
     * @param ctx
     * @param payerUserId
     * @return
     * @throws Exception
     */
    private KeyValue[] getAllPointTransactions(final Context ctx, @Nullable final String payerUserId) throws Exception {
        final ChaincodeStub stub = ctx.getStub();
        final CompositeKey partialCompositeKey = payerUserId == null
                ? new CompositeKey(PointTransaction.getObjectTypeName())
                : new CompositeKey(PointTransaction.getObjectTypeName(), payerUserId);
        final var keyValueIterator = stub.getStateByPartialCompositeKey(partialCompositeKey);
        final List<KeyValue> pointTransactionKeys = StreamSupport.stream(keyValueIterator.spliterator(), false)
                .sorted((keyValueLeft, keyValueRight) -> {
                    final var leftPointTransaction = genson.deserialize(keyValueLeft.getStringValue(),
                            PointTransaction.class);
                    final var rightPointTransaction = genson.deserialize(keyValueRight.getStringValue(),
                            PointTransaction.class);
                    return rightPointTransaction.compareToByRelativeOrder(leftPointTransaction);
                }).collect(Collectors.toList());
        keyValueIterator.close();
        return pointTransactionKeys.toArray(KeyValue[]::new);
    }

    /**
     * 
     * @param ctx
     * @param pointTransactionKey
     * @return
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public PointTransaction getPointTransactionByKey(final Context ctx, final String pointTransactionKey) {
        final ChaincodeStub stub = ctx.getStub();
        final String pointTransactionString = ChaincodeStubTools.tryGetStringStateByKey(stub, pointTransactionKey);
        return genson.deserialize(pointTransactionString, PointTransaction.class);
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public double getPointAmountByUserId(final Context ctx, final String userId) {
        final ChaincodeStub stub = ctx.getStub();
        final double totalPointAmount = 0;
        // TODO
        return totalPointAmount;
    }

    /**
     * 
     * @param ctx
     * @return
     * @throws Exception
     */
    private long determineRelativeOrderForPost(final Context ctx) throws Exception {
        final String[] keys = this.getAllPostKeys(ctx);
        if (keys.length == 0) {
            return 0L;
        }
        final Post recentPost = this.getPostByKey(ctx, keys[0]);
        return Math.max(keys.length, recentPost.getRelativeOrder() + 1);
    }

    /**
     * 
     * @param ctx
     * @param postKey
     * @return
     * @throws Exception
     */
    private long determineRelativeOrderForLike(final Context ctx, final String postKey) throws Exception {
        final String[] keys = this.getAllLikeKeysByPostKey(ctx, postKey);
        if (keys.length == 0) {
            return 0L;
        }
        final Like recentLike = this.getLikeByKey(ctx, keys[0]);
        return Math.max(keys.length, recentLike.getRelativeOrder() + 1);
    }
}

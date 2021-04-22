package app;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;

@Contract(name = "ForumAgreement", info = @Info(title = "ForumAgreement", description = "Forum chaincode", version = "0.1.0-SNAPSHOT"))

@Default
public final class ForumRepository implements ContractInterface {
    private final ForumRepositoryCC cc = new ForumRepositoryCC();

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void initLedger(final Context ctx) {
        System.out.println("initLedger: DONE");
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
     * @return postKey, call getPostByKey to verify whether the Post was
     *         successfully published
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String publishNewPost(final Context ctx, final String timestamp, final String content, final String userId,
            final String signature) {
        try {
            return this.cc.publishNewPost(ctx, timestamp, content, userId, signature);
        } catch (final Exception e) {
            e.printStackTrace();
            throw new ChaincodeException(e);
        }
    }

    /**
     * 
     * @param ctx
     * @param timestamp
     * 
     *                           <pre>
     *                           ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT); // "2015-04-14T11:07:36.639Z"
     *                           </pre>
     * 
     * @param payerEntryString   Json String in the format of:
     * 
     *                           <pre>
     *                           "{\"pointAmount\":150,\"userId\":\"ray\"}"
     *                           </pre>
     * 
     * @param issuerUserId
     * @param reference
     * @param signature
     * @param payeeEntriesString Json String in the format of:
     * 
     *                           <pre>
     *                           "[{\"pointAmount\":150,\"userId\":\"ray\"}]"
     *                           </pre>
     * 
     * @return pointTransactionKey, call getPointTransactionByKey to verify whether
     *         the PointTransaction was successfully published
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String publishNewPointTransaction(final Context ctx, final String timestamp, final String payerEntryString,
            final String issuerUserId, final String signature, final String reference,
            final String payeeEntriesString) {
        try {
            return this.cc.publishNewPointTransaction(ctx, timestamp, payerEntryString, issuerUserId, signature,
                    reference, payeeEntriesString);
        } catch (final Exception e) {
            e.printStackTrace();
            throw new ChaincodeException(e);
        }
    }

    /**
     * 
     * @param ctx
     * @param timestamp
     * 
     *                                      <pre>
     *                                      ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT); // "2015-04-14T11:07:36.639Z"
     *                                      </pre>
     * 
     * @param postKey
     * @param likerPayerEntryString
     * 
     *                                      <pre>
     *                                      "{\"pointAmount\":150,\"userId\":\"ray\"}"
     *                                      </pre>
     * 
     * @param likeSignature
     * @param likePointTransactionSignature
     * @return likeKey, call getLikeByKey to verify whether the Like was
     *         successfully published. Also call getPointTransactionByKey to check
     *         the associating PointTransaction
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String publishNewLike(final Context ctx, final String timestamp, final String postKey,
            final String likerPayerEntryString, final String likeSignature,
            final String likePointTransactionSignature) {

        try {
            return this.cc.publishNewLike(ctx, timestamp, postKey, likerPayerEntryString, likeSignature,
                    likePointTransactionSignature);
        } catch (final Exception e) {
            e.printStackTrace();
            throw new ChaincodeException(e);
        }
    }

    /**
     * Sorted by timestamp, most recent first
     * 
     * @param ctx
     * @return postKeys
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String[] getAllPostKeys(final Context ctx) {
        try {
            return this.cc.getAllPostKeys(ctx);
        } catch (final Exception e) {
            e.printStackTrace();
            throw new ChaincodeException(e);
        }
    }

    /**
     * Sorted by relativeOrder, most recent first
     * 
     * @param ctx
     * @param userId
     * @return
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String[] getAllPostKeysByUserId(final Context ctx, final String userId) {
        try {
            return this.cc.getAllPostKeysByUserId(ctx, userId);
        } catch (final Exception e) {
            e.printStackTrace();
            throw new ChaincodeException(e);
        }
    }

    /**
     * 
     * @param ctx
     * @param postKey
     * @return
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public Post getPostByKey(final Context ctx, final String postKey) {
        try {
            return this.cc.getByKey(ctx, postKey, Post.class);
        } catch (final IllegalArgumentException e) {
            e.printStackTrace();
            throw new ChaincodeException(e);
        }
    }

    /**
     * Sorted by relativeOrder, most recent first
     * 
     * @param ctx
     * @param postKey
     * @return
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String[] getAllLikeKeysByPostKey(final Context ctx, final String postKey) {
        try {
            return this.cc.getAllLikeKeysByPostKey(ctx, postKey);
        } catch (final Exception e) {
            e.printStackTrace();
            throw new ChaincodeException(e);
        }
    }

    /**
     * 
     * @param ctx
     * @param likeKey
     * @return
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public Like getLikeByKey(final Context ctx, final String likeKey) {
        try {
            return this.cc.getByKey(ctx, likeKey, Like.class);
        } catch (final IllegalArgumentException e) {
            e.printStackTrace();
            throw new ChaincodeException(e);
        }
    }

    /**
     * Sorted by relativeOrder, most recent first
     * 
     * @param ctx
     * @param postKey
     * @return
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String[] getAllDislikeKeysByPostKey(final Context ctx, final String postKey) {
        try {
            return this.cc.getAllDislikeKeysByPostKey(ctx, postKey);
        } catch (final Exception e) {
            e.printStackTrace();
            throw new ChaincodeException(e);
        }
    }

    /**
     * 
     * @param ctx
     * @param dislikeKey
     * @return
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public Dislike getDislikeByKey(final Context ctx, final String dislikeKey) {
        try {
            return this.cc.getByKey(ctx, dislikeKey, Dislike.class);
        } catch (final IllegalArgumentException e) {
            e.printStackTrace();
            throw new ChaincodeException(e);
        }
    }

    /**
     * Sorted by relativeOrder, most recent first
     * 
     * @param ctx
     * @return
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String[] getAllPointTransactionKeys(final Context ctx) {
        try {
            return this.cc.getAllPointTransactionKeys(ctx);
        } catch (final Exception e) {
            e.printStackTrace();
            throw new ChaincodeException(e);
        }
    }

    /**
     * Sorted by relativeOrder, most recent first
     * 
     * @param ctx
     * @param payerUserId
     * @return
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String[] getAllPointTransactionKeysByPayerUserId(final Context ctx, final String payerUserId) {
        try {
            return this.cc.getAllPointTransactionKeysByPayerUserId(ctx, payerUserId);
        } catch (final Exception e) {
            e.printStackTrace();
            throw new ChaincodeException(e);
        }
    }

    /**
     * Sorted by relativeOrder, oldest first
     * 
     * @param ctx
     * @param userId
     * @return
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String[] computeAllPointTransactionKeysByUserId(final Context ctx, final String userId) {
        try {
            return this.cc.computeAllPointTransactionKeysByUserId(ctx, userId);
        } catch (final Exception e) {
            e.printStackTrace();
            throw new ChaincodeException(e);
        }
    }

    /**
     * 
     * @param ctx
     * @param pointTransactionKey
     * @return
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public PointTransaction getPointTransactionByKey(final Context ctx, final String pointTransactionKey) {
        try {
            return this.cc.getByKey(ctx, pointTransactionKey, PointTransaction.class);
        } catch (final IllegalArgumentException e) {
            e.printStackTrace();
            throw new ChaincodeException(e);
        }
    }

    /**
     * 
     * @param ctx
     * @param userId
     * @return
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public double computePointBalanceByUserId(final Context ctx, final String userId) {
        try {
            return this.cc.computePointBalanceByUserId(ctx, userId);
        } catch (final Exception e) {
            e.printStackTrace();
            throw new ChaincodeException(e);
        }
    }

    public ForumRepository() {
    }
}

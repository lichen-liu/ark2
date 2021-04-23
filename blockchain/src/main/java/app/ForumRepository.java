package app;

import java.util.Arrays;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;

@Contract(name = "ForumAgreement", info = @Info(title = "ForumAgreement", description = "Forum chaincode", version = "1.0.0-Beta"))

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
            throw new ChaincodeException(toString(e));
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
     * @param issuerUserId
     * @param payerEntriesString Json String in the format of:
     * 
     *                           <pre>
     *                           "[{\"pointAmount\":150,\"userId\":\"ray\"}]"
     *                           </pre>
     * 
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
    public String publishNewPointTransaction(final Context ctx, final String timestamp, final String issuerUserId,
            final String payerEntriesString, final String signature, final String reference,
            final String payeeEntriesString) {
        try {
            return this.cc.publishNewPointTransaction(ctx, timestamp, issuerUserId, payerEntriesString, signature,
                    reference, payeeEntriesString);
        } catch (final Exception e) {
            throw new ChaincodeException(toString(e));
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
     * @param likeUserId
     * @param likePointAmount
     * @param likeSignature
     * @param likePointTransactionSignature
     * @return likeKey, call getLikeByKey to verify whether the Like was
     *         successfully published. Also call getPointTransactionByKey to check
     *         the associating PointTransaction
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String publishNewLike(final Context ctx, final String timestamp, final String postKey,
            final String likeUserId, final String likePointAmount, final String likeSignature,
            final String likePointTransactionSignature) {
        try {
            return this.cc.publishNewLike(ctx, timestamp, postKey, likeUserId, likePointAmount, likeSignature,
                    likePointTransactionSignature);
        } catch (final Exception e) {
            throw new ChaincodeException(toString(e));
        }
    }

    /**
     * 
     * @param ctx
     * @param timestamp
     * 
     *                                         <pre>
     *                                         ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT); // "2015-04-14T11:07:36.639Z"
     *                                         </pre>
     * 
     * @param postKey
     * @param dislikeUserId
     * @param dislikePointAmount
     * @param dislikeSignature
     * @param dislikePointTransactionSignature
     * @return dislikeKey, call getDislikeByKey to verify whether the Dislike was
     *         successfully published. Also call getPointTransactionByKey to check
     *         the associating PointTransaction
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String publishNewDislike(final Context ctx, final String timestamp, final String postKey,
            final String dislikeUserId, final String dislikePointAmount, final String dislikeSignature,
            final String dislikePointTransactionSignature) {
        try {
            return this.cc.publishNewDislike(ctx, timestamp, postKey, dislikeUserId, dislikePointAmount,
                    dislikeSignature, dislikePointTransactionSignature);
        } catch (final Exception e) {
            throw new ChaincodeException(toString(e));
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
            throw new ChaincodeException(toString(e));
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
            throw new ChaincodeException(toString(e));
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
            throw new ChaincodeException(toString(e));
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
            throw new ChaincodeException(toString(e));
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
            throw new ChaincodeException(toString(e));
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
            throw new ChaincodeException(toString(e));
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
            throw new ChaincodeException(toString(e));
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
            throw new ChaincodeException(toString(e));
        }
    }

    /**
     * Sorted by relativeOrder, most recent first
     * 
     * @param ctx
     * @param issuerUserId
     * @return
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String[] getAllPointTransactionKeysByIssuerUserId(final Context ctx, final String issuerUserId) {
        try {
            return this.cc.getAllPointTransactionKeysByIssuerUserId(ctx, issuerUserId);
        } catch (final Exception e) {
            throw new ChaincodeException(toString(e));
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
            throw new ChaincodeException(toString(e));
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
            throw new ChaincodeException(toString(e));
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
            throw new ChaincodeException(toString(e));
        }
    }

    private static String toString(final Exception e) {
        return e.toString() + "\n" + String.join("\n  > ",
                Arrays.stream(e.getStackTrace()).map(st -> st.toString()).toArray(String[]::new) + "\n");
    }

    public ForumRepository() {
    }
}

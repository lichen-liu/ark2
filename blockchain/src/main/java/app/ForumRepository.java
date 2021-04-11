package app;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.Transaction;

import app.datatype.Like;
import app.datatype.Post;

@Contract(name = "Agreements", info = @Info(title = "Agreements contract", description = "A java chaincode example", version = "0.0.1-SNAPSHOT"))

@Default
public final class ForumRepository implements ContractInterface {
    private final ForumRepositoryCC cc = new ForumRepositoryCC();

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
        cc.publishNewPost(ctx, "future0", "I am smart", "user007", "signature(user007)");
        cc.publishNewPost(ctx, "future1", "I am very smart", "user008", "signature(user008)");

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
        return cc.publishNewPost(ctx, timestamp, content, userId, signature);
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
        return cc.getAllPostKeys(ctx);
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
        return cc.getAllPostKeysByUserId(ctx, userId);
    }

    /**
     * 
     * @param ctx
     * @param postKey
     * @return
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public Post getPostByKey(final Context ctx, final String postKey) {
        return cc.getPostByKey(ctx, postKey);
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
        return cc.getAllLikeKeysByPostKey(ctx, postKey);
    }

    /**
     * 
     * @param ctx
     * @param likeKey
     * @return
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public Like getLikeByKey(final Context ctx, final String likeKey) {
        return cc.getLikeByKey(ctx, likeKey);
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String[] getAllPointTransactionKeys(final Context ctx) throws Exception {
        return cc.getAllPointTransactionKeys(ctx);
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public double getPointAmountByUserId(final Context ctx, final String userId) {
        return cc.getPointAmountByUserId(ctx, userId);
    }
}

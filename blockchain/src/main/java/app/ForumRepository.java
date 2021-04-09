package app;

import com.owlike.genson.Genson;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.*;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;

@Contract(name = "Agreements", info = @Info(title = "Agreements contract", description = "A java chaincode example", version = "0.0.1-SNAPSHOT"))

@Default
public final class ForumRepository implements ContractInterface {
    private final Genson genson = new Genson();

    @Transaction()
    public void initLedger(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();

        Post post = new Post("1", "timestamp", "content1", "signature1");
        PointTransaction pointTransaction = new PointTransaction("id", "now", new PointTransactionElement("user0", 100),
                "ref", "sig", "outgoing");

        String postState = genson.serialize(post);
        String pointTransactionState = genson.serialize(pointTransaction);

        stub.putStringState("POST1", postState);
        stub.putStringState("ptTransaction0", pointTransactionState);

        System.out.println("initLedger DONE!");
    }

    @Transaction()
    public Post getPost(final Context ctx, final String key) {
        ChaincodeStub stub = ctx.getStub();
        String postState = tryGetStateByKey(stub, key);

        Post post = genson.deserialize(postState, Post.class);

        return post;
    }

    @Transaction()
    public Post createPost(final Context ctx, final String key, final String id, final String timestamp,
            final String content, final String signature) {
        ChaincodeStub stub = ctx.getStub();
        String postState = tryGetStateByKey(stub, key);

        if (!postState.isEmpty()) {
            String errorMessage = String.format("Post %s already exists", id);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, "Post already exists");
        }

        Post post = new Post(id, timestamp, content, signature);
        postState = genson.serialize(post);
        stub.putStringState(key, postState);

        return post;
    }

    @Transaction()
    public Post changePostContent(final Context ctx, final String id, final String newContent) {
        ChaincodeStub stub = ctx.getStub();
        String postState = tryGetStateByKey(stub, id);

        Post post = genson.deserialize(postState, Post.class);
        Post newPost = new Post(post.getPostId(), post.getTimestamp(), newContent, post.getSignature());
        String newPostState = genson.serialize(newPost);

        stub.putStringState(id, newPostState);

        return newPost;
    }

    // endregion

    @Transaction()
    public PointTransaction getPointTransaction(final Context ctx, final String key) {
        ChaincodeStub stub = ctx.getStub();

        return genson.deserialize(tryGetStateByKey(stub, key), PointTransaction.class);
    }

    private String tryGetStateByKey(ChaincodeStub stub, final String key) {
        String state = stub.getStringState(key);

        if (state.isEmpty()) {
            String errorMessage = String.format("State %s does not exist", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, "State not found");
        }

        return state;
    }
}
package app;
import com.owlike.genson.Genson;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.*;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;


@Contract(
        name = "Agreements",
        info = @Info(
                title = "Agreements contract",
                description = "A java chaincode example",
                version = "0.0.1-SNAPSHOT"))

@Default
    public final class ForumRepository implements ContractInterface{
    private final Genson genson = new Genson();

    @Transaction()
    public void initLedger(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();

        Post post = new Post("1", "timestamp", "content1", "signature1");
        Reward reward = new Reward("2", "timestamp", "amount", "sender", "receiver", "signature");

        String postState = genson.serialize(post);
        String rewardState = genson.serialize(reward);

        stub.putStringState("POST1", postState);
        stub.putStringState("REWARD1", postState);
    }

//region Post

    @Transaction()
    public Post getPost(final Context ctx, final String key) {
        ChaincodeStub stub = ctx.getStub();
        String postState = tryGetStateByKey(stub, key);

        Post post = genson.deserialize(postState, Post.class);

        return post;
    }

    @Transaction()
    public Post createPost(final Context ctx, final String key ,final String id, final String timestamp, final String content, final String signature) {
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

//endregion

//region Reward

    @Transaction()
    public Reward getReward(final Context ctx, final String key) {
        ChaincodeStub stub = ctx.getStub();
        String rewardState = tryGetStateByKey(stub, key);

        Reward reward = genson.deserialize(rewardState, Reward.class);

        return reward;
    }

    @Transaction()
    public Reward createReward(final Context ctx, final String key, final String id, final String timestamp, final String amount, final String sender, final String receiver, final String signature) {
        ChaincodeStub stub = ctx.getStub();
        String rewardState = tryGetStateByKey(stub, key);

        if (!rewardState.isEmpty()) {
            String errorMessage = String.format("Post %s already exists", id);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, "Post already exists");
        }

        Reward reward = new Reward(id, timestamp, amount, sender, receiver, signature);
        rewardState = genson.serialize(reward);
        stub.putStringState(id, rewardState);

        return reward;
    }

//endregion

    private String tryGetStateByKey(ChaincodeStub stub, final String key){
        String state = stub.getStringState(key);

        if(state.isEmpty()) {
            String errorMessage = String.format("State %s does not exist", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, "State not found"); 
        }

        return state;
    }

}
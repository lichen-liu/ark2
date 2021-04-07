package app;
import com.owlike.genson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;


@DataType
public final class Reward {

    @Property()
    private final String id;

    @Property()
    private final String timestamp;

    @Property()
    private final String amount;

    @Property
    private final String sender;

    @Property
    private final String receiver;

    @Property
    private final String signature;

    public String getPostId() {
        return id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getAmount() {
        return amount;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return  receiver;
    }

    public String getSignature(){
        return signature;
    }

    public Reward(@JsonProperty("postId") final String id, @JsonProperty("timestamp") final String timestamp,
                     @JsonProperty("amount") final String amount, @JsonProperty("sender") final String sender,
                     @JsonProperty("receiver") final String receiver, @JsonProperty("signature") final String signature) {
        this.id = id;
        this.timestamp = timestamp;
        this.amount = amount;
        this.sender = sender;
        this.receiver = receiver;
        this.signature = signature;
    }
}
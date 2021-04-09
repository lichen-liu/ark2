package app;

import com.owlike.genson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

@DataType
public final class PointTransaction {
    @Property
    private final String pointTransactionId;

    @Property
    private final String timestamp;

    @Property
    private final PointTransactionElement incomingTransactionElement;

    @Property
    private final String reference;

    /**
     * sign(privateKey, hash(timestamp, incomingTransactionelement, reference))
     */
    @Property
    private final String signature;

    @Property
    private final String outgoingTransactionElements;

    public String getPointTransactionId() {
        return pointTransactionId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public PointTransactionElement getIncomingTransactionElement() {
        return incomingTransactionElement;
    }

    public String getReference() {
        return reference;
    }

    public String getSignature() {
        return signature;
    }

    public String getOutgoingTransactionElements() {
        return outgoingTransactionElements;
    }

    public PointTransaction(@JsonProperty("pointTransactionId") String pointTransactionId,
            @JsonProperty("timestamp") String timestamp,
            @JsonProperty("incomingTransactionElement") PointTransactionElement incomingTransactionElement,
            @JsonProperty("reference") String reference, @JsonProperty("signature") String signature,
            @JsonProperty("outgoingTransactionElements") String outgoingTransactionElements) {
        this.pointTransactionId = pointTransactionId;
        this.timestamp = timestamp;
        this.incomingTransactionElement = incomingTransactionElement;
        this.reference = reference;
        this.signature = signature;
        this.outgoingTransactionElements = outgoingTransactionElements;
    }
}
package app.datatype;

import com.owlike.genson.annotation.JsonProperty;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;
import org.hyperledger.fabric.shim.ledger.CompositeKey;

import app.util.KeyGeneration;

@DataType
public final class PointTransaction implements KeyGeneration {
    @Property
    private final String timestamp;

    @Property
    private final PointTransactionElement incomingTransactionElement;

    /**
     * A user specified reference number, can be arbitrary
     */
    @Property
    private final String reference;

    /**
     * sign(privateKey, hash(timestamp, incomingTransactionelement, reference))
     */
    @Property
    private final String signature;

    @Property
    private final PointTransactionElement[] outgoingTransactionElements;

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

    public PointTransactionElement[] getOutgoingTransactionElements() {
        return outgoingTransactionElements;
    }

    public PointTransaction(@JsonProperty("timestamp") final String timestamp,
            @JsonProperty("incomingTransactionElement") final PointTransactionElement incomingTransactionElement,
            @JsonProperty("reference") final String reference, @JsonProperty("signature") final String signature,
            @JsonProperty("outgoingTransactionElements") final PointTransactionElement[] outgoingTransactionElements) {
        this.timestamp = timestamp;
        this.incomingTransactionElement = incomingTransactionElement;
        this.reference = reference;
        this.signature = signature;
        this.outgoingTransactionElements = outgoingTransactionElements;
    }

    @Override
    public String generateKey(final String salt) {
        return new CompositeKey(getObjectTypeName(), this.getIncomingTransactionElement().getUserId(), signature, salt)
                .toString();
    }

    public static String getObjectTypeName() {
        return "POINT_TRANSACTION";
    }
}
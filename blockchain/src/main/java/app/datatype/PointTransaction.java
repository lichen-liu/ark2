package app.datatype;

import com.owlike.genson.annotation.JsonProperty;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;
import org.hyperledger.fabric.shim.ledger.CompositeKey;

import app.util.ComparableByRelativeOrder;
import app.util.ComparableByTimestamp;
import app.util.KeyGeneration;

@DataType
public final class PointTransaction implements KeyGeneration, ComparableByTimestamp, ComparableByRelativeOrder {
    @Property
    private final String timestamp;

    @Property
    private final PointTransactionElement incomingTransactionElement;

    /**
     * The issuerUserId for this PointTransaction. Also the verficationKey of the
     * signature.
     */
    private final String issuerUserId;

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

    /**
     * Sequenced
     */
    @Property
    private final PointTransactionElement[] outgoingTransactionElements;

    @Property
    private final long relativeOrder;

    /**
     * With respect to the incomingTransactionElement.getUserId(), the key to the
     * most recent spending PointTransaction (appears in
     * incomingTransactionElement).
     */
    @Property
    private final String recentSpendingPointTransactionKey;

    /**
     * With respect to the incomingTransactionElement.getUserId(), the keys to the
     * recent earning PointTransactions (appears in outgoingTransactionElements)
     * after recentSpendingPointTransactionKey.
     */
    @Property
    private final String[] recentEarningPointTransactionKeys;

    @Override
    public String getTimestamp() {
        return timestamp;
    }

    public PointTransactionElement getIncomingTransactionElement() {
        return incomingTransactionElement;
    }

    public String getIssuerUserId() {
        return issuerUserId;
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

    @Override
    public long getRelativeOrder() {
        return relativeOrder;
    }

    public String getRecentSpendingPointTransactionKey() {
        return recentSpendingPointTransactionKey;
    }

    public String[] getRecentEarningPointTransactionKeys() {
        return recentEarningPointTransactionKeys;
    }

    public PointTransaction(@JsonProperty("timestamp") final String timestamp,
            @JsonProperty("incomingTransactionElement") final PointTransactionElement incomingTransactionElement,
            @JsonProperty("issuerUserId") final String issuerUserId, @JsonProperty("reference") final String reference,
            @JsonProperty("signature") final String signature,
            @JsonProperty("outgoingTransactionElements") final PointTransactionElement[] outgoingTransactionElements,
            @JsonProperty("relativeOrder") final long relativeOrder,
            @JsonProperty("recentSpendingPointTransactionKey") final String recentSpendingPointTransactionKey,
            @JsonProperty("recentEarningPointTransactionKeys") final String[] recentEarningPointTransactionKeys) {
        this.timestamp = timestamp;
        this.incomingTransactionElement = incomingTransactionElement;
        this.issuerUserId = issuerUserId;
        this.reference = reference;
        this.signature = signature;
        this.outgoingTransactionElements = outgoingTransactionElements;
        this.relativeOrder = relativeOrder;
        this.recentSpendingPointTransactionKey = recentSpendingPointTransactionKey;
        this.recentEarningPointTransactionKeys = recentEarningPointTransactionKeys;
    }

    @Override
    public String generateKey(final String salt) {
        return new CompositeKey(getObjectTypeName(), this.getIncomingTransactionElement().getUserId(),
                String.valueOf(relativeOrder), salt).toString();
    }

    public static String getObjectTypeName() {
        return "POINT_TRANSACTION";
    }
}
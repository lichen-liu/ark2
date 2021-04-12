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
    private final PointTransactionElement payerElement;

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
     * sign(privateKey, hash(timestamp, payerElement, reference))
     */
    @Property
    private final String signature;

    /**
     * Sequenced
     */
    @Property
    private final PointTransactionElement[] payeeElements;

    @Property
    private final long relativeOrder;

    /**
     * With respect to the payerElement.getUserId(), the key to the most recent
     * spending PointTransaction (appears in payerElement).
     */
    @Property
    private final String recentSpendingPointTransactionKey;

    /**
     * With respect to the payerElement.getUserId(), the keys to the recent earning
     * PointTransactions (appears in payeeElements) after
     * recentSpendingPointTransactionKey.
     */
    @Property
    private final String[] recentEarningPointTransactionKeys;

    @Override
    public String getTimestamp() {
        return timestamp;
    }

    public PointTransactionElement getPayerElement() {
        return payerElement;
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

    public PointTransactionElement[] getPayeeElements() {
        return payeeElements;
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
            @JsonProperty("payerElement") final PointTransactionElement payerElement,
            @JsonProperty("issuerUserId") final String issuerUserId, @JsonProperty("reference") final String reference,
            @JsonProperty("signature") final String signature,
            @JsonProperty("payeeElements") final PointTransactionElement[] payeeElements,
            @JsonProperty("relativeOrder") final long relativeOrder,
            @JsonProperty("recentSpendingPointTransactionKey") final String recentSpendingPointTransactionKey,
            @JsonProperty("recentEarningPointTransactionKeys") final String[] recentEarningPointTransactionKeys) {
        this.timestamp = timestamp;
        this.payerElement = payerElement;
        this.issuerUserId = issuerUserId;
        this.reference = reference;
        this.signature = signature;
        this.payeeElements = payeeElements;
        this.relativeOrder = relativeOrder;
        this.recentSpendingPointTransactionKey = recentSpendingPointTransactionKey;
        this.recentEarningPointTransactionKeys = recentEarningPointTransactionKeys;
    }

    @Override
    public String generateKey(final String salt) {
        return new CompositeKey(getObjectTypeName(), this.getPayerElement().getUserId(), String.valueOf(relativeOrder),
                salt).toString();
    }

    public static String getObjectTypeName() {
        return PointTransaction.class.getSimpleName();
    }
}
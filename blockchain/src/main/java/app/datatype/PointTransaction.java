package app.datatype;

import javax.annotation.Nullable;

import com.owlike.genson.annotation.JsonProperty;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;
import org.hyperledger.fabric.shim.ledger.CompositeKey;

import app.policy.ComparableByRelativeOrder;
import app.policy.ComparableByTimestamp;
import app.policy.KeyGeneration;

@DataType
public final class PointTransaction implements KeyGeneration, ComparableByTimestamp, ComparableByRelativeOrder {
    @Property
    private final String timestamp;

    @Property
    private final Entry payerEntry;

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
     * sign(privateKey, hash(timestamp, payerEntry, issuerUserId, reference))
     */
    @Property
    private final String signature;

    /**
     * Sequenced
     */
    @Property
    private final Entry[] payeeEntries;

    @Property
    private final long relativeOrder;

    @Property
    private final Tracking payerPointTransactionTracking;

    @DataType
    public static class Entry {
        @Property
        private final String userId;

        @Property
        private final double pointAmount;

        public String getUserId() {
            return userId;
        }

        public double getPointAmount() {
            return pointAmount;
        }

        public Entry(@JsonProperty("userId") final String userId,
                @JsonProperty("pointAmount") final double pointAmount) {
            this.userId = userId;
            this.pointAmount = pointAmount;
        }
    }

    @DataType
    public static class Tracking {
        /**
         * With respect to the payerEntry.getUserId(), the key to the most recent
         * spending PointTransaction (appears in payerEntry).
         */
        @Property
        @Nullable
        private final String recentSpendingPointTransactionKey;

        /**
         * With respect to the payerEntry.getUserId(), the keys to the recent earning
         * PointTransactions (appears in payeeEntries) after
         * recentSpendingPointTransactionKey.
         */
        @Property
        private final String[] recentEarningPointTransactionKeys;

        public String getRecentSpendingPointTransactionKey() {
            return recentSpendingPointTransactionKey;
        }

        public String[] getRecentEarningPointTransactionKeys() {
            return recentEarningPointTransactionKeys;
        }

        public Tracking(
                @JsonProperty("recentSpendingPointTransactionKey") final String recentSpendingPointTransactionKey,
                @JsonProperty("recentEarningPointTransactionKeys") final String[] recentEarningPointTransactionKeys) {
            this.recentSpendingPointTransactionKey = recentSpendingPointTransactionKey;
            this.recentEarningPointTransactionKeys = recentEarningPointTransactionKeys;
        }
    }

    @Override
    public String getTimestamp() {
        return timestamp;
    }

    public Entry getPayerEntry() {
        return payerEntry;
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

    public Entry[] getPayeeEntries() {
        return payeeEntries;
    }

    @Override
    public long getRelativeOrder() {
        return relativeOrder;
    }

    public Tracking getPayerPointTransactionTracking() {
        return payerPointTransactionTracking;
    }

    public PointTransaction(@JsonProperty("timestamp") final String timestamp,
            @JsonProperty("payerEntry") final Entry payerEntry, @JsonProperty("issuerUserId") final String issuerUserId,
            @JsonProperty("reference") final String reference, @JsonProperty("signature") final String signature,
            @JsonProperty("payeeEntries") final Entry[] payeeEntries,
            @JsonProperty("relativeOrder") final long relativeOrder,
            @JsonProperty("payerPointTransactionTracking") final Tracking payerPointTransactionTracking) {
        this.timestamp = timestamp;
        this.payerEntry = payerEntry;
        this.issuerUserId = issuerUserId;
        this.reference = reference;
        this.signature = signature;
        this.payeeEntries = payeeEntries;
        this.relativeOrder = relativeOrder;
        this.payerPointTransactionTracking = payerPointTransactionTracking;
    }

    @Override
    public String generateKey(final String salt) {
        return new CompositeKey(getObjectTypeName(), this.getPayerEntry().getUserId(), String.valueOf(relativeOrder),
                salt).toString();
    }

    public static String getObjectTypeName() {
        return PointTransaction.class.getSimpleName();
    }
}
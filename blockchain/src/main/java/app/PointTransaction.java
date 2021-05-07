package app;

import java.io.Serializable;

import javax.annotation.Nullable;

import com.owlike.genson.annotation.JsonProperty;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;
import org.hyperledger.fabric.shim.ledger.CompositeKey;

import app.policy.BytesState;
import app.policy.ComparableByRelativeOrder;
import app.policy.ComparableByTimestamp;
import app.policy.KeyGeneration;

@DataType
public final class PointTransaction
        implements KeyGeneration, ComparableByTimestamp, ComparableByRelativeOrder, BytesState {
    @Property
    private final String timestamp;

    /**
     * The issuerUserId for this PointTransaction. Also the verficationKey of the
     * signature.
     */
    @Property
    private final String issuerUserId;

    @Property
    private final Entry[] payerEntries;

    /**
     * sign(privateKey, hash(timestamp, issuerUserId, *payerEntries))
     */
    @Property
    private final String signature;

    /**
     * A user specified reference number, can be arbitrary
     */
    @Property
    private final String reference;

    /**
     * Sequenced
     */
    @Property
    private final Entry[] payeeEntries;

    @Property
    private final long relativeOrder;

    @Property
    private final Tracking[] payersPointTransactionTracking;

    @DataType
    public static class Entry implements Serializable {
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
    public static class Tracking implements Serializable {
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

    public String getIssuerUserId() {
        return issuerUserId;
    }

    public Entry[] getPayerEntries() {
        return payerEntries;
    }

    public String getSignature() {
        return signature;
    }

    public String getReference() {
        return reference;
    }

    public Entry[] getPayeeEntries() {
        return payeeEntries;
    }

    @Override
    public long getRelativeOrder() {
        return relativeOrder;
    }

    public Tracking[] getPayersPointTransactionTracking() {
        return payersPointTransactionTracking;
    }

    public PointTransaction(@JsonProperty("timestamp") final String timestamp,
            @JsonProperty("issuerUserId") final String issuerUserId,
            @JsonProperty("payerEntries") final Entry[] payerEntries, @JsonProperty("signature") final String signature,
            @JsonProperty("reference") final String reference, @JsonProperty("payeeEntries") final Entry[] payeeEntries,
            @JsonProperty("relativeOrder") final long relativeOrder,
            @JsonProperty("payersPointTransactionTracking") final Tracking[] payersPointTransactionTracking) {
        this.timestamp = timestamp;
        this.issuerUserId = issuerUserId;
        this.payerEntries = payerEntries;
        this.signature = signature;
        this.reference = reference;
        this.payeeEntries = payeeEntries;
        this.relativeOrder = relativeOrder;
        this.payersPointTransactionTracking = payersPointTransactionTracking;
    }

    @Override
    public CompositeKey generateKey(final String salt) {
        return new CompositeKey(getObjectTypeName(), this.issuerUserId, String.valueOf(relativeOrder), salt);
    }

    @Override
    public boolean isMatchingObjectType(final String objectType) {
        return getObjectTypeName().equals(objectType);
    }

    public static String getObjectTypeName() {
        return PointTransaction.class.getSimpleName();
    }
}
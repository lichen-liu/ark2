package app.repository;

import lombok.ToString;

@ToString(includeFieldNames = true)
public class PointTransaction {
    public String timestamp;

    /**
     * The issuerUserId for this PointTransaction. Also the verficationKey of the
     * signature.
     */
    public String issuerUserId;

    public PointTransaction.Entry[] payerEntries;

    /**
     * sign(privateKey, hash(timestamp, issuerUserId, payerEntries))
     */
    public String signature;

    /**
     * A user specified reference number, can be arbitrary
     */
    public String reference;

    /**
     * Sequenced
     */
    public PointTransaction.Entry[] payeeEntries;

    public long relativeOrder;

    public Tracking[] payersPointTransactionTracking;

    @ToString(includeFieldNames = true)
    public static class Tracking {
        /**
         * With respect to the payerEntry.getUserId(), the key to the most recent
         * spending PointTransaction (appears in payerEntry).
         */

        public String recentSpendingPointTransactionKey;

        /**
         * With respect to the payerEntry.getUserId(), the keys to the recent earning
         * PointTransactions (appears in payeeEntries) after
         * recentSpendingPointTransactionKey.
         */
        public String[] recentEarningPointTransactionKeys;
    }

    @ToString(includeFieldNames = true)
    public static class Entry {
        public Entry() {
        }

        public Entry(final String userId, final Double pointAmount) {
            this.userId = userId;
            this.pointAmount = pointAmount;
        }

        public String userId;
        public Double pointAmount;
    }

}

package app.repository.data;

import app.repository.contracts.Transaction.Entry;
import lombok.ToString;

@ToString(callSuper = true, includeFieldNames = true)
public class PointTransaction {
    public String timestamp;

    public Entry payerEntry;

    /**
     * The issuerUserId for this PointTransaction. Also the verficationKey of the
     * signature.
     */
    public String issuerUserId;

    /**
     * sign(privateKey, hash(timestamp, payerEntry, issuerUserId))
     */
    public String signature;

    /**
     * A user specified reference number, can be arbitrary
     */
    public String reference;

    /**
     * Sequenced
     */
    public Entry[] payeeEntries;

    public long relativeOrder;

    public Tracking payerPointTransactionTracking;

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

}

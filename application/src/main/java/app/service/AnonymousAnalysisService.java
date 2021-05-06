package app.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import app.repository.PointTransaction;

public interface AnonymousAnalysisService extends AnonymousService {
    public interface PointBalanceSnapshot {
        public abstract double getBalance();

        public abstract double getBalanceChange();

        public abstract long getRelativeOrder();

        public abstract String getTimeStamp();

        public abstract String getPointTransactionKey();

        public default String toCsvRow() {
            return getRelativeOrder() + "," + getPointTransactionKey() + "," + getTimeStamp() + "," + getBalance() + ","
                    + getBalanceChange();
        }
    }

    public default List<PointBalanceSnapshot> analyzePointBalanceHistoryByUserId(@Nullable final String userId) {
        String[] pointTransactionKeys = null;
        if (userId == null) {
            pointTransactionKeys = fetchPointTransactionKeys();
        } else {
            pointTransactionKeys = computePointTransactionKeysByUserId(userId);
        }

        final var tracking = new ArrayList<PointBalanceSnapshot>();
        double pointBalance = 0.0;
        for (final String pointTransactionKey : pointTransactionKeys) {
            final PointTransaction pointTransaction = fetchPointTransactionByPointTransactionKey(pointTransactionKey);
            double pointBalanceChange = 0.0;

            for (final var payerEntry : pointTransaction.payerEntries) {
                if (userId.equals(payerEntry.userId)) {
                    pointBalanceChange -= payerEntry.pointAmount;
                }
            }
            for (final var payeeEntry : pointTransaction.payeeEntries) {
                if (userId.equals(payeeEntry.userId)) {
                    pointBalanceChange += payeeEntry.pointAmount;
                }
            }

            pointBalance += pointBalanceChange;

            final double currentPointBalance = pointBalance;
            final double currentPointBalanceChange = pointBalanceChange;

            tracking.add(new PointBalanceSnapshot() {
                @Override
                public double getBalance() {
                    return currentPointBalance;
                }

                @Override
                public double getBalanceChange() {
                    return currentPointBalanceChange;
                }

                @Override
                public long getRelativeOrder() {
                    return pointTransaction.relativeOrder;
                }

                @Override
                public String getTimeStamp() {
                    return pointTransaction.timestamp;
                }

                @Override
                public String getPointTransactionKey() {
                    return pointTransactionKey;
                }
            });

        }
        return tracking;
    }
}

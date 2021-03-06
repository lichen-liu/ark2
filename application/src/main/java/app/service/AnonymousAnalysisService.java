package app.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.DoubleConsumer;

import javax.annotation.Nullable;

import app.repository.PointTransaction;

public interface AnonymousAnalysisService extends AnonymousService {
    public interface PointBalanceSnapshot {
        public abstract double getBalance();

        public abstract double getBalanceChange();

        public abstract long getRelativeOrder();

        public abstract String getTimestamp();

        public abstract String getPointTransactionKey();

        public default String toCsvRow() {
            return getRelativeOrder() + "," + getPointTransactionKey() + "," + getTimestamp() + "," + getBalance() + ","
                    + getBalanceChange();
        }

        public static String CsvRowTitle() {
            return "relative_order,point_transaction_key,timestamp,point_balance,balance_change";
        }
    }

    public default List<PointBalanceSnapshot> analyzePointBalanceHistoryByUserId(@Nullable final String userId,
            @Nullable final DoubleConsumer progressCallback) {
        String[] pointTransactionKeys = null;
        if (userId == null) {
            pointTransactionKeys = fetchPointTransactionKeys();
            Collections.reverse(Arrays.asList(pointTransactionKeys));
        } else {
            pointTransactionKeys = computePointTransactionKeysByUserId(userId);
        }

        final var tracking = new ArrayList<PointBalanceSnapshot>();

        double pointBalance = 0.0;
        for (final String pointTransactionKey : pointTransactionKeys) {
            final PointTransaction pointTransaction = fetchPointTransactionByPointTransactionKey(pointTransactionKey);
            double pointBalanceChange = 0.0;

            for (final var payerEntry : pointTransaction.payerEntries) {
                if (userId == null || userId.equals(payerEntry.userId)) {
                    pointBalanceChange -= payerEntry.pointAmount;
                }
            }
            for (final var payeeEntry : pointTransaction.payeeEntries) {
                if (userId == null || userId.equals(payeeEntry.userId)) {
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
                public String getTimestamp() {
                    return pointTransaction.timestamp;
                }

                @Override
                public String getPointTransactionKey() {
                    return pointTransactionKey;
                }
            });

            if (progressCallback != null) {
                progressCallback.accept((tracking.size() - 1) / (double) pointTransactionKeys.length);
            }
        }
        return tracking;
    }

    public default List<PointBalanceSnapshot> analyzePointBalanceHistoryByUserId(@Nullable final String userId) {
        return analyzePointBalanceHistoryByUserId(userId, null);
    }
}

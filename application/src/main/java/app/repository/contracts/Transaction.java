package app.repository.contracts;

import java.util.List;

import lombok.ToString;

public class Transaction {
    public Entry payer;
    public List<Entry> payees;
    public String reference;

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

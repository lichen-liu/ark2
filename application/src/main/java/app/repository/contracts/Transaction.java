package app.repository.contracts;

import java.util.List;

import lombok.ToString;

public class Transaction {
    public Entry payer;
    public List<Entry> payees;
    public String reference;

    @ToString(callSuper = true, includeFieldNames = true)
    public static class Entry {
        public Entry(final String userId, final Double amount) {
            this.userId = userId;
            this.amount = amount;
        }

        public String userId;
        public Double amount;
    }
}

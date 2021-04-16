package app.repository.contracts;

import java.util.List;

public class Transaction {
    public Entry payer;
    public List<Entry> payees;
    public String reference;

    public static class Entry {
        public Entry(String userId, Double amount) {
            this.userId = userId;
            this.amount = amount;
        }

        public String userId;
        public Double amount;
    }
}

package app.repository.contracts;

import java.util.List;

public class Transaction {
    public Participant payer;
    public List<Participant> payees;
    public String reference;

    public static class Participant {
        public Participant(String userId, Double amount) {
            this.userId = userId;
            this.amount = amount;
        }
        public String userId;
        public Double amount;
    }
}

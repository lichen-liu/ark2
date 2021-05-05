package app.repository;

import java.util.List;

public class Payment {
    public PointTransaction.Entry payer;
    public List<PointTransaction.Entry> payees;
    public String reference;
}

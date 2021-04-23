package app.repository.data;

import java.util.List;

public class Payment {
    public PointTransaction.Entry payer;
    public List<PointTransaction.Entry> payees;
    public String reference;
}

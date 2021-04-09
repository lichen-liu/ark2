package app;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

@DataType
public class PointTransactionElement {
    @Property
    private final String id;

    @Property
    private final double pointAmount;

    public String getId() {
        return id;
    }

    public double getPointAmount() {
        return pointAmount;
    }

    public PointTransactionElement(String id, double pointAmount) {
        this.id = id;
        this.pointAmount = pointAmount;
    }
}
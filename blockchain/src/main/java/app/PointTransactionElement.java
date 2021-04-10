package app;

import com.owlike.genson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

@DataType
public class PointTransactionElement {
    @Property
    private final String userId;

    @Property
    private final double pointAmount;

    public String getUserId() {
        return userId;
    }

    public double getPointAmount() {
        return pointAmount;
    }

    public PointTransactionElement(@JsonProperty("userId") String userId, @JsonProperty("pointAmount") double pointAmount) {
        this.userId = userId;
        this.pointAmount = pointAmount;
    }
}
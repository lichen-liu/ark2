package app.util;

import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;

public class ChaincodeStubTools {
    public static boolean isKeyExisted(final ChaincodeStub stub, final String key) {
        return !stub.getStringState(key).isEmpty();
    }

    public static String tryGetStringStateByKey(final ChaincodeStub stub, final String key) {
        final String state = stub.getStringState(key);
        if (state.isEmpty()) {
            final String errorMessage = String.format("State %s does not exist", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, errorMessage);
        }
        return state;
    }
}

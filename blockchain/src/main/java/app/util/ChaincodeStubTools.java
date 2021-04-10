package app.util;

import org.hyperledger.fabric.shim.ChaincodeStub;

public class ChaincodeStubTools {
    public static boolean isKeyExisted(final ChaincodeStub stub, final String key) {
        return !stub.getStringState(key).isEmpty();
    }
}

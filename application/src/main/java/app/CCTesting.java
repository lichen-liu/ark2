package app;

import java.util.concurrent.TimeoutException;

import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.ContractException;

public class CCTesting {
    private int testId = 0;

    public CCTesting() {
    }

    public void test(final Contract contract) {
        try {
            print(contract.evaluateTransaction("getPostByKey", "post_id_0"));
            print(contract.submitTransaction("publishNewPost", "future", "I am smart", "user007",
                    "signature(user007)"));
            print(contract.evaluateTransaction("getAllPostKeys"));
        } catch (final ContractException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void print(final byte[] result) {
        final String stringResult = new String(result);
        System.out.println("\n[" + this.testId + "] result: " + stringResult);
        this.testId++;
    }
}

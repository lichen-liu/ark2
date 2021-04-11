package app;

import org.hyperledger.fabric.gateway.Contract;

public class CCTesting {
    private int testId = 0;

    public CCTesting() {
    }

    public void test(final AppClient appPeer) {
        try {
            final Contract contract = appPeer.getContract();
            final String r0 = appPeer.publishNewPost("hahaha");
            print(r0);
            print(appPeer.fetchAllPosts());
            print(contract.evaluateTransaction("getPostByKey", r0));
        } catch (final Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void print(final byte[] result) {
        this.print(new String(result));
    }

    private void print(final String result) {
        System.out.println("\n[" + this.testId + "] result: " + result);
        this.testId++;
    }
}

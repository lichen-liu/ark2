package app;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hyperledger.fabric.gateway.Contract;

public class CCTesting {
    private int testId = 0;
    private final ObjectMapper objectMapper;

    public CCTesting() {
        objectMapper = new ObjectMapper();
    }

    public void test(final AppClient appPeer) {
        try {
            final Contract contract = appPeer.getContract();

            print(appPeer.getContract().submitTransaction("publishNewPost", "a", "a", "a", "a"));

            {
                final String p0 = appPeer.publishNewPost("hahaha");
                print(p0);
                print(contract.evaluateTransaction("getPostByKey", p0));
            }

            print(appPeer.fetchAllPosts());

            print(appPeer.fetchAllPostKeys());

            {
                final Map<String, String> payer = Map.of("userId", "bank", "pointAmount", "100");
                final List<Map<String, String>> payees = List.of(Map.of("userId", "ray", "pointAmount", "100"));
                final var t0 = toString(contract.submitTransaction("publishNewPointTransaction", "20210412_155300",
                        objectMapper.writeValueAsString(payer), "bank", "signature(bank)",
                        objectMapper.writeValueAsString(payees)));
                print(t0);
                print(contract.evaluateTransaction("getPointTransactionByKey", t0));
            }

            {
                final Map<String, String> payer = Map.of("userId", "ray", "pointAmount", "100");
                final List<Map<String, String>> payees = List.of(Map.of("userId", "charles", "pointAmount", "50"),
                        Map.of("userId", "zac", "pointAmount", "50"));
                final var t1 = toString(contract.submitTransaction("publishNewPointTransaction", "20210412_155400",
                        objectMapper.writeValueAsString(payer), "ray", "signature(ray)",
                        objectMapper.writeValueAsString(payees)));
                print(t1);
                print(contract.evaluateTransaction("getPointTransactionByKey", t1));
            }

            {
                final Map<String, String> payer = Map.of("userId", "bank", "pointAmount", "100");
                final List<Map<String, String>> payees = List.of(Map.of("userId", "ray", "pointAmount", "100"));
                final var t2 = toString(contract.submitTransaction("publishNewPointTransaction", "20210412_155500",
                        objectMapper.writeValueAsString(payer), "bank", "signature(bank)",
                        objectMapper.writeValueAsString(payees)));
                print(t2);
                print(contract.evaluateTransaction("getPointTransactionByKey", t2));
            }

            {
                final Map<String, String> payer = Map.of("userId", "bank", "pointAmount", "100");
                final List<Map<String, String>> payees = List.of(Map.of("userId", "ray", "pointAmount", "100"));
                final var t3 = toString(contract.submitTransaction("publishNewPointTransaction", "20210412_155600",
                        objectMapper.writeValueAsString(payer), "bank", "signature(bank)",
                        objectMapper.writeValueAsString(payees)));
                print(t3);
                print(contract.evaluateTransaction("getPointTransactionByKey", t3));
            }

            {
                final Map<String, String> payer = Map.of("userId", "ray", "pointAmount", "150");
                final List<Map<String, String>> payees = List.of(Map.of("userId", "charles", "pointAmount", "50"),
                        Map.of("userId", "zac", "pointAmount", "100"));
                final var t4 = toString(contract.submitTransaction("publishNewPointTransaction", "20210412_155700",
                        objectMapper.writeValueAsString(payer), "ray", "signature(ray)",
                        objectMapper.writeValueAsString(payees)));
                print(t4);
                print(contract.evaluateTransaction("getPointTransactionByKey", t4));
            }

            print("bank: " + new String(contract.evaluateTransaction("getPointAmountByUserId", "bank")));
            print("ray: " + new String(contract.evaluateTransaction("getPointAmountByUserId", "ray")));
            print("charles: " + new String(contract.evaluateTransaction("getPointAmountByUserId", "charles")));
            print("zac: " + new String(contract.evaluateTransaction("getPointAmountByUserId", "zac")));
            print(contract.evaluateTransaction("getAllPointTransactionKeys"));
        } catch (final Exception e) {
            e.printStackTrace();
        }

    }

    private void print(final byte[] result) {
        this.print(toString(result));
    }

    private void print(final String result) {
        System.out.println("\n[" + this.testId + "] result: " + prettifyJson(result));
        this.testId++;
    }

    private void print(final String[] results) {

        System.out.println("\n[" + this.testId + "] result: ");

        for (final var result : results) {
            System.out.println(prettifyJson(result));
        }

        this.testId++;
    }

    private static String toString(final byte[] result) {
        return new String(result);
    }

    private String prettifyJson(final String raw) {
        try {
            final Object json = objectMapper.readValue(raw, Object.class);
            final String prettified = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
            return prettified;
        } catch (final JsonParseException e) {
            return raw;
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

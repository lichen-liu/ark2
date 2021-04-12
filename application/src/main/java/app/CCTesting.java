package app;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hyperledger.fabric.gateway.Contract;

public class CCTesting {
    private int testId = 0;
    private ObjectMapper objectMapper;

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
                Map<String, String> payer = Map.of("userId", "bank", "pointAmount", "100");
                List<Map<String, String>> payees = List.of(Map.of("userId", "bank", "pointAmount", "100"));

                final var t0 = toString(contract.submitTransaction("publishNewPointTransaction", "20210412_155300",
                        objectMapper.writeValueAsString(payer), "bank", "reference", "signature(bank)",
                        objectMapper.writeValueAsString(payees)));
                print(t0);
                print(contract.evaluateTransaction("getPointTransactionByKey", t0));
            }

            final var t1 = toString(contract.submitTransaction("publishNewPointTransaction", "20210412_155400",
                    "{\"pointAmount\":100,\"userId\":\"ray\"}", "ray", "reference", "signature(ray)",
                    "[{\"pointAmount\":50,\"userId\":\"charles\"},{\"pointAmount\":50,\"userId\":\"zac\"}]"));
            print(t1);
            print(contract.evaluateTransaction("getPointTransactionByKey", t1));

            final var t2 = toString(contract.submitTransaction("publishNewPointTransaction", "20210412_155500",
                    "{\"pointAmount\":100,\"userId\":\"bank\"}", "bank", "reference", "signature(bank)",
                    "[{\"pointAmount\":100,\"userId\":\"ray\"}]"));
            print(t2);
            print(contract.evaluateTransaction("getPointTransactionByKey", t2));

            final var t3 = toString(contract.submitTransaction("publishNewPointTransaction", "20210412_155600",
                    "{\"pointAmount\":100,\"userId\":\"bank\"}", "bank", "reference", "signature(bank)",
                    "[{\"pointAmount\":100,\"userId\":\"ray\"}]"));
            print(t3);
            print(contract.evaluateTransaction("getPointTransactionByKey", t3));

            final var t4 = toString(contract.submitTransaction("publishNewPointTransaction", "20210412_155700",
                    "{\"pointAmount\":150,\"userId\":\"ray\"}", "ray", "reference", "signature(ray)",
                    "[{\"pointAmount\":50,\"userId\":\"charles\"},{\"pointAmount\":100,\"userId\":\"zac\"}]"));
            print(t4);
            print(contract.evaluateTransaction("getPointTransactionByKey", t4));

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

        for (var result : results) {
            System.out.println(prettifyJson(result));
        }

        this.testId++;
    }

    private static String toString(final byte[] result) {
        return new String(result);
    }

    private String prettifyJson(String raw) {
        try {
            Object json = objectMapper.readValue(raw, Object.class);
            String prettified = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
            return prettified;
        } catch (JsonParseException e) {
            return raw;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

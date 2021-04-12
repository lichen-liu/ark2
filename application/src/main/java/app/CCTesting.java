package app;

import java.io.IOException;

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
            final String r0 = appPeer.publishNewPost("hahaha");
            print(r0);
            print(appPeer.fetchAllPosts());
            print(appPeer.fetchAllPostKeys().toString());
            print(contract.evaluateTransaction("getPostByKey", r0));
        } catch (final Exception e) {
            e.printStackTrace();
        }

    }

    private void print(final byte[] result) {
        this.print(new String(result));
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

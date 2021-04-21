package app.tests.util;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;

import app.repository.data.Like;
import app.repository.data.PointTransaction;
import app.repository.data.Post;

public class Logger {
    private int testId;

    private final ObjectMapper objectMapper;

    public Logger() {
        objectMapper = new ObjectMapper();
    }

    public void print(final Post result) {
        print(result.toString());
    }

    public void print(final Like result) {
        print(result.toString());
    }

    public void print(final PointTransaction result) {
        print(result.toString());
    }

    public void print(final byte[] result) {
        this.print(toString(result));
    }

    public void print(final String result) {
        System.out.println("\n[" + this.testId + "] result: " + prettifyJson(result));
        this.testId++;
    }

    public void print(final String[] results) {

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
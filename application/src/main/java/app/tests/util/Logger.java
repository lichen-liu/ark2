package app.tests.util;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;

import app.repository.data.Like;
import app.repository.data.PointTransaction;
import app.repository.data.Post;

public class Logger {
    private int lineId = 0;
    private String name = "";
    private int subname = 0;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static Map<String, Integer> loggerName = new HashMap<String, Integer>();

    public Logger() {
    }

    public Logger(final String name, final int subname) {
        this.name = name;
        this.subname = subname;
    }

    public Logger(final String name) {
        this.name = name;
        this.subname = loggerName.getOrDefault(this.name, 0);
        loggerName.put(this.name, this.subname + 1);
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
        System.out.println(
                "\n[" + this.name + ":" + this.subname + ":" + this.lineId + "] result: " + prettifyJson(result));
        this.lineId++;
    }

    public void print(final String[] results) {

        System.out.println("\n[" + this.name + ":" + this.subname + ":" + this.lineId + "] result: ");

        for (final var result : results) {
            System.out.println(prettifyJson(result));
        }

        this.lineId++;
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

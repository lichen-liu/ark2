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

    public Logger() {
    }

    public Logger(final String name, final int subname) {
        this.name = name;
        this.subname = subname;
    }

    public void print(final String string) {
        System.out.println("\n[" + this.name + ":" + this.subname + ":" + this.lineId + "]: " + string);
        this.lineId++;
    }

    public void print(final String[] strings) {
        System.out.println("\n[" + this.name + ":" + this.subname + ":" + this.lineId + "]: ");

        for (final var string : strings) {
            System.out.println(string);
        }

        this.lineId++;
    }

    public void printResult(final String result) {
        this.print("RESULT: " + result);
    }

    public void printResult(final String[] results) {
        System.out.println("\n[" + this.name + ":" + this.subname + ":" + this.lineId + "]: RESULT: ");

        for (final var result : results) {
            System.out.println(prettifyJson(result));
        }

        this.lineId++;
    }

    public void print(final Post result) {
        printResult(result.toString());
    }

    public void print(final Like result) {
        printResult(result.toString());
    }

    public void print(final PointTransaction result) {
        printResult(result.toString());
    }

    public void print(final byte[] result) {
        this.printResult(toString(result));
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

    public static class Builder {
        private final String suiteName;
        private final Map<String, Integer> loggerName = new HashMap<String, Integer>();

        public Builder(final String suiteName) {
            this.suiteName = suiteName;
        }

        public Logger create(String name) {
            if (this.suiteName != null) {
                name = suiteName + "::" + name;
            }
            final int subname = loggerName.getOrDefault(name, 0);
            loggerName.put(name, subname + 1);
            return new Logger(name, subname);
        }
    }
}

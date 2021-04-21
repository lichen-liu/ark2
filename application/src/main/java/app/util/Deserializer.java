package app.util;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import app.repository.data.Transaction.Entry;

public interface Deserializer {
    public abstract String[] toStringArray(String str) throws Exception;

    public abstract String transactionEntryToJson(Iterable<Entry> participants) throws JsonProcessingException;

    public abstract String transactionEntriesToJson(Entry participant)
            throws IOException, JsonParseException, JsonMappingException;
}

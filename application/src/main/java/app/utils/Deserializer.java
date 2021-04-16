package app.utils;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import app.repository.contracts.Transaction.Entry;

public interface Deserializer {
    public String[] toStringArray(String str) throws Exception;

    public String transactionEntryToJson(Iterable<Entry> participants) throws JsonProcessingException;

    public String transactionEntriesToJson(Entry participant)
            throws IOException, JsonParseException, JsonMappingException;
}

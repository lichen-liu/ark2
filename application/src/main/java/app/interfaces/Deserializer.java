package app.interfaces;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import app.repository.contracts.Transaction.Participant;

public interface Deserializer {
    public String[] toStringArray(String str) throws Exception;
    public String participantsToJson(Iterable<Participant> participants) throws JsonProcessingException;
    public String participantToJson(Participant participant) throws IOException, JsonParseException, JsonMappingException;

}

package app.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

//import com.owlike.genson.Genson;

import app.interfaces.Deserializer;
import app.repository.contracts.Transaction.Participant;

public class GensonDeserializer implements Deserializer {
    private ObjectMapper om;
    public GensonDeserializer() {
        this.om = new ObjectMapper();
    }
    @Override
    public String[] toStringArray(String str) throws IOException, JsonParseException, JsonMappingException {
        return om.readValue(str, String[].class);
    }

    @Override
    public String participantsToJson(final Iterable<Participant> participants) throws JsonProcessingException {
        var list = new ArrayList<Map<String, String>> ();
        participants.forEach(p -> {
            try {
                list.add(participantMap(p));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return om.writeValueAsString(list);
    }

    @Override
    public String participantToJson(final Participant participant) throws IOException, JsonParseException, JsonMappingException {
        return om.writeValueAsString(participantMap(participant));
    }

    private Map<String, String> participantMap(final Participant participant) {
        return Map.of("userId", participant.userId,"pointAmount", Double.toString(participant.amount));
    }
}

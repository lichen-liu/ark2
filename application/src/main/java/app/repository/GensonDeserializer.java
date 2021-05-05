package app.repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import app.util.Deserializer;

public class GensonDeserializer implements Deserializer {
    private final ObjectMapper om;

    public GensonDeserializer() {
        this.om = new ObjectMapper();
    }

    @Override
    public String[] toStringArray(final String str) throws IOException, JsonParseException, JsonMappingException {
        return om.readValue(str, String[].class);
    }

    @Override
    public String transactionEntriesToJson(final Iterable<PointTransaction.Entry> participants)
            throws JsonProcessingException {
        final var list = new ArrayList<Map<String, String>>();
        participants.forEach(p -> {
            try {
                list.add(participantMap(p));
            } catch (final Exception e) {
                e.printStackTrace();
            }
        });
        return om.writeValueAsString(list);
    }

    @Override
    public String transactionEntryToJson(final PointTransaction.Entry participant)
            throws IOException, JsonParseException, JsonMappingException {
        return om.writeValueAsString(participantMap(participant));
    }

    private Map<String, String> participantMap(final PointTransaction.Entry participant) {
        return Map.of("userId", participant.userId, "pointAmount", Double.toString(participant.pointAmount));
    }
}

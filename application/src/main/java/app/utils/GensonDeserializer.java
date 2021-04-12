package app.utils;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

//import com.owlike.genson.Genson;

import app.interfaces.Deserializer;

public class GensonDeserializer implements Deserializer {
    private ObjectMapper om;
    public GensonDeserializer() {
        this.om = new ObjectMapper();
    }
    @Override
    public String[] toStringArray(String str) throws IOException, JsonParseException, JsonMappingException {
        return om.readValue(str, String[].class);
    }
}

package app.utils;

import com.owlike.genson.Genson;

import app.interfaces.Deserializer;

public class GensonDeserializer implements Deserializer {
    private Genson genson;
    public GensonDeserializer() {
        this.genson = new Genson();
    }
    @Override
    public String[] toStringArray(String str) {
        return genson.deserialize(str, String[].class);
    }
}

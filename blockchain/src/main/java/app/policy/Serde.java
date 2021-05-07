package app.policy;

import java.io.Serializable;

public interface Serde {
    public abstract <T> T deserialize(byte[] bytes, Class<T> classType);

    public abstract byte[] serialize(Serializable object);
}

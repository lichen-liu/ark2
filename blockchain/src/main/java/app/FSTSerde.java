package app;

import java.io.Serializable;

import org.nustaq.serialization.FSTConfiguration;

import app.policy.Serde;

public class FSTSerde implements Serde {
    private final static FSTConfiguration conf = FSTConfiguration.createDefaultConfiguration();
    static {
        conf.registerClass(Post.class, Like.class, Dislike.class, PointTransaction.class, PointTransaction.Entry.class,
                PointTransaction.Tracking.class);
    }

    public static FSTSerde create() {
        return new FSTSerde();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T deserialize(final byte[] bytes, final Class<T> classType) {
        return (T) conf.asObject(bytes);
    }

    @Override
    public byte[] serialize(final Serializable object) {
        return conf.asByteArray(object);
    }
}

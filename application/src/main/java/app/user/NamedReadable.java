package app.user;

public interface NamedReadable extends Anonymous, Identifiable {
    public default String[] fetchMyPostKeys() {
        return fetchPostKeysByUserId(getPublicKeyString());
    }

    public default String[] fetchMyPointTransactionKeys() {
        return fetchPointTransactionKeysByUserId(getPublicKeyString());
    }

    public default String getMyPointAmount() {
        return getPointAmountByUserId(getPublicKeyString());
    }
}

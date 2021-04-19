package app.user;

public interface NamedReadableService extends AnonymousService, Identifiable {
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

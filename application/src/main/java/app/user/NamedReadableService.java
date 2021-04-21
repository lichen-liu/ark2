package app.user;

public interface NamedReadableService extends AnonymousService, Identifiable {
    public default String[] fetchMyPostKeys() {
        return fetchPostKeysByUserId(getPublicKeyString());
    }

    public default String[] fetchMyPayerPointTransactionKeys() {
        return fetchPointTransactionKeysByPayerUserId(getPublicKeyString());
    }

    public default String computeMyPointAmount() {
        return computePointAmountByUserId(getPublicKeyString());
    }
}

package app.user;

public interface NamedReadableService extends AnonymousService, Identifiable {
    public default String[] fetchMyPostKeys() {
        return fetchPostKeysByUserId(getPublicKeyString());
    }

    public default String[] fetchMyIssuerPointTransactionKeys() {
        return fetchPointTransactionKeysByIssuerUserId(getPublicKeyString());
    }

    public default String computeMyPointBalance() {
        return computePointBalanceByUserId(getPublicKeyString());
    }
}

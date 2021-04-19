package app.user;

import app.repository.data.Transaction;

public interface NamedWriteable extends Repository, Signable {
    public default String publishNewPost(final String content) {
        try {
            return getPostRepository().insertNewPost(content, getPublicKey(), getPrivateKey());
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public default String publishNewTransaction(final Transaction transaction) {
        try {
            return getPointTransactionRepository().insertNewTransaction(transaction.reference, transaction,
                    getPublicKey(), getPrivateKey());
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public default String publishNewLike(final String postKey) {
        try {
            return new String(getLikeRepository().insertNewLike(postKey, 1.0, getPublicKey(), getPrivateKey()));
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

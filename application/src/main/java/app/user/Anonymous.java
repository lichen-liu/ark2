package app.user;

import org.hyperledger.fabric.gateway.ContractException;

import app.repository.data.Like;
import app.repository.data.PointTransaction;
import app.repository.data.Post;

public interface Anonymous extends Repository {
    public default String[] fetchPostKeys() {
        try {
            return getPostRepository().selectObjectKeysByCustomKey();
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public default String[] fetchPostKeysByUserId(final String userId) {
        try {
            return getPostRepository().selectObjectKeysByCustomKey(userId);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public default String[] fetchPointTransactionKeys() {
        try {
            return getPointTransactionRepository().selectObjectKeysByCustomKey();
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public default String[] fetchPointTransactionKeysByUserId(final String userId) {
        try {
            return getPostRepository().selectObjectKeysByCustomKey(userId);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public default String[] fetchLikeKeysByPostKey(final String postKey) {
        try {
            return getLikeRepository().selectObjectKeysByCustomKey(postKey);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public default PointTransaction fetchPointTransactionByPointTransactionKey(final String key) {
        try {
            return getPointTransactionRepository().selectObjectsByKeys(key).get(0);
        } catch (final Exception e) {
        }
        return null;
    }

    public default Post fetchPostByPostKey(final String key) {
        try {
            return getPostRepository().selectObjectsByKeys(key).get(0);
        } catch (final Exception e) {
        }
        return null;
    }

    public default Like fetchLikeByLikeKey(final String likeKey) {
        try {
            return getLikeRepository().selectObjectsByKeys(likeKey).get(0);
        } catch (final Exception e) {
        }
        return null;
    }

    public default String getPointAmountByUserId(final String userId) {
        try {
            return getPointTransactionRepository().getPointAmount(userId);
        } catch (final ContractException e) {
            e.printStackTrace();
        }
        return null;
    }

    public default Like[] fetchLikesByPostKey(final String postKey) {
        try {
            return getLikeRepository().selectObjectsByCustomKeys(postKey).toArray(Like[]::new);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

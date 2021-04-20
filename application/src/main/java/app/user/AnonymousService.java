package app.user;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

import org.hyperledger.fabric.gateway.ContractException;

import app.repository.Hash;
import app.repository.data.Like;
import app.repository.data.PointTransaction;
import app.repository.data.Post;
import app.utils.ByteUtils;
import app.utils.Cryptography;

public interface AnonymousService extends Repository {
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
            return getPointTransactionRepository().selectObjectKeysByCustomKey(userId);
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

    public static boolean verifyPostSignature(final Post post) {
        try {
            final byte[] hashedContentBytes = Hash.generatePostHash(post.timestamp, post.content, post.userId);
            final PublicKey publicKey = Cryptography.parsePublicKey(ByteUtils.toByteArray(post.userId));
            return Cryptography.verify(publicKey, hashedContentBytes, ByteUtils.toByteArray(post.signature));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | IllegalArgumentException | InvalidKeyException
                | SignatureException e) {
            return false;
        }
    }

    public static boolean verifyLikeSignature(final Like like) {
        try {
            final byte[] hashedContentBytes = Hash.generateLikeHash(like.timestamp, like.postKey, like.userId);
            final PublicKey publicKey = Cryptography.parsePublicKey(ByteUtils.toByteArray(like.userId));
            return Cryptography.verify(publicKey, hashedContentBytes, ByteUtils.toByteArray(like.signature));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | IllegalArgumentException | InvalidKeyException
                | SignatureException e) {
            return false;
        }
    }

    public static boolean verifyPointTransactionSignature(final PointTransaction pointTransaction) {
        try {
            final byte[] hashedContentBytes = Hash.generatePointTransactionHash(pointTransaction.timestamp,
                    pointTransaction.payerEntry.userId, String.valueOf(pointTransaction.payerEntry.pointAmount),
                    pointTransaction.issuerUserId);
            final PublicKey publicKey = Cryptography
                    .parsePublicKey(ByteUtils.toByteArray(pointTransaction.issuerUserId));
            return Cryptography.verify(publicKey, hashedContentBytes,
                    ByteUtils.toByteArray(pointTransaction.signature));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | IllegalArgumentException | InvalidKeyException
                | SignatureException | NullPointerException e) {
            return false;
        }
    }

    public interface VerificationResult {
        public abstract boolean isValid();

        public abstract String[] getItems();

        public default String getItemsString() {
            return String.join(" ", getItems());
        }

        public static final VerificationResult INVALID = new VerificationResult() {
            @Override
            public boolean isValid() {
                return false;
            }

            @Override
            public String[] getItems() {
                return new String[] { "Invalid!" };
            }
        };
    }

    public default VerificationResult verifyPost(final String postKey) {
        final Post post = fetchPostByPostKey(postKey);
        if (post == null) {
            return VerificationResult.INVALID;
        }

        final boolean isSignatureValid = verifyPostSignature(post);
        final String signatureItem = isSignatureValid ? "Signature Passed!" : "Signature Failed!";
        return new VerificationResult() {
            @Override
            public boolean isValid() {
                return isSignatureValid;
            }

            @Override
            public String[] getItems() {
                return new String[] { signatureItem };
            }
        };
    }

    public default VerificationResult verifyLike(final String likeKey) {
        final Like like = fetchLikeByLikeKey(likeKey);
        if (like == null) {
            return VerificationResult.INVALID;
        }

        final boolean isSignatureValid = verifyLikeSignature(like);
        final String signatureItem = isSignatureValid ? "Signature Passed!" : "Signature Failed!";
        return new VerificationResult() {
            @Override
            public boolean isValid() {
                return isSignatureValid;
            }

            @Override
            public String[] getItems() {
                return new String[] { signatureItem };
            }
        };
    }

    public default VerificationResult verifyPointTransaction(final String pointTransactionKey) {
        final PointTransaction pointTransaction = fetchPointTransactionByPointTransactionKey(pointTransactionKey);
        if (pointTransaction == null) {
            return VerificationResult.INVALID;
        }

        final boolean isSignatureValid = verifyPointTransactionSignature(pointTransaction);
        final String signatureItem = isSignatureValid ? "Signature Passed!" : "Signature Failed!";
        return new VerificationResult() {
            @Override
            public boolean isValid() {
                return isSignatureValid;
            }

            @Override
            public String[] getItems() {
                return new String[] { signatureItem };
            }
        };
    }
}

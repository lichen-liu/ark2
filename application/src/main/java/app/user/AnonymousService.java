package app.user;

import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import javax.annotation.Nullable;

import org.hyperledger.fabric.gateway.ContractException;

import app.repository.Hash;
import app.repository.data.Dislike;
import app.repository.data.Like;
import app.repository.data.PointTransaction;
import app.repository.data.Post;
import app.util.ByteUtils;
import app.util.Cryptography;

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

    public default String[] fetchPointTransactionKeysByIssuerUserId(final String userId) {
        try {
            return getPointTransactionRepository().selectObjectKeysByCustomKey(userId);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public default String[] computePointTransactionKeysByUserId(final String userId) {
        try {
            return getPointTransactionRepository().computePointTransactionKeysByUserId(userId);
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

    public default String[] fetchDislikeKeysByPostKey(final String postKey) {
        try {
            return getDislikeRepository().selectObjectKeysByCustomKey(postKey);
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

    public default Dislike fetchDislikeByDislikeKey(final String dislikeKey) {
        try {
            return getDislikeRepository().selectObjectsByKeys(dislikeKey).get(0);
        } catch (final Exception e) {
        }
        return null;
    }

    public default String computePointBalanceByUserId(final String userId) {
        try {
            return getPointTransactionRepository().computePointBalanceByUserId(userId);
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
            final PublicKey publicKey = Cryptography.parsePublicKey(ByteUtils.fromAsciiString(post.userId));
            return Cryptography.verify(publicKey, hashedContentBytes, ByteUtils.fromAsciiString(post.signature));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | IllegalArgumentException | InvalidKeyException
                | SignatureException e) {
            return false;
        }
    }

    public static boolean verifyLikeSignature(final Like like) {
        try {
            final byte[] hashedContentBytes = Hash.generateLikeHash(like.timestamp, like.postKey, like.userId);
            final PublicKey publicKey = Cryptography.parsePublicKey(ByteUtils.fromAsciiString(like.userId));
            return Cryptography.verify(publicKey, hashedContentBytes, ByteUtils.fromAsciiString(like.signature));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | IllegalArgumentException | InvalidKeyException
                | SignatureException e) {
            return false;
        }
    }

    public static boolean verifyDislikeSignature(final Dislike dislike) {
        try {
            final byte[] hashedContentBytes = Hash.generateDislikeHash(dislike.timestamp, dislike.postKey,
                    dislike.userId);
            final PublicKey publicKey = Cryptography.parsePublicKey(ByteUtils.fromAsciiString(dislike.userId));
            return Cryptography.verify(publicKey, hashedContentBytes, ByteUtils.fromAsciiString(dislike.signature));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | IllegalArgumentException | InvalidKeyException
                | SignatureException e) {
            return false;
        }
    }

    public static boolean verifyPointTransactionSignature(final PointTransaction pointTransaction) {
        try {
            final byte[] hashedContentBytes = Hash.generatePointTransactionHash(pointTransaction.timestamp,
                    pointTransaction.issuerUserId, pointTransaction.payerEntries);
            final PublicKey publicKey = Cryptography
                    .parsePublicKey(ByteUtils.fromAsciiString(pointTransaction.issuerUserId));
            return Cryptography.verify(publicKey, hashedContentBytes,
                    ByteUtils.fromAsciiString(pointTransaction.signature));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | IllegalArgumentException | InvalidKeyException
                | SignatureException | NullPointerException e) {
            return false;
        }
    }

    public interface VerificationResult {
        public abstract boolean isValid();

        public abstract List<String> getItems();

        public default String getItemsString() {
            return String.join(" ", getItems());
        }

        public static VerificationResult invalid(final String entity) {
            return new VerificationResult() {
                @Override
                public boolean isValid() {
                    return false;
                }

                @Override
                public List<String> getItems() {
                    return List.of("Invalid " + entity + "!");
                }
            };
        }
    }

    public default VerificationResult verifyPost(final String postKey,
            @Nullable final Function<? super Post, ? extends VerificationResult> verifier) {
        final Post post = fetchPostByPostKey(postKey);
        if (post == null) {
            return VerificationResult.invalid("Post");
        }

        final boolean isSignatureValid = verifyPostSignature(post);
        final String signatureItem = isSignatureValid ? "Post Signature Ok!" : "Post Signature Failed!";

        final VerificationResult verifierValidity = verifier != null ? verifier.apply(post) : null;

        return new VerificationResult() {
            @Override
            public boolean isValid() {
                if (verifierValidity == null) {
                    return isSignatureValid;
                } else {
                    return isSignatureValid && verifierValidity.isValid();
                }
            }

            @Override
            public List<String> getItems() {
                final List<String> items = new ArrayList<String>(List.of(signatureItem));
                if (verifierValidity != null) {
                    items.addAll(verifierValidity.getItems());
                }
                return items;
            }
        };
    }

    public default VerificationResult verifyLike(final String likeKey) {
        final Like like = fetchLikeByLikeKey(likeKey);
        if (like == null) {
            return VerificationResult.invalid("Like");
        }

        final boolean isSignatureValid = verifyLikeSignature(like);
        final String signatureItem = isSignatureValid ? "Like Signature Ok!" : "Like Signature Failed!";

        final Function<Post, ? extends VerificationResult> postVerifier = post -> {
            final Function<PointTransaction, ? extends VerificationResult> pointTransactionVerifier = pointTransaction -> {
                final boolean isCrossReferenced = likeKey.equals(pointTransaction.reference);
                final String crossReferenceString = isCrossReferenced ? "Like-Point Transaction Reference Ok!"
                        : "Like-Point Transaction Reference Failed!";

                final boolean isPayerVerified = pointTransaction.payerEntries.length == 1
                        && like.userId.equals(pointTransaction.payerEntries[0].userId);
                final String payerVerifiedString = isPayerVerified ? "Like-Point Transaction Payer Ok!"
                        : "Like-Point Transaction Payer failed!";

                final boolean isPayeeVerified = pointTransaction.payeeEntries.length > 0 && Arrays
                        .stream(pointTransaction.payeeEntries).anyMatch(payee -> post.userId.equals(payee.userId));
                final String payeeVerifiedString = isPayeeVerified ? "Like-Point Transaction Payee Ok!"
                        : "Like-Point Transaction Payee failed!";

                return new VerificationResult() {
                    @Override
                    public boolean isValid() {
                        return isCrossReferenced && isPayerVerified && isPayeeVerified;
                    }

                    @Override
                    public List<String> getItems() {
                        return List.of(crossReferenceString, payerVerifiedString, payeeVerifiedString);
                    }
                };
            };
            final var pointTransactionValidity = verifyPointTransaction(like.pointTransactionKey,
                    pointTransactionVerifier);
            return new VerificationResult() {
                @Override
                public boolean isValid() {
                    return pointTransactionValidity.isValid();
                }

                @Override
                public List<String> getItems() {
                    return pointTransactionValidity.getItems();
                }

            };
        };
        final var postValidity = verifyPost(like.postKey, postVerifier);

        return new VerificationResult() {
            @Override
            public boolean isValid() {
                return isSignatureValid && postValidity.isValid();
            }

            @Override
            public List<String> getItems() {
                final List<String> items = new ArrayList<String>(List.of(signatureItem));
                items.addAll(postValidity.getItems());
                return items;
            }
        };
    }

    public default VerificationResult verifyDislike(final String dislikeKey) {
        final Dislike dislike = fetchDislikeByDislikeKey(dislikeKey);
        if (dislike == null) {
            return VerificationResult.invalid("Dislike");
        }

        final boolean isSignatureValid = verifyDislikeSignature(dislike);
        final String signatureItem = isSignatureValid ? "Dislike Signature Ok!" : "Dislike Signature Failed!";

        final Function<Post, ? extends VerificationResult> postVerifier = post -> {
            final Function<PointTransaction, ? extends VerificationResult> pointTransactionVerifier = pointTransaction -> {
                final boolean isCrossReferenced = dislikeKey.equals(pointTransaction.reference);
                final String crossReferenceString = isCrossReferenced ? "Dislike-Point Transaction Reference Ok!"
                        : "Dislike-Point Transaction Reference Failed!";

                final boolean isPayerVerified = pointTransaction.payerEntries.length == 2
                        && Arrays.stream(pointTransaction.payerEntries)
                                .anyMatch(payer -> dislike.userId.equals(payer.userId))
                        && Arrays.stream(pointTransaction.payerEntries)
                                .anyMatch(payer -> post.userId.equals(payer.userId))
                        && BigDecimal.valueOf(pointTransaction.payerEntries[0].pointAmount)
                                .equals(BigDecimal.valueOf(pointTransaction.payerEntries[1].pointAmount));
                final String payerVerifiedString = isPayerVerified ? "Dislike-Point Transaction Payer Ok!"
                        : "Dislike-Point Transaction Payer failed!";

                final boolean isPayeeVerified = pointTransaction.payeeEntries.length > 0 && Arrays
                        .stream(pointTransaction.payeeEntries).anyMatch(payee -> post.userId.equals(payee.userId));
                final String payeeVerifiedString = isPayeeVerified ? "Dislike-Point Transaction Payee Ok!"
                        : "Dislike-Point Transaction Payee failed!";

                return new VerificationResult() {
                    @Override
                    public boolean isValid() {
                        return isCrossReferenced && isPayerVerified && isPayeeVerified;
                    }

                    @Override
                    public List<String> getItems() {
                        return List.of(crossReferenceString, payerVerifiedString, payeeVerifiedString);
                    }
                };
            };
            final var pointTransactionValidity = verifyPointTransaction(dislike.pointTransactionKey,
                    pointTransactionVerifier);
            return new VerificationResult() {
                @Override
                public boolean isValid() {
                    return pointTransactionValidity.isValid();
                }

                @Override
                public List<String> getItems() {
                    return pointTransactionValidity.getItems();
                }

            };
        };
        final var postValidity = verifyPost(dislike.postKey, postVerifier);

        return new VerificationResult() {
            @Override
            public boolean isValid() {
                return isSignatureValid && postValidity.isValid();
            }

            @Override
            public List<String> getItems() {
                final List<String> items = new ArrayList<String>(List.of(signatureItem));
                items.addAll(postValidity.getItems());
                return items;
            }
        };
    }

    public default VerificationResult verifyPointTransaction(final String pointTransactionKey,
            @Nullable final Function<? super PointTransaction, ? extends VerificationResult> verifier) {

        final PointTransaction pointTransaction = fetchPointTransactionByPointTransactionKey(pointTransactionKey);
        if (pointTransaction == null) {
            return VerificationResult.invalid("Point Transaction");
        }

        final boolean isSignatureValid = verifyPointTransactionSignature(pointTransaction);
        final String signatureItem = isSignatureValid ? "Point Transaction Signature Ok!"
                : "Point Transaction Signature Failed!";

        final VerificationResult verifierValidity = verifier != null ? verifier.apply(pointTransaction) : null;

        return new VerificationResult() {
            @Override
            public boolean isValid() {
                if (verifierValidity == null) {
                    return isSignatureValid;
                } else {
                    return isSignatureValid && verifierValidity.isValid();
                }
            }

            @Override
            public List<String> getItems() {
                final List<String> items = new ArrayList<String>(List.of(signatureItem));
                if (verifierValidity != null) {
                    items.addAll(verifierValidity.getItems());
                }
                return items;
            }
        };
    }

    public default VerificationResult verifyPointTransaction(final String pointTransactionKey) {
        return verifyPointTransaction(pointTransactionKey, null);
    }
}

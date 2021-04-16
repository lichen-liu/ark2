package app;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.concurrent.TimeoutException;

import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.ContractException;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.X509Identity;

import app.repository.contracts.Transaction;

public class PublishableAppUser extends ReadOnlyAppUser {
    private final PrivateKey privateKey;

    public PublishableAppUser(final Wallet wallet, final Contract contract, final String userId)
            throws InvalidKeySpecException, IOException {
        super(wallet, contract, userId);
        final X509Identity adminIdentity = (X509Identity) wallet.get(userId);
        this.privateKey = adminIdentity.getPrivateKey();
    }

    public PublishableAppUser(final Contract contract, final PublicKey publicKey, final PrivateKey privateKey) {
        super(contract, publicKey);
        this.privateKey = privateKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public String publishNewPost(final String content) throws ContractException, TimeoutException, InterruptedException,
            InvalidKeyException, NoSuchAlgorithmException, SignatureException {
        return super.getPostRepository().insertNewPost(super.getContract(), content, super.getPublicKey(), privateKey);
    }

    public String publishNewTransaction(final Transaction transaction) throws Exception {
        return super.getTransactionRepository().insertNewTransaction(super.getContract(), transaction.reference,
                transaction, super.getPublicKey(), privateKey);
    }

    public String publishNewLike(final String postKey, final Transaction.Entry likeInfo) {
        try {
            return new String(super.getLikeRepository().insertNewLike(super.getContract(), postKey, likeInfo,
                    super.getPublicKey(), privateKey));
        } catch (final Exception e) {
        }

        return postKey;
    }
}

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

import app.repository.LikeRepository;
import app.repository.PointTransactionRepository;
import app.repository.PostRepository;
import app.repository.contracts.Transaction;
import app.utils.ByteUtils;

public class AppClient {

    private final Contract contract;
    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    private PostRepository postRepository;
    private LikeRepository likeRepository;
    private PointTransactionRepository transactionRepository;

    public AppClient(final Wallet wallet, final Contract contract, final String userId)
            throws InvalidKeySpecException, IOException {
        this.contract = contract;
        final X509Identity adminIdentity = (X509Identity) wallet.get(userId);
        this.privateKey = adminIdentity.getPrivateKey();
        this.publicKey = adminIdentity.getCertificate().getPublicKey();
        InitRepositories();
    }

    public AppClient(final Contract contract, final PublicKey publicKey, final PrivateKey privateKey) {
        this.contract = contract;
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        InitRepositories();
    }

    public String publishNewPost(final String content) throws ContractException, TimeoutException, InterruptedException,
            InvalidKeyException, NoSuchAlgorithmException, SignatureException {
        return postRepository.insertNewPost(contract, content, publicKey, privateKey);
    }

    public String[] fetchUserPostKeys(final String userId) throws Exception {
        return postRepository.selectObjectKeysByCustomKey(userId);
    }

    public String[] fetchAllPostKeys() {
        try {
            return postRepository.selectObjectKeysByCustomKey();
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String[] fetchAllUserPosts(final String userId) throws Exception {
        return postRepository.selectObjectsByCustomKeys(userId);
    }

    public String[] fetchAllPosts() throws Exception {
        return postRepository.selectObjectsByCustomKeys();
    }

    public String publishNewTransaction(final Transaction transaction) throws Exception {
        return transactionRepository.insertNewTransaction(contract, transaction.reference, transaction, publicKey,
                privateKey);
    }

    public String getPointAmount() throws ContractException {
        return new String(contract.evaluateTransaction("getPointAmountByUserId",
                ByteUtils.bytesToHexString(publicKey.getEncoded())));
    }

    public String publishNewLike(final String postKey, final Transaction.Entry likeInfo) {
        try {
            return new String(likeRepository.insertNewLike(contract, postKey, likeInfo, publicKey, privateKey));
        } catch (final Exception e) {
        }

        return postKey;
    }

    public Contract getContract() {
        return contract;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    private void InitRepositories() {
        this.postRepository = new PostRepository(contract);
        this.transactionRepository = new PointTransactionRepository(contract);
        this.likeRepository = new LikeRepository(contract);
    }
}
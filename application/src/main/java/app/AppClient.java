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

public class AppClient {

    private Contract contract;
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private PostRepository postRepository;
    private LikeRepository likeRepository;
    private PointTransactionRepository transactionRepository;

    public AppClient(Wallet wallet, Contract contract, String userId) throws InvalidKeySpecException, IOException {
        this.contract = contract;
        this.postRepository = new PostRepository(contract);
        X509Identity adminIdentity = (X509Identity) wallet.get(userId);

        this.privateKey = adminIdentity.getPrivateKey();
        this.publicKey = adminIdentity.getCertificate().getPublicKey();
    }

    public String publishNewPost(String content) throws ContractException, TimeoutException, InterruptedException, InvalidKeyException, NoSuchAlgorithmException, SignatureException{
        return postRepository.insertNewPost(contract, content, publicKey, privateKey);
    }

    public String[] fetchUserPostKeys(String userId) throws Exception {
        return postRepository.selectObjectKeysByCustomKey(userId);
    }

    public String[] fetchAllPostKeys() throws Exception {
        return postRepository.selectObjectKeysByCustomKey();
    }   
    
    public String[] fetchAllUserPosts(String userId) throws Exception {
        return postRepository.selectObjectsByCustomKeys(userId);
    }

    public String[] fetchAllPosts() throws Exception {
        return postRepository.selectObjectsByCustomKeys();
    }

    public Contract getContract() {
        return contract;
    }
}
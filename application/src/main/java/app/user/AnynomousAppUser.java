package app.user;

import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.ContractException;

import app.repository.LikeRepository;
import app.repository.PointTransactionRepository;
import app.repository.PostRepository;

public class AnynomousAppUser {
    private final Contract contract;
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final PointTransactionRepository transactionRepository;

    public Contract getContract() {
        return contract;
    }

    public PostRepository getPostRepository() {
        return postRepository;
    }

    public LikeRepository getLikeRepository() {
        return likeRepository;
    }

    public PointTransactionRepository getTransactionRepository() {
        return transactionRepository;
    }

    public AnynomousAppUser(final Contract contract) {
        this.contract = contract;
        this.postRepository = new PostRepository(contract);
        this.transactionRepository = new PointTransactionRepository(contract);
        this.likeRepository = new LikeRepository(contract);
    }

    public String[] fetchAllPostKeys() {
        try {
            return postRepository.selectObjectKeysByCustomKey();
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String[] fetchAllPosts() {
        try {
            return postRepository.selectObjectsByCustomKeys();
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String fetchPostByPostKey(final String key) {
        try {
            return postRepository.selectObjectsByKeys(key)[0];
        } catch (final ContractException e) {
        }
        return null;
    }

    public String[] fetchLikeKeysByPostKey(final String postKey) {
        try {
            return likeRepository.selectObjectKeysByCustomKey(postKey);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String fetchLikeByLikeKey(final String likeKey) {
        try {
            return likeRepository.selectObjectsByKeys(likeKey)[0];
        } catch (final ContractException e) {
        }
        return null;
    }
}

package app.user;

import org.hyperledger.fabric.gateway.Contract;

import app.repository.LikeRepository;
import app.repository.PointTransactionRepository;
import app.repository.PostRepository;

public class RepositoryProvider implements Repository {
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final PointTransactionRepository pointTransactionRepository;

    @Override
    public PostRepository getPostRepository() {
        return postRepository;
    }

    @Override
    public LikeRepository getLikeRepository() {
        return likeRepository;
    }

    @Override
    public PointTransactionRepository getPointTransactionRepository() {
        return pointTransactionRepository;
    }

    public RepositoryProvider(final Contract contract) {
        this.postRepository = new PostRepository(contract);
        this.pointTransactionRepository = new PointTransactionRepository(contract);
        this.likeRepository = new LikeRepository(contract);
    }
}

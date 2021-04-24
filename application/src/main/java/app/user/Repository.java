package app.user;

import app.repository.DislikeRepository;
import app.repository.LikeRepository;
import app.repository.PointTransactionRepository;
import app.repository.PostRepository;

public interface Repository {
    public abstract PostRepository getPostRepository();

    public abstract LikeRepository getLikeRepository();

    public abstract DislikeRepository getDislikeRepository();

    public abstract PointTransactionRepository getPointTransactionRepository();
}
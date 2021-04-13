package app.repository.contracts;

public class Post implements RepositoryContract {
    public Post() {
    }

    @Override
    public String getKeySelectionQuery() {
        return "getAllPostKeys";
    }

    @Override
    public String getObjectSelectionQuery() {
        return "getAllPostKeysByUserId";
    }

    @Override
    public String getInsertionQuery() {
        return "getInsertionQuery";
    }
}

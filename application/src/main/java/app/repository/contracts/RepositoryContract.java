package app.repository.contracts;

public interface RepositoryContract {
    public String getInsertionQuery();
    public String getKeySelectionQuery();
    public String getObjectSelectionQuery();
}

package app.user;

import java.security.PrivateKey;
import java.security.PublicKey;

import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.ContractException;

import app.repository.data.Transaction;
import app.user.internal.RepositoryHolder;
import app.user.internal.SignableHolder;

public class ServiceProvider {
    public static AnonymousService createAnonymousService(final Contract contract) {
        class AnonymousAppUser extends RepositoryHolder implements AnonymousService {
            public AnonymousAppUser(final Contract contract) {
                super(contract);
            }
        }

        return new AnonymousAppUser(contract);
    }

    public static NamedService createNamedService(final Contract contract, final PublicKey publicKey,
            final PrivateKey privateKey) {
        class PublishableAppUser extends SignableHolder implements NamedService {

            private Integer maxRetries = 5;
            private Retry<String[]> stringArrayRetry = new Retry<String[]>(maxRetries, ContractException.class);
            private Retry<String> stringRetry = new Retry<String>(maxRetries, ContractException.class);

            public PublishableAppUser(final Contract contract, final PublicKey publicKey, final PrivateKey privateKey) {
                super(contract, publicKey, privateKey);
            }

            @Override
            public String[] fetchMyPostKeys(){
                return stringArrayRetry.run(() -> NamedService.super.fetchMyPostKeys());
            }

            @Override
            public String[] fetchMyPayerPointTransactionKeys() {
                return stringArrayRetry.run(() -> NamedService.super.fetchMyPayerPointTransactionKeys());
            }

            @Override
            public String computeMyPointAmount() {
                return stringRetry.run(() -> NamedService.super.computeMyPointAmount());
            }

            @Override
            public String publishNewPost(final String content) {
                return stringRetry.run(() -> NamedService.super.publishNewPost(content));
            }

            @Override
            public String publishNewTransaction(final Transaction transaction) {
                return stringRetry.run(() -> NamedService.super.publishNewTransaction(transaction));
            }

            @Override
            public String publishNewLike(final String postKey) {
                return stringRetry.run(() -> NamedService.super.publishNewLike(postKey));
            }
        }
        return new PublishableAppUser(contract, publicKey, privateKey);
    }
}

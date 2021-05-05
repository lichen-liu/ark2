package app.user;

import java.security.PrivateKey;
import java.security.PublicKey;

import org.hyperledger.fabric.gateway.Contract;

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

            public PublishableAppUser(final Contract contract, final PublicKey publicKey, final PrivateKey privateKey) {
                super(contract, publicKey, privateKey);
            }
        }
        return new PublishableAppUser(contract, publicKey, privateKey);
    }
}

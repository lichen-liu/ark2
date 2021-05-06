package app.service;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.X509Identity;

import app.repository.DislikeRepository;
import app.repository.LikeRepository;
import app.repository.PointTransactionRepository;
import app.repository.PostRepository;

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

    private static class IdentifiableHolder extends RepositoryHolder implements Identifiable {
        private final PublicKey publicKey;

        public IdentifiableHolder(final Wallet wallet, final Contract contract, final String userName)
                throws InvalidKeySpecException, IOException {
            super(contract);
            final X509Identity adminIdentity = (X509Identity) wallet.get(userName);
            this.publicKey = adminIdentity.getCertificate().getPublicKey();
        }

        public IdentifiableHolder(final Contract contract, final PublicKey publicKey) {
            super(contract);
            this.publicKey = publicKey;
        }

        @Override
        public PublicKey getPublicKey() {
            return publicKey;
        }
    }

    private static class RepositoryHolder implements Repository {
        private final PostRepository postRepository;
        private final LikeRepository likeRepository;
        private final DislikeRepository dislikeRepository;
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
        public DislikeRepository getDislikeRepository() {
            return dislikeRepository;
        }

        @Override
        public PointTransactionRepository getPointTransactionRepository() {
            return pointTransactionRepository;
        }

        public RepositoryHolder(final Contract contract) {
            this.postRepository = new PostRepository(contract);
            this.pointTransactionRepository = new PointTransactionRepository(contract);
            this.likeRepository = new LikeRepository(contract);
            this.dislikeRepository = new DislikeRepository(contract);
        }
    }

    private static class SignableHolder extends IdentifiableHolder implements Signable {
        private final PrivateKey privateKey;

        public SignableHolder(final Wallet wallet, final Contract contract, final String userName)
                throws InvalidKeySpecException, IOException {
            super(wallet, contract, userName);
            final X509Identity adminIdentity = (X509Identity) wallet.get(userName);
            this.privateKey = adminIdentity.getPrivateKey();
        }

        public SignableHolder(final Contract contract, final PublicKey publicKey, final PrivateKey privateKey) {
            super(contract, publicKey);
            this.privateKey = privateKey;
        }

        public PrivateKey getPrivateKey() {
            return privateKey;
        }
    }
}

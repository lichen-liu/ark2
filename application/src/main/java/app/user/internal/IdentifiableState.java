package app.user.internal;

import java.io.IOException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.X509Identity;

import app.user.Identifiable;

public class IdentifiableState extends RepositoryState implements Identifiable {
    private final PublicKey publicKey;

    public IdentifiableState(final Wallet wallet, final Contract contract, final String userName)
            throws InvalidKeySpecException, IOException {
        super(contract);
        final X509Identity adminIdentity = (X509Identity) wallet.get(userName);
        this.publicKey = adminIdentity.getCertificate().getPublicKey();
    }

    public IdentifiableState(final Contract contract, final PublicKey publicKey) {
        super(contract);
        this.publicKey = publicKey;
    }

    @Override
    public PublicKey getPublicKey() {
        return publicKey;
    }
}

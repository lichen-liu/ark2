package app.user.internal;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.X509Identity;

import app.user.Signable;

public class SignableState extends IdentifiableState implements Signable {
    private final PrivateKey privateKey;

    public SignableState(final Wallet wallet, final Contract contract, final String userName)
            throws InvalidKeySpecException, IOException {
        super(wallet, contract, userName);
        final X509Identity adminIdentity = (X509Identity) wallet.get(userName);
        this.privateKey = adminIdentity.getPrivateKey();
    }

    public SignableState(final Contract contract, final PublicKey publicKey, final PrivateKey privateKey) {
        super(contract, publicKey);
        this.privateKey = privateKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }
}

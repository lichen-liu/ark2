package app.user.service;

import java.security.PublicKey;

import org.hyperledger.fabric.gateway.Contract;

import app.user.IdentityProvider;
import app.user.NamedReadable;

public class ReadonlyAppUser extends IdentityProvider implements NamedReadable {
    public ReadonlyAppUser(final Contract contract, final PublicKey publicKey) {
        super(contract, publicKey);
    }
}

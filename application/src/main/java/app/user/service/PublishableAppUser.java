package app.user.service;

import java.security.PrivateKey;
import java.security.PublicKey;

import org.hyperledger.fabric.gateway.Contract;

import app.user.NamedReadable;
import app.user.NamedWriteable;
import app.user.SignatureProvider;

public class PublishableAppUser extends SignatureProvider implements NamedWriteable, NamedReadable {
    public PublishableAppUser(final Contract contract, final PublicKey publicKey, final PrivateKey privateKey) {
        super(contract, publicKey, privateKey);
    }
}

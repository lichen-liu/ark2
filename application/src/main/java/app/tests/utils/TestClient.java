package app.tests.utils;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import org.hyperledger.fabric.gateway.Contract;

import app.user.NamedService;
import app.user.ServiceProvider;
import app.util.Cryptography;

public class TestClient {
    public static NamedService createTestClient(final Contract contract)
            throws InvalidAlgorithmParameterException, NoSuchAlgorithmException {
        final KeyPair pair = Cryptography.generateRandomKeyPair();
        final PrivateKey priv = pair.getPrivate();
        final PublicKey pub = pair.getPublic();
        return ServiceProvider.createNamedService(contract, pub, priv);
    }
}

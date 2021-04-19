package app.tests.utils;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import org.hyperledger.fabric.gateway.Contract;

import app.user.service.PublishableAppUser;
import app.utils.Cryptography;

public class TestClient {
    public static PublishableAppUser createTestClient(final Contract contract)
            throws InvalidAlgorithmParameterException, NoSuchAlgorithmException {
        final KeyPair pair = Cryptography.generateRandomKeyPair();
        final PrivateKey priv = pair.getPrivate();
        final PublicKey pub = pair.getPublic();
        return new PublishableAppUser(contract, pub, priv);
    }
}

package app.tests.utils;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;

import org.hyperledger.fabric.gateway.Contract;

import app.AppClient;

public class TestClient {
    public static AppClient createTestClient(final Contract contract)
            throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        final KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
        keyGen.initialize(new ECGenParameterSpec("secp256r1"), new SecureRandom());

        final KeyPair pair = keyGen.generateKeyPair();
        final PrivateKey priv = pair.getPrivate();
        final PublicKey pub = pair.getPublic();
        return new AppClient(contract, pub, priv);
    }
}

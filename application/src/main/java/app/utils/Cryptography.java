package app.utils;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class Cryptography {
    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    public static PrivateKey parsePrivateKey(final byte[] keyBin)
            throws InvalidKeySpecException, NoSuchAlgorithmException {
        final EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(keyBin);
        final KeyFactory kf = KeyFactory.getInstance("EC");
        final PrivateKey privateKey = kf.generatePrivate(privateKeySpec);
        return privateKey;
    }

    public static PublicKey parsePublicKey(final byte[] keyBin)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        final EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(keyBin);
        final KeyFactory kf = KeyFactory.getInstance("EC");
        final PublicKey pub = kf.generatePublic(publicKeySpec);
        return pub;
    }

    public static KeyPair generateRandomKeyPair() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException {
        final KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
        keyGen.initialize(new ECGenParameterSpec("secp256r1"), new SecureRandom());
        final KeyPair pair = keyGen.generateKeyPair();
        return pair;
    }
}
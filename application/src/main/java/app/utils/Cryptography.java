package app.utils;

import java.math.BigInteger;
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
import java.security.spec.ECPoint;
import java.security.spec.ECPrivateKeySpec;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.InvalidKeySpecException;

import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.ECPointUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;

public class Cryptography {

    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    public static PrivateKey parsePrivateKey(final byte[] keyBin)
            throws InvalidKeySpecException, NoSuchAlgorithmException {
        final ECNamedCurveParameterSpec spec = ECNamedCurveTable.getParameterSpec("secp256k1");
        final KeyFactory kf = KeyFactory.getInstance("ECDSA", new BouncyCastleProvider());
        final ECNamedCurveSpec params = new ECNamedCurveSpec("secp256k1", spec.getCurve(), spec.getG(), spec.getN());
        final ECPrivateKeySpec privKeySpec = new ECPrivateKeySpec(new BigInteger(keyBin), params);
        return kf.generatePrivate(privKeySpec);
    }

    public static PublicKey parsePublicKey(final byte[] keyBin)
            throws InvalidKeySpecException, NoSuchAlgorithmException {
        final ECNamedCurveParameterSpec spec = ECNamedCurveTable.getParameterSpec("secp256k1");
        final KeyFactory kf = KeyFactory.getInstance("ECDSA", new BouncyCastleProvider());
        final ECNamedCurveSpec params = new ECNamedCurveSpec("secp256k1", spec.getCurve(), spec.getG(), spec.getN());
        final ECPoint point = ECPointUtil.decodePoint(params.getCurve(), keyBin);
        final ECPublicKeySpec pubKeySpec = new ECPublicKeySpec(point, params);
        return kf.generatePublic(pubKeySpec);
    }

    public static KeyPair generateRandomKeyPair() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException {
        final KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
        keyGen.initialize(new ECGenParameterSpec("secp256r1"), new SecureRandom());
        final KeyPair pair = keyGen.generateKeyPair();
        return pair;
    }

}

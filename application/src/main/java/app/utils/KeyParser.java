package app.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.ECPoint;
import java.security.spec.ECPrivateKeySpec;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.ECPointUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

public class KeyParser {

    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    private PemObject pemObject;
    private final KeyFactory factory;

    public KeyParser(final String filename, final String algorithm, final String provider)
            throws IOException, NoSuchAlgorithmException, NoSuchProviderException {

        final PemReader pemReader = new PemReader(new InputStreamReader(new FileInputStream(filename)));
        try {
            this.pemObject = pemReader.readPemObject();
        } finally {
            pemReader.close();
        }

        this.factory = KeyFactory.getInstance(algorithm, provider);
    }

    public PublicKey getPublicKey() throws InvalidKeySpecException {
        final byte[] content = pemObject.getContent();
        final X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(content);
        return factory.generatePublic(pubKeySpec);
    }

    public PrivateKey getPrivateKey() throws InvalidKeySpecException {
        final byte[] content = pemObject.getContent();
        final PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(content);
        return factory.generatePrivate(privKeySpec);
    }

    public static PrivateKey generatePrivateKey(final byte[] keyBin)
            throws InvalidKeySpecException, NoSuchAlgorithmException {
        final ECNamedCurveParameterSpec spec = ECNamedCurveTable.getParameterSpec("secp256k1");
        final KeyFactory kf = KeyFactory.getInstance("ECDSA", new BouncyCastleProvider());
        final ECNamedCurveSpec params = new ECNamedCurveSpec("secp256k1", spec.getCurve(), spec.getG(), spec.getN());
        final ECPrivateKeySpec privKeySpec = new ECPrivateKeySpec(new BigInteger(keyBin), params);
        return kf.generatePrivate(privKeySpec);
    }

    public static PublicKey generatePublicKey(final byte[] keyBin)
            throws InvalidKeySpecException, NoSuchAlgorithmException {
        final ECNamedCurveParameterSpec spec = ECNamedCurveTable.getParameterSpec("secp256k1");
        final KeyFactory kf = KeyFactory.getInstance("ECDSA", new BouncyCastleProvider());
        final ECNamedCurveSpec params = new ECNamedCurveSpec("secp256k1", spec.getCurve(), spec.getG(), spec.getN());
        final ECPoint point = ECPointUtil.decodePoint(params.getCurve(), keyBin);
        final ECPublicKeySpec pubKeySpec = new ECPublicKeySpec(point, params);
        return kf.generatePublic(pubKeySpec);
    }
}

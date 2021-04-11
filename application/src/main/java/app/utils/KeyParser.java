package app.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

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
}

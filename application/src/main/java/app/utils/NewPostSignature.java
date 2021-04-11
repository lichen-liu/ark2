package app.utils;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

public class NewPostSignature {

    public static byte[] sign(PrivateKey privateKey, String content) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        assert privateKey.getAlgorithm() == "ECDSA" : "The private key is not in ECDSA format";

        Signature sig = Signature.getInstance("SHA256withECDSA");
        sig.initSign(privateKey);
        sig.update(content.getBytes());
        return sig.sign();
    }

    public static boolean verify(PublicKey publicKey, byte[] content, byte[] signature) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature sig2 = Signature.getInstance("SHA256withECDSA");
        sig2.initVerify(publicKey);
        sig2.update(content);
        return sig2.verify(signature);
    }

}
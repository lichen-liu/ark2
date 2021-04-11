package app;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.ContractException;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.X509Identity;

public class AppPeer {

    private final Contract contract;
    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    public AppPeer(final Wallet wallet, final Contract contract, final String userId)
            throws InvalidKeySpecException, IOException {
        this.contract = contract;

        final X509Identity adminIdentity = (X509Identity) wallet.get(userId);

        this.privateKey = adminIdentity.getPrivateKey();
        this.publicKey = adminIdentity.getCertificate().getPublicKey();
    }

    public String publishNewPost(final String content) throws ContractException, TimeoutException, InterruptedException,
            InvalidKeyException, NoSuchAlgorithmException, SignatureException {

        final var timestamp = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);

        // TODO: should be publicKey
        final var hash = getSHA(String.join("", timestamp, content, privateKey.toString()));

        assert privateKey.getAlgorithm() == "ECDSA" : "The private key is not in ECDSA format";

        final Signature sig = Signature.getInstance("SHA256withECDSA");
        sig.initSign(privateKey);
        sig.update(hash.getBytes());

        final var signature = sig.sign();

        final Signature sig2 = Signature.getInstance("SHA256withECDSA");
        sig2.initVerify(publicKey);
        sig2.update(hash.getBytes());
        assert sig2.verify(signature) : "Signature not correct";

        return new String(contract.submitTransaction("publishNewPost", timestamp, content,
                bytesToHexString(publicKey.getEncoded()), bytesToHexString(sig.sign())));
    }

    public Iterable<String> fetchUserPosts() {
        return new ArrayList<String>(0);
    }

    public String fetchAllPosts() throws ContractException {
        return new String(contract.evaluateTransaction("getAllPostKeys"));
    }

    public final Contract getContract() {
        return this.contract;
    }

    public static String getSHA(final String input) throws NoSuchAlgorithmException {
        final MessageDigest md = MessageDigest.getInstance("SHA-256");
        final var hash = md.digest(input.getBytes(StandardCharsets.UTF_8));

        return bytesToHexString(hash);
    }

    public static String bytesToHexString(final byte[] bytes) {
        final BigInteger number = new BigInteger(1, bytes);
        final StringBuilder hexString = new StringBuilder(number.toString(16));

        while (hexString.length() < 32) {
            hexString.insert(0, '0');
        }

        return hexString.toString();
    }
}
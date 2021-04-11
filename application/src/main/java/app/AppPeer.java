package app;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    private Contract contract;
    private PrivateKey privateKey;
    private PublicKey publicKey;

    public AppPeer(Wallet wallet, Contract contract, String userId) throws InvalidKeySpecException, IOException {
        this.contract = contract;

        X509Identity adminIdentity = (X509Identity) wallet.get(userId);

        this.privateKey = adminIdentity.getPrivateKey();
        this.publicKey = adminIdentity.getCertificate().getPublicKey();
    }

    public String publishNewPost(String content) throws ContractException, TimeoutException, InterruptedException, InvalidKeyException, NoSuchAlgorithmException, SignatureException{

        var timestamp = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);

        var hash = getSHA(String.join("", timestamp, content, privateKey.toString()));
        
        assert privateKey.getAlgorithm() == "ECDSA" : "The private key is not in ECDSA format";

        Signature sig = Signature.getInstance("SHA256withECDSA");
        sig.initSign(privateKey);
        sig.update(hash.getBytes());

        var signature = sig.sign();

        Signature sig2 = Signature.getInstance("SHA256withECDSA");
        sig2.initVerify(publicKey);
        sig2.update(hash.getBytes());
        assert sig2.verify(signature) : "Signature not correct";

        return new String(contract.submitTransaction("publishNewPost", timestamp, content, bytesToHexString(publicKey.getEncoded()), bytesToHexString(sig.sign())));        
    }

    public Iterable<String> fetchUserPosts() {
        return new ArrayList<String>(0);
    }

    public String fetchAllPosts() throws ContractException {
        return new String(contract.evaluateTransaction("getAllPostKeys"));
    }
    
    public final Contract getContract(){
        return this.contract;
    }

    public static String getSHA(String input) throws NoSuchAlgorithmException
    { 
        MessageDigest md = MessageDigest.getInstance("SHA-256"); 
        var hash = md.digest(input.getBytes(StandardCharsets.UTF_8)); 

        return bytesToHexString(hash);
    }

    public static String bytesToHexString(byte[] bytes)
    {
        BigInteger number = new BigInteger(1, bytes); 
        StringBuilder hexString = new StringBuilder(number.toString(16)); 
  
        while (hexString.length() < 32) 
        { 
            hexString.insert(0, '0'); 
        } 
  
        return hexString.toString(); 
    }
}
package app.utils;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ByteUtils {

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

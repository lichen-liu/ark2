package app.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class ByteUtils {

    public static byte[] getSHA(final String input) throws NoSuchAlgorithmException {
        final MessageDigest md = MessageDigest.getInstance("SHA-256");
        final var hash = md.digest(input.getBytes(StandardCharsets.UTF_8));

        return hash;
    }

    /**
     * Converts byte[] into ascii String that can be printed
     * 
     * For inversion:
     * 
     * <pre>
     * byte[] ByteUtils.fromAsciiString(String)
     * </pre>
     * 
     * @param bytes
     * @return Base64, URL and Filename safe, encoded ascii String
     */
    public static String toAsciiString(final byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    /**
     * Converts ascii String into byte[]
     * 
     * The ascii String must be generated via:
     * 
     * <pre>
     * String toAsciiString(byte[])
     * </pre>
     * 
     * @param asciiString, Base64, URL and Filename safe, encoded ascii String
     * @return bytes
     * @throws IllegalArgumentException
     */
    public static byte[] fromAsciiString(final String asciiString) throws IllegalArgumentException {
        return Base64.getUrlDecoder().decode(asciiString);
    }
}

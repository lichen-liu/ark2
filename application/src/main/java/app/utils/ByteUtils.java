package app.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.DatatypeConverter;

public class ByteUtils {

    public static byte[] getSHA(final String input) throws NoSuchAlgorithmException {
        final MessageDigest md = MessageDigest.getInstance("SHA-256");
        final var hash = md.digest(input.getBytes(StandardCharsets.UTF_8));

        return hash;
    }

    public static String toHexString(final byte[] bytes) {
        return DatatypeConverter.printHexBinary(bytes);
    }

    public static byte[] toByteArray(final String hexString) throws IllegalArgumentException {
        return DatatypeConverter.parseHexBinary(hexString);
    }
}

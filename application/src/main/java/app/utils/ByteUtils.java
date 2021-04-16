package app.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.DatatypeConverter;

public class ByteUtils {

    public static String getSHA(final String input) throws NoSuchAlgorithmException {
        final MessageDigest md = MessageDigest.getInstance("SHA-256");
        final var hash = md.digest(input.getBytes(StandardCharsets.UTF_8));

        return toHexString(hash);
    }

    public static String toHexString(final byte[] array) {
        return DatatypeConverter.printHexBinary(array);
    }

    public static byte[] toByteArray(final String s) {
        return DatatypeConverter.parseHexBinary(s);
    }
}

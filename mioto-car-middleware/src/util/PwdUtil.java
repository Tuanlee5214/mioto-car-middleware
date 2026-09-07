/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;

/**
 *
 * @author tuanlee
 */
public class PwdUtil {
    private static final SecureRandom RANDOM = new SecureRandom();

    private PwdUtil() { }

    public static String newSalt() {
        byte[] b = new byte[16];
        RANDOM.nextBytes(b);
        return toHex(b);
    }

    public static String hash(String pwd, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes(StandardCharsets.UTF_8));
            md.update(pwd.getBytes(StandardCharsets.UTF_8));
            return toHex(md.digest());
        } catch (Exception ex) {
            throw new IllegalStateException("SHA-256 unavailable", ex);
        }
    }
    
    public static boolean matches(String pwd, String salt, String expectedHash) {
        String actual = hash(pwd, salt);
        if (actual.length() != expectedHash.length()) {
            return false;
        }
        int diff = 0;
        for (int i = 0; i < actual.length(); ++i) {
            diff |= actual.charAt(i) ^ expectedHash.charAt(i);   // never short-circuits
        }
        return diff == 0;
    }

    public static long newSessionId() {
        long id = RANDOM.nextLong();
        if (id == 0) {
            return 1;
        }
        return id < 0 ? -id : id;
    }

    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (int i = 0; i < bytes.length; ++i) {
            String h = Integer.toHexString(bytes[i] & 0xFF);
            if (h.length() == 1) {
                sb.append('0');
            }
            sb.append(h);
        }
        return sb.toString();
    }
}

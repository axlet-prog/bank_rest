package com.example.bankcards.util.converter;

import java.security.Key;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class ConverterUtil {

    private static final String ALGORITHM = "AES";

    public static Cipher initCipher(String secretKey, String algorithm, int opmode) throws Exception {
        try {
            byte[] secretKeyBytes = Base64.getDecoder().decode(secretKey);
            Key key = new SecretKeySpec(secretKeyBytes, algorithm);
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(opmode, key);
            return cipher;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Cipher initCipher(String secretKey, int opmode) throws Exception {
        return initCipher(secretKey, ALGORITHM, opmode);
    }
}

package com.drobucs.histology.users.encryption;

import javax.crypto.KeyGenerator;
import java.security.NoSuchAlgorithmException;

public class EncryptionInfo {
    public static final String SALT_PREFIX = "sjdf=)?1f455";
    public static final String SALT_SUFFIX = "92342popasd";

    public static String salt(String str) {
        return SALT_PREFIX + str + SALT_SUFFIX;
    }


    private final String algorithm;
    private final KeyGenerator keyGenerator;
    private static EncryptionInfo encryptionInfo = null;

    public static synchronized EncryptionInfo getInstance() throws NoSuchAlgorithmException {
        if (encryptionInfo == null) {
            return new EncryptionInfo("AES");
        }
        return encryptionInfo;
    }

    private EncryptionInfo(String algorithm) throws NoSuchAlgorithmException {
        this.algorithm = algorithm;
        keyGenerator = KeyGenerator.getInstance(algorithm);
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public KeyGenerator getKeyGenerator() {
        return keyGenerator;
    }
}

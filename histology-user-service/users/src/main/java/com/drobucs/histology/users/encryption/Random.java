package com.drobucs.histology.users.encryption;

import java.security.SecureRandom;

public class Random {
    private static SecureRandom secureRandom = null;
    public static synchronized SecureRandom getRandom() {
        if (secureRandom == null) {
            secureRandom = new SecureRandom();
            secureRandom.setSeed(42);
            return secureRandom;
        }
        return secureRandom;
    }
    private Random(){}
}

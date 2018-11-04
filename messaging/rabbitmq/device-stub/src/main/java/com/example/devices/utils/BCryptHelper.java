package com.example.devices.utils;

import org.mindrot.jbcrypt.BCrypt;

public class BCryptHelper {

    public static String hash(String password, int logRounds)
    {
        return BCrypt.hashpw(password, BCrypt.gensalt(logRounds));
    }

    public static String hash(String password)
    {
        return hash(password, 10);
    }

    public boolean verifyHash(String password, String hash) {
        return BCrypt.checkpw(password, hash);
    }

}

package com.absence.auth.utilities;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

public class OtpGeneratorUtil {

    private static Random random;

    private static final Logger logger = LogManager.getLogger(OtpGeneratorUtil.class);

    static {
        try {
            random = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            logger.log(Level.INFO, e.getLocalizedMessage());
        }
    }

    public static String getRandomSixNumber() {
        int number = random.nextInt(999999);
        return String.format("%06d", number);
    }
}

package com.absence.auth.utilities;

import org.apache.logging.log4j.Level;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.Date;
import java.util.Random;

public class EmployeeNumberGeneratorUtil {

    private static Random random;

    static {
        try {
            random = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static String generate() {
        LocalDate localDate = LocalDate.now();
        int number = random.nextInt(9999);
        return "79" + localDate.getMonthValue() + localDate.getYear() + String.format("%04d", number);
    }

}

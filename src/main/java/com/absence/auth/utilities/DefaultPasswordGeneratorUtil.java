package com.absence.auth.utilities;

import java.security.SecureRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DefaultPasswordGeneratorUtil {

    public static String generateRandomPassword()
    {
        final String uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final String lowercase = "abcdefghijklmnopqrstuvwxyz";
        final String numbers = "0123456789";

        SecureRandom random = new SecureRandom();

        StringBuilder password = new StringBuilder();
        password.append(IntStream.range(0, 2)
                .map(i -> random.nextInt(uppercase.length()))
                .mapToObj(randomIndex -> String.valueOf(uppercase.charAt(randomIndex)))
                .collect(Collectors.joining()));

        password.append(IntStream.range(0, 4)
                .map(i -> random.nextInt(lowercase.length()))
                .mapToObj(randomIndex -> String.valueOf(lowercase.charAt(randomIndex)))
                .collect(Collectors.joining()));

        password.append(IntStream.range(0, 2)
                .map(i -> random.nextInt(numbers.length()))
                .mapToObj(randomIndex -> String.valueOf(numbers.charAt(randomIndex)))
                .collect(Collectors.joining()));

        password.append("#");

        return password.toString();
    }

}

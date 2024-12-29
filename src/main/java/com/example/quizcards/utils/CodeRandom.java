package com.example.quizcards.utils;

import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class CodeRandom {
    private static final Random random = new Random();

    public static String generateRandomCode(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();

        for (int i = 0; i < length; i++) {
            code.append(characters.charAt(random.nextInt(characters.length())));
        }
        return code.toString();
    }
}

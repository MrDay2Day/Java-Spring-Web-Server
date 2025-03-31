package org.file.utils;

import org.mindrot.jbcrypt.BCrypt;

public class BcryptHashing {
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    public static boolean checkPassword(String candidatePassword, String hashedPassword) {
        try {
            return BCrypt.checkpw(candidatePassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid BCrypt hash: " + e.getMessage());
            return false; // Or handle legacy hashes if needed
        }
    }
}
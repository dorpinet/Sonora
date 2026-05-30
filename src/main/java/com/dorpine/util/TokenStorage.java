package com.dorpine.util;

import java.io.*;
import java.nio.file.*;
import java.util.Properties;

public class TokenStorage {
    private static final Path FILE = Path.of(System.getProperty("user.home"), ".sonora", "session.properties");

    public static void save(String accessToken, String refreshToken, String userJson) {
        try {
            Files.createDirectories(FILE.getParent());
            Properties p = new Properties();
            if (accessToken != null) p.setProperty("accessToken", accessToken);
            if (refreshToken != null) p.setProperty("refreshToken", refreshToken);
            if (userJson != null) p.setProperty("user", userJson);
            try (OutputStream out = Files.newOutputStream(FILE)) {
                p.store(out, "SONORA Session");
            }
        } catch (Exception e) {
            System.err.println("[TokenStorage] Save error: " + e.getMessage());
        }
    }

    public static Properties load() {
        try {
            if (!Files.exists(FILE)) return null;
            Properties p = new Properties();
            try (InputStream in = Files.newInputStream(FILE)) {
                p.load(in);
            }
            return p;
        } catch (Exception e) {
            System.err.println("[TokenStorage] Load error: " + e.getMessage());
            return null;
        }
    }

    public static void clear() {
        try {
            Files.deleteIfExists(FILE);
        } catch (Exception e) {
            System.err.println("[TokenStorage] Clear error: " + e.getMessage());
        }
    }
}

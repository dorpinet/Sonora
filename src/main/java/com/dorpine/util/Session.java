package com.dorpine.util;

import com.dorpine.model.UserProfile;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Session {
    private static String accessToken;
    private static String refreshToken;
    private static UserProfile currentUser;
    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        loadFromStorage();
    }

    private static void loadFromStorage() {
        java.util.Properties p = TokenStorage.load();
        if (p != null) {
            accessToken = p.getProperty("accessToken");
            refreshToken = p.getProperty("refreshToken");
            String userJson = p.getProperty("user");
            if (userJson != null) {
                try {
                    currentUser = MAPPER.readValue(userJson, UserProfile.class);
                } catch (Exception e) {
                    System.err.println("[Session] Failed to load user: " + e.getMessage());
                }
            }
        }
    }

    public static boolean isAuthenticated() {
        return accessToken != null && !accessToken.isEmpty();
    }

    public static String getAccessToken() { return accessToken; }
    public static void setAccessToken(String token) {
        accessToken = token;
        save();
    }

    public static String getRefreshToken() { return refreshToken; }
    public static void setRefreshToken(String token) {
        refreshToken = token;
        save();
    }

    public static UserProfile getCurrentUser() { return currentUser; }
    public static void setCurrentUser(UserProfile user) {
        currentUser = user;
        save();
    }

    public static void clear() {
        accessToken = null;
        refreshToken = null;
        currentUser = null;
        TokenStorage.clear();
    }

    private static void save() {
        String userJson = null;
        if (currentUser != null) {
            try {
                userJson = MAPPER.writeValueAsString(currentUser);
            } catch (Exception e) {
                System.err.println("[Session] Failed to save user: " + e.getMessage());
            }
        }
        TokenStorage.save(accessToken, refreshToken, userJson);
    }

    private Session() {}
}

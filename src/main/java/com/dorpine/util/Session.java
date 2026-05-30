package com.dorpine.util;

import com.dorpine.model.UserProfile;

public class Session {
    private static String accessToken;
    private static String refreshToken;
    private static UserProfile currentUser;

    public static boolean isAuthenticated() {
        return accessToken != null && !accessToken.isEmpty();
    }

    public static String getAccessToken() { return accessToken; }
    public static void setAccessToken(String token) { accessToken = token; }

    public static String getRefreshToken() { return refreshToken; }
    public static void setRefreshToken(String token) { refreshToken = token; }

    public static UserProfile getCurrentUser() { return currentUser; }
    public static void setCurrentUser(UserProfile user) { currentUser = user; }

    public static void clear() {
        accessToken = null;
        refreshToken = null;
        currentUser = null;
    }

    private Session() {}
}

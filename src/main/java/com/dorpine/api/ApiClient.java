package com.dorpine.api;

import com.dorpine.model.Note;
import com.dorpine.model.Playlist;
import com.dorpine.model.Track;
import com.dorpine.model.UserProfile;
import com.dorpine.util.Session;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ApiClient {
    private static final String BASE_URL = System.getProperty("api.url", "https://sonora-rdg0.onrender.com/api");
    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(15))
            .build();
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private ApiClient() {}

    // ========== Auth ==========

    private static String extractError(String body) {
        String trimmed = body.trim();
        if (trimmed.isEmpty()) return "Empty response";
        if (trimmed.startsWith("<")) {
            return "Server returned HTML (code " + trimmed.substring(0, Math.min(60, trimmed.length())) + "...)";
        }
        try {
            JsonNode root = MAPPER.readTree(trimmed);
            return root.has("error") ? root.get("error").asText() : "Request failed";
        } catch (Exception e) {
            return trimmed.substring(0, Math.min(120, trimmed.length()));
        }
    }

    public static boolean wakeUp() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/health"))
                    .header("Accept", "application/json")
                    .timeout(Duration.ofSeconds(30))
                    .GET()
                    .build();
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (Exception e) {
            System.err.println("[API] Wake-up failed: " + e.getMessage());
            return false;
        }
    }

    public static AuthResult login(String email, String password) {
        try {
            String json = MAPPER.writeValueAsString(new java.util.HashMap<String, String>() {{
                put("email", email); put("password", password);
            }});
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/auth/login"))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .timeout(Duration.ofSeconds(30))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("[API] Login HTTP " + response.statusCode());
            if (response.statusCode() == 200) {
                JsonNode root = MAPPER.readTree(response.body());
                JsonNode tokens = root.get("tokens");
                JsonNode user = root.get("user");
                if (tokens != null && user != null) {
                    Session.setAccessToken(tokens.get("accessToken").asText());
                    Session.setRefreshToken(tokens.get("refreshToken").asText());
                    UserProfile profile = MAPPER.treeToValue(user, UserProfile.class);
                    Session.setCurrentUser(profile);
                    return new AuthResult(true, null);
                }
            } else {
                return new AuthResult(false, extractError(response.body()));
            }
        } catch (Exception e) {
            System.err.println("[API] Login error: " + e.getMessage());
            return new AuthResult(false, "Network error: " + e.getMessage());
        }
        return new AuthResult(false, "Unknown error");
    }

    public static AuthResult registerInit(String email) {
        try {
            String json = MAPPER.writeValueAsString(new java.util.HashMap<String, String>() {{
                put("email", email);
            }});
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/auth/register-init"))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .timeout(Duration.ofSeconds(30))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("[API] Register init HTTP " + response.statusCode());
            if (response.statusCode() == 200) {
                return new AuthResult(true, null);
            } else {
                return new AuthResult(false, extractError(response.body()));
            }
        } catch (Exception e) {
            System.err.println("[API] Register init error: " + e.getMessage());
            return new AuthResult(false, "Network error: " + e.getMessage());
        }
    }

    public static AuthResult registerVerify(String email, String code, String username, String password, String confirmPassword) {
        try {
            String json = MAPPER.writeValueAsString(new java.util.HashMap<String, String>() {{
                put("email", email); put("code", code); put("username", username);
                put("password", password); put("confirmPassword", confirmPassword);
            }});
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/auth/register-verify"))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .timeout(Duration.ofSeconds(30))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("[API] Register verify HTTP " + response.statusCode());
            if (response.statusCode() == 201) {
                JsonNode root = MAPPER.readTree(response.body());
                JsonNode tokens = root.get("tokens");
                JsonNode user = root.get("user");
                if (tokens != null && user != null) {
                    Session.setAccessToken(tokens.get("accessToken").asText());
                    Session.setRefreshToken(tokens.get("refreshToken").asText());
                    UserProfile profile = MAPPER.treeToValue(user, UserProfile.class);
                    Session.setCurrentUser(profile);
                    return new AuthResult(true, null);
                }
            } else {
                return new AuthResult(false, extractError(response.body()));
            }
        } catch (Exception e) {
            System.err.println("[API] Register verify error: " + e.getMessage());
            return new AuthResult(false, "Network error: " + e.getMessage());
        }
        return new AuthResult(false, "Unknown error");
    }

    public static AuthResult forgotPassword(String email) {
        try {
            String json = MAPPER.writeValueAsString(new java.util.HashMap<String, String>() {{
                put("email", email);
            }});
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/auth/forgot-password"))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .timeout(Duration.ofSeconds(30))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("[API] Forgot password HTTP " + response.statusCode());
            if (response.statusCode() == 200) {
                return new AuthResult(true, null);
            } else {
                return new AuthResult(false, extractError(response.body()));
            }
        } catch (Exception e) {
            System.err.println("[API] Forgot password error: " + e.getMessage());
            return new AuthResult(false, "Network error: " + e.getMessage());
        }
    }

    public static AuthResult logout() {
        try {
            String token = Session.getAccessToken();
            if (token == null) return new AuthResult(true, null);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/auth/logout"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .timeout(Duration.ofSeconds(30))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();
            CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            return new AuthResult(true, null);
        } catch (Exception e) {
            return new AuthResult(true, null);
        } finally {
            Session.clear();
        }
    }

    public static AuthResult resetPassword(String email, String code, String password, String confirmPassword) {
        try {
            String json = MAPPER.writeValueAsString(new java.util.HashMap<String, String>() {{
                put("email", email); put("code", code);
                put("password", password); put("confirmPassword", confirmPassword);
            }});
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/auth/reset-password"))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .timeout(Duration.ofSeconds(30))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("[API] Reset password HTTP " + response.statusCode());
            if (response.statusCode() == 200) {
                return new AuthResult(true, null);
            } else {
                return new AuthResult(false, extractError(response.body()));
            }
        } catch (Exception e) {
            System.err.println("[API] Reset password error: " + e.getMessage());
            return new AuthResult(false, "Network error: " + e.getMessage());
        }
    }

    // ========== Data ==========

    public static List<Track> getTracks(String genre) {
        try {
            String url = BASE_URL + "/tracks" + (genre != null && !genre.isEmpty() ? "?genre=" + URLEncoder.encode(genre, StandardCharsets.UTF_8) : "");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Accept", "application/json")
                    .timeout(Duration.ofSeconds(30))
                    .GET()
                    .build();
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JsonNode root = MAPPER.readTree(response.body());
                JsonNode tracksNode = root.get("tracks");
                if (tracksNode != null && tracksNode.isArray()) {
                    List<Track> tracks = new ArrayList<>();
                    for (JsonNode node : tracksNode) {
                        try {
                            tracks.add(MAPPER.treeToValue(node, Track.class));
                        } catch (Exception ex) {
                            System.err.println("[API] Parse track error: " + ex.getMessage());
                        }
                    }
                    return tracks;
                }
            } else {
                System.err.println("[API] Tracks HTTP " + response.statusCode());
            }
        } catch (Exception e) {
            System.err.println("[API] Fetch tracks error: " + e.getMessage());
        }
        return Collections.emptyList();
    }

    public static List<Note> getNotes() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/notes"))
                    .header("Accept", "application/json")
                    .timeout(Duration.ofSeconds(30))
                    .GET()
                    .build();
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JsonNode root = MAPPER.readTree(response.body());
                JsonNode notesNode = root.get("notes");
                if (notesNode != null && notesNode.isArray()) {
                    List<Note> notes = new ArrayList<>();
                    for (JsonNode node : notesNode) {
                        try {
                            notes.add(MAPPER.treeToValue(node, Note.class));
                        } catch (Exception ex) {
                            System.err.println("[API] Parse note error: " + ex.getMessage());
                        }
                    }
                    return notes;
                }
            } else {
                System.err.println("[API] Notes HTTP " + response.statusCode());
            }
        } catch (Exception e) {
            System.err.println("[API] Fetch notes error: " + e.getMessage());
        }
        return Collections.emptyList();
    }

    public static List<String> getGenres() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/tracks/genres"))
                    .header("Accept", "application/json")
                    .timeout(Duration.ofSeconds(30))
                    .GET()
                    .build();
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JsonNode root = MAPPER.readTree(response.body());
                JsonNode genresNode = root.get("genres");
                if (genresNode != null && genresNode.isArray()) {
                    List<String> genres = new ArrayList<>();
                    for (JsonNode g : genresNode) genres.add(g.asText());
                    return genres;
                }
            } else {
                System.err.println("[API] Genres HTTP " + response.statusCode());
            }
        } catch (Exception e) {
            System.err.println("[API] Fetch genres error: " + e.getMessage());
        }
        return Collections.emptyList();
    }

    public static String getPreviewUrl(String spotifyId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/spotify/preview/" + spotifyId))
                    .header("Accept", "application/json")
                    .timeout(Duration.ofSeconds(30))
                    .GET()
                    .build();
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JsonNode root = MAPPER.readTree(response.body());
                JsonNode previewUrlNode = root.get("previewUrl");
                if (previewUrlNode != null && !previewUrlNode.isNull()) {
                    return previewUrlNode.asText();
                }
            }
        } catch (Exception e) {
            System.err.println("[API] Preview error: " + e.getMessage());
        }
        return null;
    }

    public static class AuthResult {
        public final boolean success;
        public final String error;
        public AuthResult(boolean success, String error) {
            this.success = success;
            this.error = error;
        }
    }
}

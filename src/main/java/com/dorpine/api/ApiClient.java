package com.dorpine.api;

import com.dorpine.model.Note;
import com.dorpine.model.Playlist;
import com.dorpine.model.Track;
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
}

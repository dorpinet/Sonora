package com.dorpine.api;

import com.dorpine.model.Note;
import com.dorpine.model.Playlist;
import com.dorpine.model.Track;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ApiClient {
    private static final String BASE_URL = System.getProperty("api.url", "https://sonora-rdg0.onrender.com/api");
    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private ApiClient() {}

    public static List<Track> getTracks(String genre) {
        try {
            String url = BASE_URL + "/tracks";
            if (genre != null && !genre.isEmpty()) {
                url += "?genre=" + genre;
            }
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Accept", "application/json")
                    .GET()
                    .build();
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JsonNode root = MAPPER.readTree(response.body());
                JsonNode tracksNode = root.get("tracks");
                if (tracksNode != null && tracksNode.isArray()) {
                    List<Track> tracks = new ArrayList<>();
                    for (JsonNode node : tracksNode) {
                        tracks.add(MAPPER.treeToValue(node, Track.class));
                    }
                    return tracks;
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch tracks: " + e.getMessage());
        }
        return Collections.emptyList();
    }

    public static List<Note> getNotes() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/notes"))
                    .header("Accept", "application/json")
                    .GET()
                    .build();
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JsonNode root = MAPPER.readTree(response.body());
                JsonNode notesNode = root.get("notes");
                if (notesNode != null && notesNode.isArray()) {
                    List<Note> notes = new ArrayList<>();
                    for (JsonNode node : notesNode) {
                        notes.add(MAPPER.treeToValue(node, Note.class));
                    }
                    return notes;
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch notes: " + e.getMessage());
        }
        return Collections.emptyList();
    }

    public static List<String> getGenres() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/tracks/genres"))
                    .header("Accept", "application/json")
                    .GET()
                    .build();
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JsonNode root = MAPPER.readTree(response.body());
                JsonNode genresNode = root.get("genres");
                if (genresNode != null && genresNode.isArray()) {
                    List<String> genres = new ArrayList<>();
                    for (JsonNode g : genresNode) {
                        genres.add(g.asText());
                    }
                    return genres;
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch genres: " + e.getMessage());
        }
        return Collections.emptyList();
    }

    public static List<Playlist> getPlaylists() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/playlists"))
                    .header("Accept", "application/json")
                    .GET()
                    .build();
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JsonNode root = MAPPER.readTree(response.body());
                JsonNode playlistsNode = root.get("playlists");
                if (playlistsNode != null && playlistsNode.isArray()) {
                    List<Playlist> playlists = new ArrayList<>();
                    for (JsonNode node : playlistsNode) {
                        playlists.add(MAPPER.treeToValue(node, Playlist.class));
                    }
                    return playlists;
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch playlists: " + e.getMessage());
        }
        return Collections.emptyList();
    }

    public static String getPreviewUrl(String spotifyId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/spotify/preview/" + spotifyId))
                    .header("Accept", "application/json")
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
            System.err.println("Failed to fetch preview: " + e.getMessage());
        }
        return null;
    }

    public static boolean isHealthy() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL.replace("/api", "") + "/health"))
                    .GET()
                    .build();
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }
}

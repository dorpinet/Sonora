package com.dorpine.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Playlist {
    @com.fasterxml.jackson.annotation.JsonProperty("_id")
    private String id;
    private String name;
    private String description;
    private String type;
    private String genre;
    @JsonProperty("tracks")
    private List<PlaylistTrack> tracks;
    @JsonProperty("coverUrl")
    private String coverUrl;
    @JsonProperty("isPublic")
    private boolean isPublic;
    @JsonProperty("createdAt")
    private String createdAt;

    public Playlist() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public List<PlaylistTrack> getTracks() { return tracks; }
    public void setTracks(List<PlaylistTrack> tracks) { this.tracks = tracks; }

    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }

    public boolean isPublic() { return isPublic; }
    public void setPublic(boolean isPublic) { this.isPublic = isPublic; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PlaylistTrack {
        @JsonProperty("trackId")
        private String trackId;
        private String title;
        private String artist;
        @JsonProperty("coverUrl")
        private String coverUrl;

        public String getTrackId() { return trackId; }
        public void setTrackId(String trackId) { this.trackId = trackId; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getArtist() { return artist; }
        public void setArtist(String artist) { this.artist = artist; }

        public String getCoverUrl() { return coverUrl; }
        public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
    }
}

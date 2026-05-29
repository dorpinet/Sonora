package com.dorpine.ui.screens;

import com.dorpine.api.ApiClient;
import com.dorpine.model.Note;
import com.dorpine.model.Playlist;
import com.dorpine.model.Track;
import com.dorpine.ui.components.HorizontalCarousel;
import com.dorpine.ui.components.TopBar;
import com.dorpine.ui.components.TrackCard;
import javafx.application.Platform;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class HomeScreen extends VBox {
    private final Consumer<String> navHandler;
    private final Consumer<Object> detailsHandler;
    private final Runnable toggleTheme;
    private final VBox sectionsBox;

    public HomeScreen(Consumer<String> navHandler, Consumer<Object> detailsHandler, Runnable toggleTheme) {
        this.navHandler = navHandler;
        this.detailsHandler = detailsHandler;
        this.toggleTheme = toggleTheme;
        setSpacing(0);
        setFillWidth(true);

        sectionsBox = new VBox(28);
        sectionsBox.setFillWidth(true);

        sectionsBox.getChildren().add(new TopBar(s -> {
            if (!s.equals("home")) navHandler.accept(s);
        }, toggleTheme));

        getChildren().add(sectionsBox);
        loadSections();
    }

    private void loadSections() {
        new Thread(() -> {
            List<Note> notes = ApiClient.getNotes();
            List<String> genres = ApiClient.getGenres();

            List<Playlist> stubPlaylists = new ArrayList<>();
            Playlist liked = new Playlist(); liked.setName("Liked"); liked.setDescription("Your favorite tracks"); stubPlaylists.add(liked);
            Playlist recent = new Playlist(); recent.setName("Recently Played"); recent.setDescription("Tracks you listened to"); stubPlaylists.add(recent);
            Playlist favs = new Playlist(); favs.setName("For Your Favs"); favs.setDescription("Meditation vibes"); stubPlaylists.add(favs);

            Platform.runLater(() -> {
                sectionsBox.getChildren().clear();
                sectionsBox.getChildren().add(new TopBar(s -> {
                    if (!s.equals("home")) navHandler.accept(s);
                }, toggleTheme));

                HorizontalCarousel notesSection = new HorizontalCarousel("Sheet notes", new ArrayList<>(), 3);
                sectionsBox.getChildren().add(notesSection);
                if (!notes.isEmpty()) {
                    List<TrackCard> noteCards = new ArrayList<>();
                    for (Note n : notes) noteCards.add(new TrackCard(n, item -> detailsHandler.accept(item)));
                    notesSection.setCards(noteCards);
                }

                HorizontalCarousel playlistsSection = new HorizontalCarousel("My playlists", new ArrayList<>(), 3);
                sectionsBox.getChildren().add(playlistsSection);
                List<TrackCard> playlistCards = new ArrayList<>();
                for (Playlist p : stubPlaylists) {
                    playlistCards.add(new TrackCard(p, item -> {
                        if (item instanceof Playlist pl) navHandler.accept("playlist:" + pl.getName());
                    }));
                }
                playlistsSection.setCards(playlistCards);

                for (String genre : genres) {
                    HorizontalCarousel gs = new HorizontalCarousel(genre, new ArrayList<>(), 4);
                    sectionsBox.getChildren().add(gs);
                    String g = genre;
                    new Thread(() -> {
                        List<Track> tracks = ApiClient.getTracks(g);
                        Platform.runLater(() -> {
                            List<TrackCard> cards = new ArrayList<>();
                            for (Track t : tracks) cards.add(new TrackCard(t, item -> detailsHandler.accept(item)));
                            gs.setCards(cards);
                        });
                    }).start();
                }
            });
        }).start();
    }
}

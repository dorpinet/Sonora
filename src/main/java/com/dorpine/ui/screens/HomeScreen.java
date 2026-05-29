package com.dorpine.ui.screens;

import com.dorpine.api.ApiClient;
import com.dorpine.model.Note;
import com.dorpine.model.Playlist;
import com.dorpine.model.Track;
import com.dorpine.ui.components.HorizontalCarousel;
import com.dorpine.ui.components.TopBar;
import com.dorpine.ui.components.TrackCard;
import com.dorpine.util.Theme;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
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
        setPadding(new Insets(0, 0, 40, 0));

        sectionsBox = new VBox(40);
        sectionsBox.setFillWidth(true);
        sectionsBox.setPadding(new Insets(0, 24, 0, 24));

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

                addSectionTitle("Sheet notes");
                HorizontalCarousel notesSection = new HorizontalCarousel("", new ArrayList<>(), 3);
                sectionsBox.getChildren().add(notesSection);
                if (!notes.isEmpty()) {
                    List<TrackCard> noteCards = new ArrayList<>();
                    for (Note n : notes) noteCards.add(new TrackCard(n, item -> detailsHandler.accept(item)));
                    notesSection.setCards(noteCards);
                }

                addSectionTitle("My playlists");
                HBox playlistsBox = new HBox(20);
                playlistsBox.setAlignment(Pos.CENTER);
                for (Playlist p : stubPlaylists) {
                    playlistsBox.getChildren().add(new TrackCard(p, item -> {
                        if (item instanceof Playlist pl) navHandler.accept("playlist:" + pl.getName());
                    }));
                }
                sectionsBox.getChildren().add(playlistsBox);

                for (String genre : genres) {
                    addSectionTitle(genre);
                    HorizontalCarousel gs = new HorizontalCarousel("", new ArrayList<>(), 4);
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

    private void addSectionTitle(String text) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-text-fill: " + Theme.toCss(Theme.textPrimary()) + "; -fx-font-size: 24px; -fx-font-weight: bold;");
        lbl.setPadding(new Insets(8, 0, 0, 0));
        sectionsBox.getChildren().add(lbl);
    }
}

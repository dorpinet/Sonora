package com.dorpine.ui.screens;

import com.dorpine.api.ApiClient;
import com.dorpine.model.Note;
import com.dorpine.model.Playlist;
import com.dorpine.model.Track;
import com.dorpine.ui.components.HorizontalCarousel;
import com.dorpine.ui.components.TrackCard;
import com.dorpine.util.Fonts;
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

    public HomeScreen(Consumer<String> navHandler, Consumer<Object> detailsHandler) {
        this.navHandler = navHandler;
        this.detailsHandler = detailsHandler;
        setSpacing(0);
        setFillWidth(true);
        setPadding(new Insets(0, 0, 40, 0));
        setAlignment(Pos.TOP_CENTER);
        loadSections();
    }

    private void loadSections() {
        new Thread(() -> {
            List<Note> notes = ApiClient.getNotes();
            List<String> genres = ApiClient.getGenres();
            Playlist daily = ApiClient.getDaily();

            Platform.runLater(() -> {
                // 1. Sheet notes (always first)
                addSectionTitle("Sheet notes");
                HorizontalCarousel notesSection = new HorizontalCarousel("", new ArrayList<>(), 5);
                getChildren().add(notesSection);
                if (!notes.isEmpty()) {
                    List<TrackCard> noteCards = new ArrayList<>();
                    for (Note n : notes) noteCards.add(new TrackCard(n, item -> detailsHandler.accept(item)));
                    notesSection.setCards(noteCards);
                }

                // 2. My playlists (Liked, Recently Played, Daily Mix)
                addSectionTitle("My playlists");
                HBox playlistsBox = new HBox(16);
                playlistsBox.setAlignment(Pos.CENTER);

                Playlist liked = new Playlist();
                liked.setName("Liked");
                liked.setDescription("Explore new tracks!");
                playlistsBox.getChildren().add(new TrackCard(liked, item -> {
                    if (item instanceof Playlist pl) navHandler.accept("playlist:" + pl.getName());
                }));

                Playlist recent = new Playlist();
                recent.setName("Recently Played");
                recent.setDescription("Explore new tracks!");
                playlistsBox.getChildren().add(new TrackCard(recent, item -> {
                    if (item instanceof Playlist pl) navHandler.accept("playlist:" + pl.getName());
                }));

                if (daily != null) {
                    playlistsBox.getChildren().add(new TrackCard(daily, item -> {
                        if (item instanceof Playlist pl) navHandler.accept("playlist:" + pl.getName());
                    }));
                }

                getChildren().add(playlistsBox);

                // 3. Genres
                for (String genre : genres) {
                    addSectionTitle(genre);
                    HorizontalCarousel gs = new HorizontalCarousel("", new ArrayList<>(), 5);
                    getChildren().add(gs);
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
        lbl.setFont(Fonts.heading(22));
        lbl.setStyle("-fx-text-fill: " + Theme.toCss(Theme.textPrimary()) + ";");
        lbl.setPadding(new Insets(16, 0, 4, 0));
        lbl.setAlignment(Pos.CENTER);
        lbl.setMaxWidth(Double.MAX_VALUE);
        getChildren().add(lbl);
    }
}

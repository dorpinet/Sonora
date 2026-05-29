package com.dorpine.ui.screens;

import com.dorpine.api.ApiClient;
import com.dorpine.model.Note;
import com.dorpine.model.Playlist;
import com.dorpine.model.Track;
import com.dorpine.ui.components.DetailsPanel;
import com.dorpine.ui.components.HorizontalCarousel;
import com.dorpine.ui.components.TopBar;
import com.dorpine.ui.components.TrackCard;
import com.dorpine.util.Theme;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class HomeScreen extends BorderPane {
    private final Consumer<String> navigationHandler;
    private final Consumer<Object> detailsHandler;
    private VBox contentBox;
    private HorizontalCarousel notesCarousel;
    private HorizontalCarousel playlistsCarousel;
    private HorizontalCarousel drainCarousel;
    private HorizontalCarousel rageCarousel;

    public HomeScreen(Consumer<String> navigationHandler, Consumer<Object> detailsHandler) {
        this.navigationHandler = navigationHandler;
        this.detailsHandler = detailsHandler;
        build();
        loadData();
    }

    private void build() {
        setStyle("-fx-background-color: " + toHex(Theme.BACKGROUND_START) + ";");

        TopBar topBar = new TopBar(screen -> {
            if (!screen.equals("home")) {
                navigationHandler.accept(screen);
            }
        });
        setTop(topBar);
        BorderPane.setMargin(topBar, new Insets(16, 24, 8, 24));

        contentBox = new VBox(24);
        contentBox.setAlignment(Pos.TOP_CENTER);
        contentBox.setPadding(new Insets(16, 0, 24, 0));
        contentBox.setFillWidth(true);

        notesCarousel = new HorizontalCarousel("Sheet notes", new ArrayList<>(), 3);
        playlistsCarousel = new HorizontalCarousel("My playlists", new ArrayList<>(), 3);
        drainCarousel = new HorizontalCarousel("Drain", new ArrayList<>(), 4);
        rageCarousel = new HorizontalCarousel("Rage", new ArrayList<>(), 4);

        contentBox.getChildren().addAll(notesCarousel, playlistsCarousel, drainCarousel, rageCarousel);

        ScrollPane scrollPane = new ScrollPane(contentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        setCenter(scrollPane);
        BorderPane.setMargin(scrollPane, new Insets(0, 16, 0, 24));
    }

    private void loadData() {
        new Thread(() -> {
            List<Note> notes = ApiClient.getNotes();
            List<Track> drainTracks = ApiClient.getTracks("Drain");
            List<Track> rageTracks = ApiClient.getTracks("Rage");

            List<Playlist> playlists = new ArrayList<>();
            Playlist liked = new Playlist();
            liked.setName("Liked");
            liked.setDescription("Your favorite tracks");
            playlists.add(liked);

            Playlist recent = new Playlist();
            recent.setName("Recently Played");
            recent.setDescription("Tracks you listened to");
            playlists.add(recent);

            Playlist favs = new Playlist();
            favs.setName("For Your Favs");
            favs.setDescription("Meditation vibes");
            playlists.add(favs);

            Platform.runLater(() -> {
                List<TrackCard> noteCards = new ArrayList<>();
                for (Note note : notes) {
                    noteCards.add(new TrackCard(note, item -> detailsHandler.accept(item)));
                }
                notesCarousel.setCards(noteCards);

                List<TrackCard> playlistCards = new ArrayList<>();
                for (Playlist playlist : playlists) {
                    playlistCards.add(new TrackCard(playlist, item -> {
                        if (item instanceof Playlist) {
                            navigationHandler.accept("playlist:" + ((Playlist) item).getName());
                        }
                    }));
                }
                playlistsCarousel.setCards(playlistCards);

                List<TrackCard> drainCards = new ArrayList<>();
                for (Track track : drainTracks) {
                    drainCards.add(new TrackCard(track, item -> detailsHandler.accept(item)));
                }
                drainCarousel.setCards(drainCards);

                List<TrackCard> rageCards = new ArrayList<>();
                for (Track track : rageTracks) {
                    rageCards.add(new TrackCard(track, item -> detailsHandler.accept(item)));
                }
                rageCarousel.setCards(rageCards);
            });
        }).start();
    }

    private static String toHex(Color color) {
        return String.format("#%02X%02X%02X",
            (int) (color.getRed() * 255),
            (int) (color.getGreen() * 255),
            (int) (color.getBlue() * 255));
    }
}

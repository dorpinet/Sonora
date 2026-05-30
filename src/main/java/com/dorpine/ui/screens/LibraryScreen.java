package com.dorpine.ui.screens;

import com.dorpine.api.ApiClient;
import com.dorpine.model.Playlist;
import com.dorpine.ui.components.TopBar;
import com.dorpine.ui.components.TrackCard;
import com.dorpine.util.Fonts;
import com.dorpine.util.Theme;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class LibraryScreen extends StackPane {
    private final Consumer<String> navHandler;
    private VBox playlistsContainer;

    public LibraryScreen(Consumer<String> navHandler) {
        this.navHandler = navHandler;
        build();
    }

    private void build() {
        setStyle("-fx-background-color: " + Theme.GRADIENT_CSS() + ";");

        VBox mainBox = new VBox(16);
        mainBox.setAlignment(Pos.TOP_LEFT);
        mainBox.setPadding(new Insets(20, 24, 24, 24));
        mainBox.setFillWidth(true);

        TopBar topBar = new TopBar(navHandler, null);
        topBar.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(topBar, Priority.NEVER);

        Label title = new Label("My library");
        title.setFont(Fonts.heading(28));
        title.setStyle("-fx-text-fill: " + Theme.toCss(Theme.textPrimary()) + ";");
        title.setPadding(new Insets(8, 0, 8, 0));

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        playlistsContainer = new VBox(16);
        playlistsContainer.setAlignment(Pos.TOP_LEFT);
        playlistsContainer.setPadding(new Insets(8, 0, 0, 0));

        // Static fallback stubs while loading
        List<Playlist> stubs = new ArrayList<>();
        Playlist liked = new Playlist(); liked.setName("Liked"); liked.setDescription("Your favorite tracks"); stubs.add(liked);
        Playlist recent = new Playlist(); recent.setName("Recently Played"); recent.setDescription("Tracks you listened to"); stubs.add(recent);
        Playlist favs = new Playlist(); favs.setName("For Your Favs"); favs.setDescription("Meditation vibes"); stubs.add(favs);

        for (Playlist p : stubs) {
            playlistsContainer.getChildren().add(new TrackCard(p, item -> {
                if (item instanceof Playlist pl) navHandler.accept("playlist:" + pl.getName());
            }));
        }

        scrollPane.setContent(playlistsContainer);

        mainBox.getChildren().addAll(topBar, title, scrollPane);
        getChildren().add(mainBox);

        loadPlaylists();
    }

    private void loadPlaylists() {
        new Thread(() -> {
            List<Playlist> playlists = ApiClient.getPlaylists();
            Platform.runLater(() -> {
                if (!playlists.isEmpty()) {
                    playlistsContainer.getChildren().clear();
                    HBox row = new HBox(16);
                    row.setAlignment(Pos.TOP_LEFT);
                    for (Playlist p : playlists) {
                        if (p.getName() == null) p.setName("Playlist");
                        if (p.getDescription() == null) p.setDescription("");
                        row.getChildren().add(new TrackCard(p, item -> {
                            if (item instanceof Playlist pl) navHandler.accept("playlist:" + pl.getName());
                        }));
                    }
                    playlistsContainer.getChildren().add(row);
                }
            });
        }).start();
    }
}

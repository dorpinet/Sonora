package com.dorpine.ui.screens;

import com.dorpine.model.Playlist;
import com.dorpine.ui.components.TopBar;
import com.dorpine.ui.components.TrackCard;
import com.dorpine.util.Fonts;
import com.dorpine.util.Theme;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class LibraryScreen extends StackPane {
    private final Consumer<String> navHandler;

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

        HBox playlistsGrid = new HBox(16);
        playlistsGrid.setAlignment(Pos.TOP_LEFT);
        playlistsGrid.setPadding(new Insets(8, 0, 0, 0));

        List<Playlist> stubs = new ArrayList<>();
        Playlist liked = new Playlist(); liked.setName("Liked"); liked.setDescription("Your favorite tracks"); stubs.add(liked);
        Playlist recent = new Playlist(); recent.setName("Recently Played"); recent.setDescription("Tracks you listened to"); stubs.add(recent);
        Playlist favs = new Playlist(); favs.setName("For Your Favs"); favs.setDescription("Meditation vibes"); stubs.add(favs);

        for (Playlist p : stubs) {
            playlistsGrid.getChildren().add(new TrackCard(p, item -> {
                if (item instanceof Playlist pl) navHandler.accept("playlist:" + pl.getName());
            }));
        }

        mainBox.getChildren().addAll(topBar, title, playlistsGrid);
        getChildren().add(mainBox);
    }
}

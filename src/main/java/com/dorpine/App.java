package com.dorpine;

import com.dorpine.model.Note;
import com.dorpine.model.Playlist;
import com.dorpine.model.Track;
import com.dorpine.ui.components.DetailsPanel;
import com.dorpine.ui.components.TopBar;
import com.dorpine.ui.screens.*;
import com.dorpine.util.Fonts;
import com.dorpine.util.Theme;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application {
    private StackPane root;
    private DetailsPanel detailsPanel;

    @Override
    public void start(Stage stage) {
        Fonts.load();
        root = new StackPane();
        root.setStyle("-fx-background-color: " + Theme.GRADIENT_CSS() + ";");
        showHome();
        Scene scene = new Scene(root, 1200, 800);
        stage.setTitle("SONORA");
        stage.setScene(scene);
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.show();
    }

    private void showHome() {
        root.getChildren().clear();

        HBox mainBox = new HBox();

        VBox leftPanel = new VBox();
        leftPanel.setSpacing(16);
        leftPanel.setPadding(new Insets(20, 20, 0, 20));
        HBox.setHgrow(leftPanel, Priority.ALWAYS);

        TopBar topBar = new TopBar(this::navigateTo, () -> Platform.runLater(this::toggleTheme));
        topBar.setPadding(new Insets(0, 20, 0, 20));

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setPannable(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        HomeScreen homeScreen = new HomeScreen(this::navigateTo, this::showDetails);
        scrollPane.setContent(homeScreen);

        leftPanel.getChildren().addAll(topBar, scrollPane);

        detailsPanel = new DetailsPanel(this::navigateTo);

        mainBox.getChildren().addAll(leftPanel, detailsPanel);
        root.getChildren().add(mainBox);
    }

    private void toggleTheme() {
        Theme.toggle();
        root.setStyle("-fx-background-color: " + Theme.GRADIENT_CSS() + ";");
        showHome();
    }

    private void showDetails(Object item) {
        if (item instanceof Track t) {
            detailsPanel.showTrack(t);
            String sid = t.getSpotifyId();
            if (sid != null && !sid.isEmpty()) {
                new Thread(() -> {
                    String url = com.dorpine.api.ApiClient.getPreviewUrl(sid);
                    Platform.runLater(() -> detailsPanel.setPreviewUrl(url));
                }).start();
            } else {
                detailsPanel.setPreviewUrl(null);
            }
        } else if (item instanceof Note n) {
            detailsPanel.showNote(n);
            String sid = n.getSpotifyId();
            if (sid != null && !sid.isEmpty()) {
                new Thread(() -> {
                    String url = com.dorpine.api.ApiClient.getPreviewUrl(sid);
                    Platform.runLater(() -> detailsPanel.setPreviewUrl(url));
                }).start();
            } else {
                detailsPanel.setPreviewUrl(null);
            }
        }
    }

    private void navigateTo(String screen) {
        if (screen.equals("home")) { showHome(); return; }
        root.getChildren().clear();
        root.setStyle("-fx-background-color: " + Theme.GRADIENT_CSS() + ";");
        switch (screen) {
            case "settings" -> root.getChildren().add(new SettingsScreen(this::navigateTo));
            case "theory"   -> root.getChildren().add(new TheoryScreen(this::navigateTo));
            case "piano"    -> root.getChildren().add(new PianoScreen(this::navigateTo));
            default -> {
                if (screen.startsWith("playlist:")) {
                    String name = screen.substring("playlist:".length());
                    Playlist stub = new Playlist();
                    stub.setName(name);
                    if (name.equals("Liked")) stub.setDescription("Your favorite tracks");
                    else if (name.equals("Recently Played")) stub.setDescription("Tracks you listened to");
                    else if (name.equals("For Your Favs")) stub.setDescription("Meditation vibes");
                    root.getChildren().add(new PlaylistDetailScreen(stub, this::navigateTo));
                } else { showHome(); }
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

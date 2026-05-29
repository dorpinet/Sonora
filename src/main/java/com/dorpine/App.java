package com.dorpine;

import com.dorpine.model.Note;
import com.dorpine.model.Track;
import com.dorpine.ui.components.DetailsPanel;
import com.dorpine.ui.components.TopBar;
import com.dorpine.ui.screens.*;
import com.dorpine.util.Theme;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class App extends Application {
    private StackPane root;
    private DetailsPanel detailsPanel;
    private HomeScreen homeScreen;

    @Override
    public void start(Stage primaryStage) {
        root = new StackPane();
        root.setStyle("-fx-background-color: " + toHex(Theme.BACKGROUND_START) + ";");

        showHome();

        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setTitle("SONORA");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }

    private void showHome() {
        BorderPane homeRoot = new BorderPane();
        homeRoot.setStyle("-fx-background-color: " + toHex(Theme.BACKGROUND_START) + ";");

        TopBar topBar = new TopBar(this::navigateTo);
        homeRoot.setTop(topBar);
        BorderPane.setMargin(topBar, new Insets(16, 24, 8, 24));

        detailsPanel = new DetailsPanel(this::navigateTo);
        homeScreen = new HomeScreen(this::navigateTo, this::showDetails);

        homeRoot.setCenter(homeScreen);
        homeRoot.setRight(detailsPanel);
        BorderPane.setMargin(detailsPanel, new Insets(0, 24, 16, 0));

        root.getChildren().clear();
        root.getChildren().add(homeRoot);
    }

    private void showDetails(Object item) {
        if (detailsPanel == null) return;
        if (item instanceof Track) {
            Track track = (Track) item;
            detailsPanel.showTrack(track);
            String spotifyId = track.getSpotifyId();
            if (spotifyId != null && !spotifyId.isEmpty()) {
                new Thread(() -> {
                    String previewUrl = com.dorpine.api.ApiClient.getPreviewUrl(spotifyId);
                    javafx.application.Platform.runLater(() -> detailsPanel.setPreviewUrl(previewUrl));
                }).start();
            } else {
                detailsPanel.setPreviewUrl(null);
            }
        } else if (item instanceof Note) {
            Note note = (Note) item;
            detailsPanel.showNote(note);
            String spotifyId = note.getSpotifyId();
            if (spotifyId != null && !spotifyId.isEmpty()) {
                new Thread(() -> {
                    String previewUrl = com.dorpine.api.ApiClient.getPreviewUrl(spotifyId);
                    javafx.application.Platform.runLater(() -> detailsPanel.setPreviewUrl(previewUrl));
                }).start();
            } else {
                detailsPanel.setPreviewUrl(null);
            }
        }
    }

    private void navigateTo(String screen) {
        if (screen.equals("home")) {
            showHome();
            return;
        }

        if (screen.startsWith("playlist:")) {
            String playlistName = screen.substring("playlist:".length());
            root.getChildren().clear();
            root.getChildren().add(new PlaylistDetailScreen(playlistName, this::navigateTo));
            return;
        }

        root.getChildren().clear();
        switch (screen) {
            case "settings":
                root.getChildren().add(new SettingsScreen(this::navigateTo));
                break;
            case "theory":
                root.getChildren().add(new TheoryScreen(this::navigateTo));
                break;
            case "piano":
                root.getChildren().add(new PianoScreen(this::navigateTo));
                break;
            default:
                showHome();
        }
    }

    private static String toHex(javafx.scene.paint.Color color) {
        return String.format("#%02X%02X%02X",
            (int) (color.getRed() * 255),
            (int) (color.getGreen() * 255),
            (int) (color.getBlue() * 255));
    }

    public static void main(String[] args) {
        launch(args);
    }
}

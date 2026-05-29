package com.dorpine.ui.screens;

import com.dorpine.util.Theme;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.function.Consumer;

public class PlaylistDetailScreen extends VBox {
    public PlaylistDetailScreen(String playlistName, Consumer<String> navigationHandler) {
        setAlignment(Pos.CENTER);
        setSpacing(24);
        setPadding(new Insets(40));
        setStyle("-fx-background-color: " + toHex(Theme.BACKGROUND_START) + ";");

        Label title = new Label(playlistName);
        title.setStyle("-fx-text-fill: " + toHex(Theme.TEXT_PRIMARY) + "; -fx-font-size: 32px; -fx-font-weight: bold;");

        Label subtitle = new Label("Playlist details coming soon...");
        subtitle.setStyle("-fx-text-fill: " + toHex(Theme.TEXT_SECONDARY) + "; -fx-font-size: 16px;");

        Button backBtn = new Button("Back to Home");
        backBtn.setStyle(
            "-fx-background-color: " + toHex(Theme.ACCENT) + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-padding: 12 24;" +
            "-fx-background-radius: 24px;" +
            "-fx-cursor: hand;"
        );
        backBtn.setOnAction(e -> navigationHandler.accept("home"));

        getChildren().addAll(title, subtitle, backBtn);
    }

    private static String toHex(Color color) {
        return String.format("#%02X%02X%02X",
            (int) (color.getRed() * 255),
            (int) (color.getGreen() * 255),
            (int) (color.getBlue() * 255));
    }
}

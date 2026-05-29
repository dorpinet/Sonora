package com.dorpine.ui.screens;

import com.dorpine.util.Theme;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class SettingsScreen extends VBox {
    public SettingsScreen(Consumer<String> nav) {
        setAlignment(Pos.CENTER); setSpacing(24); setPadding(new javafx.geometry.Insets(40));
        setStyle("-fx-background-color: " + Theme.GRADIENT_CSS() + ";");

        Label t = new Label("Settings");
        t.setStyle("-fx-text-fill: " + Theme.toCss(Theme.textPrimary()) + "; -fx-font-size: 32px; -fx-font-weight: bold;");
        Label s = new Label("Coming soon...");
        s.setStyle("-fx-text-fill: " + Theme.toCss(Theme.textSecondary()) + "; -fx-font-size: 16px;");
        Button b = new Button("Back to Home");
        b.setStyle(String.join(";", "-fx-background-color: " + Theme.toCss(Theme.accent()), "-fx-text-fill: white",
              "-fx-font-size: 14px", "-fx-padding: 12 24", "-fx-background-radius: 24px", "-fx-cursor: hand"));
        b.setOnAction(e -> nav.accept("home"));
        getChildren().addAll(t, s, b);
    }
}

package com.dorpine.ui.screens;

import com.dorpine.util.Fonts;
import com.dorpine.util.Theme;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.function.Consumer;

public class SettingsScreen extends StackPane {
    private final Consumer<String> navHandler;
    private final Runnable themeToggleHandler;

    public SettingsScreen(Consumer<String> navHandler, Runnable themeToggleHandler) {
        this.navHandler = navHandler;
        this.themeToggleHandler = themeToggleHandler;
        build();
    }

    private void build() {
        setStyle("-fx-background-color: " + Theme.GRADIENT_CSS() + ";");

        VBox mainBox = new VBox(16);
        mainBox.setAlignment(Pos.TOP_LEFT);
        mainBox.setPadding(new Insets(20, 24, 24, 24));
        mainBox.setFillWidth(true);

        com.dorpine.ui.components.TopBar topBar = new com.dorpine.ui.components.TopBar(navHandler, themeToggleHandler);
        topBar.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(topBar, Priority.NEVER);

        HBox contentBox = new HBox(24);
        contentBox.setAlignment(Pos.TOP_CENTER);
        HBox.setHgrow(contentBox, Priority.ALWAYS);
        VBox.setVgrow(contentBox, Priority.ALWAYS);
        contentBox.setMaxWidth(Double.MAX_VALUE);

        // Left: user card
        VBox userCard = new VBox(8);
        userCard.setAlignment(Pos.TOP_CENTER);
        userCard.setPadding(new Insets(12));
        userCard.setStyle(String.join(";",
            "-fx-background-color: " + (Theme.isDark() ? "rgba(255,255,255,0.08)" : "rgba(255,255,255,0.45)"),
            "-fx-background-radius: 20px",
            "-fx-border-color: " + (Theme.isDark() ? "rgba(255,255,255,0.15)" : "rgba(255,255,255,0.7)"),
            "-fx-border-radius: 20px",
            "-fx-border-width: 1px"
        ));
        userCard.setPrefWidth(220);
        userCard.setMaxWidth(260);
        userCard.setMinWidth(200);

        StackPane avatarPane = new StackPane();
        avatarPane.setPrefSize(120, 120);
        avatarPane.setMinSize(120, 120);
        avatarPane.setMaxSize(120, 120);
        avatarPane.setStyle("-fx-background-color: " + Theme.toCss(Color.web("rgba(200,190,255,0.2)")) + "; -fx-background-radius: 16px;");

        String avatarUrl = "https://pub-b7eac54927804333ac001f55505f5006.r2.dev/Noicon(1).jpg";
        try {
            Image img = new Image(avatarUrl, 120, 120, true, true, true);
            ImageView iv = new ImageView(img);
            iv.setFitWidth(120);
            iv.setFitHeight(120);
            iv.setPreserveRatio(true);
            Rectangle c = new Rectangle(120, 120);
            c.setArcWidth(16);
            c.setArcHeight(16);
            iv.setClip(c);
            avatarPane.getChildren().add(iv);
        } catch (Exception e) {
            Label placeholder = new Label("U");
            placeholder.setFont(Fonts.heading(48));
            placeholder.setStyle("-fx-text-fill: " + Theme.toCss(Theme.accent()) + ";");
            avatarPane.getChildren().add(placeholder);
        }

        Label username = new Label("@67diva");
        username.setFont(Fonts.heading(18));
        username.setStyle("-fx-text-fill: " + Theme.toCss(Theme.textPrimary()) + ";");
        username.setAlignment(Pos.CENTER);

        userCard.getChildren().addAll(avatarPane, username);

        // Right: settings panel
        StackPane settingsPanel = new StackPane();
        settingsPanel.setStyle(String.join(";",
            "-fx-background-color: " + (Theme.isDark() ? "rgba(255,255,255,0.08)" : "rgba(255,255,255,0.45)"),
            "-fx-background-radius: 20px",
            "-fx-border-color: " + (Theme.isDark() ? "rgba(255,255,255,0.15)" : "rgba(255,255,255,0.7)"),
            "-fx-border-radius: 20px",
            "-fx-border-width: 1px"
        ));
        settingsPanel.setPadding(new Insets(24));
        HBox.setHgrow(settingsPanel, Priority.ALWAYS);

        VBox settingsBox = new VBox(4);
        settingsBox.setAlignment(Pos.TOP_CENTER);
        settingsBox.setFillWidth(true);

        settingsBox.getChildren().add(settingRow("Change username", () -> showChangeField("New username", val -> username.setText("@" + val))));
        settingsBox.getChildren().add(settingRow("Change email", () -> showChangeField("New email", val -> {})));
        settingsBox.getChildren().add(settingRow("Change password", () -> showChangeField("New password", val -> {})));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        settingsBox.getChildren().add(spacer);

        Label logout = new Label("Logout");
        logout.setFont(Fonts.heading(18));
        logout.setStyle("-fx-text-fill: " + Theme.toCss(Theme.textPrimary()) + "; -fx-cursor: hand;");
        logout.setPadding(new Insets(12, 0, 0, 0));
        logout.setOnMouseClicked(e -> navHandler.accept("home"));
        settingsBox.getChildren().add(logout);

        settingsPanel.getChildren().add(settingsBox);

        contentBox.getChildren().addAll(userCard, settingsPanel);

        mainBox.getChildren().addAll(topBar, contentBox);
        getChildren().add(mainBox);
    }

    private HBox settingRow(String text, Runnable onClick) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(16, 12, 16, 12));
        row.setStyle(String.join(";",
            "-fx-background-color: transparent",
            "-fx-border-color: " + (Theme.isDark() ? "rgba(255,255,255,0.1)" : "rgba(255,255,255,0.5)"),
            "-fx-border-width: 0 0 1 0"
        ));

        Label lbl = new Label(text);
        lbl.setFont(Fonts.heading(18));
        lbl.setStyle("-fx-text-fill: " + Theme.toCss(Theme.textPrimary()) + ";");
        HBox.setHgrow(lbl, Priority.ALWAYS);

        Label arrow = new Label("\u2192");
        arrow.setFont(Fonts.body(22));
        arrow.setStyle("-fx-text-fill: " + Theme.toCss(Theme.textPrimary()) + "; -fx-cursor: hand;");

        row.setOnMouseClicked(e -> onClick.run());
        arrow.setOnMouseClicked(e -> onClick.run());
        lbl.setOnMouseClicked(e -> onClick.run());

        row.getChildren().addAll(lbl, arrow);
        return row;
    }

    private void showChangeField(String prompt, java.util.function.Consumer<String> onSave) {
        javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog();
        dialog.setTitle(prompt);
        dialog.setHeaderText(null);
        dialog.setContentText(prompt + ":");
        dialog.getEditor().setStyle(
            "-fx-background-radius: 12px; -fx-padding: 8 12; -fx-background-color: " +
            (Theme.isDark() ? "rgba(255,255,255,0.1)" : "rgba(255,255,255,0.6)") + ";"
        );
        dialog.showAndWait().ifPresent(onSave);
    }
}

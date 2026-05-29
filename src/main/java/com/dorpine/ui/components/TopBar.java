package com.dorpine.ui.components;

import com.dorpine.util.Fonts;
import com.dorpine.util.Theme;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.function.Consumer;

public class TopBar extends HBox {
    private final Consumer<String> navHandler;
    private final Runnable themeToggleHandler;

    public TopBar(Consumer<String> navHandler, Runnable themeToggleHandler) {
        this.navHandler = navHandler;
        this.themeToggleHandler = themeToggleHandler;
        build();
    }

    private void build() {
        setAlignment(Pos.CENTER);
        setSpacing(20);
        setPadding(new Insets(16, 32, 16, 32));
        setStyle(String.join(";",
            "-fx-background-radius: 32px",
            "-fx-background-color: " + Theme.toCss(Theme.topBarBg())
        ));

        StackPane logoPane = new StackPane();
        logoPane.setPrefSize(36, 36);
        ImageView logo = new ImageView();
        try {
            Image img = new Image(getClass().getResourceAsStream("/images/logo.png"));
            logo.setImage(img);
        } catch (Exception e) {
            System.err.println("[TopBar] Logo load failed: " + e.getMessage());
        }
        logo.setFitWidth(36);
        logo.setFitHeight(36);
        logo.setPreserveRatio(true);
        Rectangle clip = new Rectangle(36, 36);
        clip.setArcWidth(10);
        clip.setArcHeight(10);
        logo.setClip(clip);
        logoPane.getChildren().add(logo);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox navBox = new HBox(28);
        navBox.setAlignment(Pos.CENTER);

        navBox.getChildren().addAll(
            navBtn("Home", true),
            navBtn("Settings", false),
            navBtn("Theory", false)
        );

        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        StackPane toggle = new StackPane();
        toggle.setPrefWidth(48);
        toggle.setPrefHeight(26);
        toggle.setMaxWidth(48);
        toggle.setMaxHeight(26);
        toggle.setStyle(String.join(";",
            "-fx-background-radius: 13px",
            "-fx-background-color: " + (Theme.isDark() ? "#6C5CE7" : "#2D3436"),
            "-fx-cursor: hand"
        ));

        Circle thumb = new Circle(10, Color.WHITE);
        thumb.setTranslateX(Theme.isDark() ? 10 : -10);
        toggle.getChildren().add(thumb);

        toggle.setOnMouseClicked(e -> {
            if (themeToggleHandler != null) themeToggleHandler.run();
        });

        getChildren().addAll(logoPane, spacer, navBox, spacer2, toggle);
    }

    private Label navBtn(String text, boolean active) {
        String color = Theme.isDark() ? (active ? "#FFFFFF" : "rgba(255,255,255,0.65)") : "#000000";
        Label l = new Label(text);
        l.setFont(Fonts.body(15));
        l.setStyle(String.join(";",
            "-fx-text-fill: " + color,
            "-fx-font-weight: " + (active ? "bold" : "normal"),
            "-fx-cursor: hand"
        ));
        l.setOnMouseClicked(e -> {
            if (!active && navHandler != null) navHandler.accept(text.toLowerCase());
        });
        return l;
    }
}

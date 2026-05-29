package com.dorpine.ui.components;

import com.dorpine.util.Fonts;
import com.dorpine.util.Theme;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
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
        setSpacing(16);
        setPadding(new Insets(8, 20, 8, 20));

        String glassBg = Theme.isDark()
            ? "rgba(20,20,30,0.6)"
            : "rgba(255,255,255,0.55)";
        String glassBorder = Theme.isDark()
            ? "rgba(255,255,255,0.12)"
            : "rgba(255,255,255,0.8)";

        setStyle(String.join(";",
            "-fx-background-radius: 24px",
            "-fx-background-color: " + glassBg,
            "-fx-border-color: " + glassBorder,
            "-fx-border-radius: 24px",
            "-fx-border-width: 1px"
        ));

        GaussianBlur blur = new GaussianBlur(0);
        blur.setRadius(12);
        setEffect(blur);

        StackPane logoPane = new StackPane();
        logoPane.setPrefSize(28, 28);
        ImageView logo = new ImageView();
        try {
            Image img = new Image(getClass().getResourceAsStream("/images/logo.png"));
            logo.setImage(img);
        } catch (Exception e) {}
        logo.setFitWidth(28);
        logo.setFitHeight(28);
        logo.setPreserveRatio(true);
        Rectangle clip = new Rectangle(28, 28);
        clip.setArcWidth(8);
        clip.setArcHeight(8);
        logo.setClip(clip);
        logoPane.getChildren().add(logo);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox navBox = new HBox(20);
        navBox.setAlignment(Pos.CENTER);
        navBox.getChildren().addAll(
            navBtn("Home", true),
            navBtn("Settings", false),
            navBtn("Theory", false)
        );

        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        StackPane toggle = new StackPane();
        toggle.setPrefWidth(44);
        toggle.setPrefHeight(24);
        toggle.setMaxWidth(44);
        toggle.setMaxHeight(24);
        toggle.setStyle(String.join(";",
            "-fx-background-radius: 12px",
            "-fx-background-color: " + (Theme.isDark() ? "#6C5CE7" : "#2D3436"),
            "-fx-cursor: hand"
        ));

        Circle thumb = new Circle(9, Color.WHITE);
        thumb.setTranslateX(Theme.isDark() ? 9 : -9);
        toggle.getChildren().add(thumb);

        toggle.setOnMouseClicked(e -> {
            if (themeToggleHandler != null) themeToggleHandler.run();
        });

        getChildren().addAll(logoPane, spacer, navBox, spacer2, toggle);
    }

    private Label navBtn(String text, boolean active) {
        String color = Theme.isDark() ? (active ? "#FFFFFF" : "rgba(255,255,255,0.65)") : "#000000";
        Label l = new Label(text);
        l.setFont(Fonts.body(14));
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

package com.dorpine.ui.components;

import com.dorpine.util.Theme;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;

import java.util.function.Consumer;

public class TopBar extends HBox {
    private final Consumer<String> navigationHandler;

    public TopBar(Consumer<String> navigationHandler) {
        this.navigationHandler = navigationHandler;
        build();
    }

    private void build() {
        setAlignment(Pos.CENTER);
        setSpacing(20);
        setPadding(new Insets(12, 24, 12, 24));
        setStyle(
            "-fx-background-radius: " + Theme.TOP_BAR_RADIUS + ";" +
            "-fx-background-color: " + toHex(Theme.TOP_BAR_BG) + ";"
        );

        HBox logoBox = new HBox();
        logoBox.setAlignment(Pos.CENTER);
        SVGPath logo = new SVGPath();
        logo.setContent("M12,2C6.48,2,2,6.48,2,12s4.48,10,10,10s10-4.48,10-10S17.52,2,12,2z M12,20c-4.41,0-8-3.59-8-8s3.59-8,8-8s8,3.59,8,8S16.41,20,12,20z M9.5,16.5l7-4.5l-7-4.5V16.5z");
        logo.setFill(Color.WHITE);
        logo.setScaleX(1.2);
        logo.setScaleY(1.2);
        logoBox.getChildren().add(logo);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox navBox = new HBox(24);
        navBox.setAlignment(Pos.CENTER);

        Label homeBtn = createNavButton("Home", true);
        Label settingsBtn = createNavButton("Settings", false);
        Label theoryBtn = createNavButton("Theory", false);

        homeBtn.setOnMouseClicked(e -> navigationHandler.accept("home"));
        settingsBtn.setOnMouseClicked(e -> navigationHandler.accept("settings"));
        theoryBtn.setOnMouseClicked(e -> navigationHandler.accept("theory"));

        navBox.getChildren().addAll(homeBtn, settingsBtn, theoryBtn);

        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        ToggleButton themeToggle = new ToggleButton();
        themeToggle.setText("");
        themeToggle.setStyle(
            "-fx-background-radius: 20px;" +
            "-fx-min-width: 44px;" +
            "-fx-min-height: 24px;" +
            "-fx-background-color: #2D3436;"
        );

        Circle thumb = new Circle(8, Color.WHITE);
        thumb.setTranslateX(-8);
        themeToggle.setGraphic(thumb);

        themeToggle.selectedProperty().addListener((obs, oldVal, newVal) -> {
            thumb.setTranslateX(newVal ? 8 : -8);
        });

        getChildren().addAll(logoBox, spacer, navBox, spacer2, themeToggle);
    }

    private Label createNavButton(String text, boolean active) {
        Label label = new Label(text);
        label.setStyle(
            "-fx-text-fill: " + (active ? toHex(Theme.TEXT_LIGHT) : toHex(Color.web("rgba(255,255,255,0.7)"))) + ";" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: " + (active ? "bold" : "normal") + ";" +
            "-fx-cursor: hand;"
        );
        label.setOnMouseEntered(e -> {
            if (!active) {
                label.setStyle(label.getStyle().replace(toHex(Color.web("rgba(255,255,255,0.7)")), toHex(Theme.TEXT_LIGHT)));
            }
        });
        label.setOnMouseExited(e -> {
            if (!active) {
                label.setStyle(label.getStyle().replace(toHex(Theme.TEXT_LIGHT), toHex(Color.web("rgba(255,255,255,0.7)"))));
            }
        });
        return label;
    }

    private static String toHex(Color color) {
        int r = (int) (color.getRed() * 255);
        int g = (int) (color.getGreen() * 255);
        int b = (int) (color.getBlue() * 255);
        double a = color.getOpacity();
        if (a >= 1.0) {
            return String.format("#%02X%02X%02X", r, g, b);
        }
        return String.format("rgba(%d,%d,%d,%.2f)", r, g, b, a);
    }
}

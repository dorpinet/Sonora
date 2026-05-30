package com.dorpine.ui.screens;

import com.dorpine.api.ApiClient;
import com.dorpine.util.Fonts;
import com.dorpine.util.Theme;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class InterestScreen extends StackPane {
    private final Consumer<String> navHandler;
    private final List<String> selected = new ArrayList<>();
    private final List<Button> interestBtns = new ArrayList<>();

    public InterestScreen(Consumer<String> navHandler) {
        this.navHandler = navHandler;
        build();
    }

    private void build() {
        setStyle("-fx-background-color: " + Theme.GRADIENT_CSS() + ";");

        VBox card = new VBox(12);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPrefWidth(340);
        card.setMaxWidth(340);
        card.setMinWidth(340);
        card.setMaxHeight(Region.USE_PREF_SIZE);
        card.setPadding(new Insets(24));
        String glassBg = Theme.isDark() ? "rgba(30,30,45,0.55)" : "rgba(255,255,255,0.55)";
        String glassBorder = Theme.isDark() ? "rgba(255,255,255,0.18)" : "rgba(255,255,255,0.8)";
        card.setStyle(String.join(";",
            "-fx-background-color: " + glassBg,
            "-fx-background-radius: 20px",
            "-fx-border-color: " + glassBorder,
            "-fx-border-radius: 20px",
            "-fx-border-width: 1px"
        ));

        Label title = new Label("What are you into?");
        title.setFont(Fonts.heading(20));
        title.setStyle("-fx-text-fill: " + Theme.toCss(Theme.textPrimary()) + ";");
        title.setAlignment(Pos.CENTER);

        Label subtitle = new Label("Pick at least one to get your daily playlist");
        subtitle.setFont(Fonts.body(12));
        subtitle.setStyle("-fx-text-fill: " + Theme.toCss(Theme.textSecondary()) + ";");
        subtitle.setAlignment(Pos.CENTER);
        subtitle.setWrapText(true);

        VBox btnBox = new VBox(8);
        btnBox.setAlignment(Pos.TOP_CENTER);
        btnBox.setFillWidth(true);

        String[] interests = {"Sport", "Meditation", "Party", "Focus"};
        for (String interest : interests) {
            Button btn = new Button(interest);
            btn.setFont(Fonts.body(14));
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setPrefHeight(40);
            String defaultStyle = String.join(";",
                "-fx-background-color: " + (Theme.isDark() ? "rgba(255,255,255,0.08)" : "rgba(255,255,255,0.5)"),
                "-fx-text-fill: " + Theme.toCss(Theme.textPrimary()),
                "-fx-background-radius: 12px",
                "-fx-cursor: hand",
                "-fx-border-color: " + (Theme.isDark() ? "rgba(255,255,255,0.15)" : "rgba(255,255,255,0.6)"),
                "-fx-border-radius: 12px",
                "-fx-border-width: 1px"
            );
            btn.setStyle(defaultStyle);
            btn.setOnAction(e -> toggleInterest(btn, interest, defaultStyle));
            interestBtns.add(btn);
            btnBox.getChildren().add(btn);
        }

        Button continueBtn = new Button("Continue");
        continueBtn.setFont(Fonts.heading(14));
        continueBtn.setMaxWidth(Double.MAX_VALUE);
        continueBtn.setPrefHeight(42);
        continueBtn.setStyle(String.join(";",
            "-fx-background-color: " + Theme.toCss(Theme.accent()),
            "-fx-text-fill: #FFFFFF",
            "-fx-background-radius: 24px",
            "-fx-cursor: hand",
            "-fx-font-weight: bold"
        ));
        continueBtn.setOnAction(e -> handleContinue());

        card.getChildren().addAll(title, subtitle, btnBox, continueBtn);
        getChildren().add(card);
        StackPane.setAlignment(card, Pos.CENTER);
    }

    private void toggleInterest(Button btn, String interest, String defaultStyle) {
        if (selected.contains(interest)) {
            selected.remove(interest);
            btn.setStyle(defaultStyle);
        } else {
            selected.add(interest);
            btn.setStyle(String.join(";",
                "-fx-background-color: " + Theme.toCss(Theme.accent()),
                "-fx-text-fill: #FFFFFF",
                "-fx-background-radius: 12px",
                "-fx-cursor: hand",
                "-fx-border-color: " + Theme.toCss(Theme.accent()),
                "-fx-border-radius: 12px",
                "-fx-border-width: 1px"
            ));
        }
    }

    private void handleContinue() {
        if (selected.isEmpty()) return;
        for (Button b : interestBtns) b.setDisable(true);
        new Thread(() -> {
            List<String> lower = selected.stream().map(String::toLowerCase).toList();
            ApiClient.AuthResult r1 = ApiClient.saveInterests(lower);
            ApiClient.AuthResult r2 = ApiClient.regenerateDaily();
            Platform.runLater(() -> {
                if (r1.success || r2.success) {
                    navHandler.accept("home");
                } else {
                    for (Button b : interestBtns) b.setDisable(false);
                }
            });
        }).start();
    }
}

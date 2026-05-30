package com.dorpine.ui.screens;

import com.dorpine.api.ApiClient;
import com.dorpine.model.Note;
import com.dorpine.util.Fonts;
import com.dorpine.util.Theme;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.List;
import java.util.function.Consumer;

public class PianoScreen extends StackPane {
    private final Consumer<String> navHandler;

    public PianoScreen(Consumer<String> navHandler) {
        this.navHandler = navHandler;
        build();
        loadNotes();
    }

    private void build() {
        setStyle("-fx-background-color: " + Theme.GRADIENT_CSS() + ";");

        VBox mainBox = new VBox(20);
        mainBox.setAlignment(Pos.TOP_CENTER);
        mainBox.setPadding(new Insets(20, 24, 24, 24));
        mainBox.setFillWidth(true);

        // Top-right controls
        HBox topControls = new HBox();
        topControls.setAlignment(Pos.TOP_RIGHT);
        topControls.setSpacing(12);

        // Back button (small plaque)
        Button backBtn = new Button("\u2190");
        backBtn.setFont(Fonts.body(16));
        backBtn.setStyle(String.join(";",
            "-fx-background-color: " + (Theme.isDark() ? "rgba(255,255,255,0.15)" : "rgba(255,255,255,0.6)"),
            "-fx-text-fill: " + Theme.toCss(Theme.textPrimary()),
            "-fx-background-radius: 12px",
            "-fx-min-width: 36px",
            "-fx-min-height: 36px",
            "-fx-cursor: hand"
        ));
        backBtn.setOnAction(e -> navHandler.accept("home"));

        // Logo
        ImageView logo = new ImageView();
        try {
            Image img = new Image(getClass().getResourceAsStream("/images/logo.png"));
            logo.setImage(img);
        } catch (Exception e) {}
        logo.setFitWidth(32);
        logo.setFitHeight(32);
        logo.setPreserveRatio(true);
        Rectangle clip = new Rectangle(32, 32);
        clip.setArcWidth(8);
        clip.setArcHeight(8);
        logo.setClip(clip);

        topControls.getChildren().addAll(backBtn, logo);

        // Notes viewer (glass card)
        StackPane notesCard = new StackPane();
        notesCard.setStyle(String.join(";",
            "-fx-background-color: " + (Theme.isDark() ? "rgba(255,255,255,0.08)" : "rgba(255,255,255,0.45)"),
            "-fx-background-radius: 20px",
            "-fx-border-color: " + (Theme.isDark() ? "rgba(255,255,255,0.15)" : "rgba(255,255,255,0.7)"),
            "-fx-border-radius: 20px",
            "-fx-border-width: 1px"
        ));
        notesCard.setPadding(new Insets(16));
        VBox.setVgrow(notesCard, Priority.ALWAYS);

        ScrollPane notesScroll = new ScrollPane();
        notesScroll.setFitToWidth(true);
        notesScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        notesScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        notesScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        notesScroll.setPannable(true);

        VBox notesContent = new VBox(12);
        notesContent.setAlignment(Pos.TOP_CENTER);
        notesContent.setId("notesContent");

        notesScroll.setContent(notesContent);
        notesCard.getChildren().add(notesScroll);

        // Piano keys
        PianoKeys piano = new PianoKeys();
        piano.setPrefHeight(160);
        piano.setMaxHeight(180);
        VBox.setVgrow(piano, Priority.NEVER);

        mainBox.getChildren().addAll(topControls, notesCard, piano);
        getChildren().add(mainBox);
    }

    private void loadNotes() {
        new Thread(() -> {
            List<Note> notes = ApiClient.getNotes();
            Platform.runLater(() -> {
                VBox content = (VBox) lookup("#notesContent");
                if (content == null) return;
                content.getChildren().clear();

                if (notes.isEmpty()) {
                    Label empty = new Label("No sheet music available");
                    empty.setFont(Fonts.body(16));
                    empty.setStyle("-fx-text-fill: " + Theme.toCss(Theme.textSecondary()) + ";");
                    content.getChildren().add(empty);
                    return;
                }

                for (Note note : notes) {
                    if (note.getImageUrl() != null && !note.getImageUrl().isEmpty()) {
                        ImageView iv = new ImageView();
                        try {
                            Image img = new Image(note.getImageUrl(), 700, 0, true, true, true);
                            iv.setImage(img);
                            iv.setFitWidth(700);
                            iv.setPreserveRatio(true);
                            iv.setStyle("-fx-background-radius: 12px;");
                        } catch (Exception e) {
                            continue;
                        }
                        content.getChildren().add(iv);
                    }
                }
            });
        }).start();
    }
}

package com.dorpine.ui.screens;

import com.dorpine.model.Note;
import com.dorpine.util.Fonts;
import com.dorpine.util.Theme;
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

import java.util.function.Consumer;

public class PianoScreen extends StackPane {
    private final Note note;
    private final Consumer<String> navHandler;

    public PianoScreen(Note note, Consumer<String> navHandler) {
        this.note = note;
        this.navHandler = navHandler;
        build();
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

        // Notes viewer - FIXED max height so piano doesn't get squished
        StackPane notesCard = new StackPane();
        notesCard.setStyle(String.join(";",
            "-fx-background-color: " + (Theme.isDark() ? "rgba(255,255,255,0.08)" : "rgba(255,255,255,0.45)"),
            "-fx-background-radius: 20px",
            "-fx-border-color: " + (Theme.isDark() ? "rgba(255,255,255,0.15)" : "rgba(255,255,255,0.7)"),
            "-fx-border-radius: 20px",
            "-fx-border-width: 1px"
        ));
        notesCard.setPadding(new Insets(16));
        notesCard.setMaxHeight(320);
        notesCard.setPrefHeight(300);
        VBox.setVgrow(notesCard, Priority.NEVER);

        ScrollPane notesScroll = new ScrollPane();
        notesScroll.setFitToWidth(true);
        notesScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        notesScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        notesScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        notesScroll.setPannable(true);

        VBox notesContent = new VBox(12);
        notesContent.setAlignment(Pos.TOP_CENTER);
        notesContent.setFillWidth(true);

        loadNoteContent(notesContent);

        notesScroll.setContent(notesContent);
        notesCard.getChildren().add(notesScroll);

        // Piano keys - fixed large height
        PianoKeys piano = new PianoKeys();
        piano.setPrefHeight(240);
        piano.setMinHeight(200);
        piano.setMaxHeight(280);
        VBox.setVgrow(piano, Priority.NEVER);

        // Prompt text for hotkeys
        Label hint = new Label("Hotkeys: Z X C V B N M , . / Q W E R T Y U  |  Black: S D G H J L ; 2 3 5 6 7");
        hint.setFont(Fonts.body(11));
        hint.setStyle("-fx-text-fill: " + Theme.toCss(Theme.textSecondary()) + ";");
        hint.setAlignment(Pos.CENTER);

        mainBox.getChildren().addAll(topControls, notesCard, hint, piano);
        getChildren().add(mainBox);

        // Focus piano for keyboard input
        Platform.runLater(() -> piano.requestFocus());
    }

    private void loadNoteContent(VBox content) {
        content.getChildren().clear();

        if (note == null) {
            Label empty = new Label("Select a track with sheet music");
            empty.setFont(Fonts.body(16));
            empty.setStyle("-fx-text-fill: " + Theme.toCss(Theme.textSecondary()) + ";");
            content.getChildren().add(empty);
            return;
        }

        String url = note.getImageUrl() != null ? note.getImageUrl() : note.getCoverUrl();
        if (url != null && !url.isEmpty()) {
            ImageView iv = new ImageView();
            try {
                Image img = new Image(url, 700, 0, true, true, true);
                iv.setImage(img);
                iv.setFitWidth(700);
                iv.setPreserveRatio(true);
                iv.setStyle("-fx-background-radius: 12px;");
            } catch (Exception e) {
                showError(content, "Failed to load sheet music");
                return;
            }
            content.getChildren().add(iv);
        } else {
            showError(content, "No sheet music available for this track");
        }
    }

    private void showError(VBox content, String msg) {
        Label err = new Label(msg);
        err.setFont(Fonts.body(16));
        err.setStyle("-fx-text-fill: " + Theme.toCss(Theme.textSecondary()) + ";");
        content.getChildren().add(err);
    }
}

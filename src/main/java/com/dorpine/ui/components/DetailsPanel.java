package com.dorpine.ui.components;

import com.dorpine.model.Note;
import com.dorpine.model.Track;
import com.dorpine.util.Theme;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.function.Consumer;

public class DetailsPanel extends VBox {
    private final Consumer<String> navigationHandler;
    private MediaPlayer mediaPlayer;
    private Button listenButton;
    private String currentPreviewUrl;
    private boolean isPlaying = false;

    private final Label nameLabel;
    private final Label artistLabel;
    private final StackPane coverPane;

    public DetailsPanel(Consumer<String> navigationHandler) {
        this.navigationHandler = navigationHandler;
        setAlignment(Pos.TOP_CENTER);
        setSpacing(16);
        setPadding(new Insets(20));
        setPrefWidth(280);
        setMinWidth(260);
        setMaxWidth(320);
        setStyle(
            "-fx-background-color: " + toHex(Theme.DETAILS_PANEL_BG) + ";" +
            "-fx-background-radius: " + Theme.CARD_RADIUS + ";" +
            "-fx-border-color: " + toHex(Theme.CARD_BORDER) + ";" +
            "-fx-border-radius: " + Theme.CARD_RADIUS + ";" +
            "-fx-border-width: 1px;"
        );

        Label titleLabel = new Label("Details");
        titleLabel.setStyle("-fx-text-fill: " + toHex(Theme.TEXT_PRIMARY) + "; -fx-font-size: 18px; -fx-font-weight: bold;");
        titleLabel.setAlignment(Pos.CENTER);

        coverPane = new StackPane();
        coverPane.setPrefSize(200, 200);
        coverPane.setMinSize(200, 200);
        coverPane.setMaxSize(200, 200);
        coverPane.setStyle("-fx-background-color: " + toHex(Color.web("rgba(200,190,255,0.3)")) + "; -fx-background-radius: 16px;");
        showPlaceholder();

        nameLabel = new Label("Select a track");
        nameLabel.setStyle("-fx-text-fill: " + toHex(Theme.TEXT_PRIMARY) + "; -fx-font-size: 16px; -fx-font-weight: bold;");
        nameLabel.setWrapText(true);
        nameLabel.setAlignment(Pos.CENTER);

        artistLabel = new Label("");
        artistLabel.setStyle("-fx-text-fill: " + toHex(Theme.TEXT_SECONDARY) + "; -fx-font-size: 14px;");
        artistLabel.setWrapText(true);
        artistLabel.setAlignment(Pos.CENTER);

        listenButton = createActionButton("Listen");
        listenButton.setOnAction(e -> toggleListen());

        Button playButton = createActionButton("Play");
        playButton.setOnAction(e -> {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                isPlaying = false;
                listenButton.setText("Listen");
            }
            navigationHandler.accept("piano");
        });

        getChildren().addAll(titleLabel, coverPane, nameLabel, artistLabel, listenButton, playButton);
    }

    public void showTrack(Track track) {
        if (track == null) {
            clear();
            return;
        }
        nameLabel.setText(track.getTitle());
        artistLabel.setText(track.getArtist());
        setCoverImage(track.getCoverUrl());
        currentPreviewUrl = null;
        isPlaying = false;
        listenButton.setText("Listen");
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
    }

    public void showNote(Note note) {
        if (note == null) {
            clear();
            return;
        }
        nameLabel.setText(note.getTitle());
        artistLabel.setText(note.getArtist());
        String cover = note.getCoverUrl() != null ? note.getCoverUrl() : note.getImageUrl();
        setCoverImage(cover);
        currentPreviewUrl = null;
        isPlaying = false;
        listenButton.setText("Listen");
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
    }

    public void setPreviewUrl(String previewUrl) {
        this.currentPreviewUrl = previewUrl;
        if (isPlaying) {
            stopPlayback();
            startPlayback();
        }
    }

    private void toggleListen() {
        if (currentPreviewUrl == null || currentPreviewUrl.isEmpty()) {
            listenButton.setText("No preview");
            return;
        }

        if (isPlaying) {
            stopPlayback();
            listenButton.setText("Listen");
        } else {
            startPlayback();
            listenButton.setText("Pause");
        }
    }

    private void startPlayback() {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.dispose();
            }
            Media media = new Media(currentPreviewUrl);
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setOnEndOfMedia(() -> {
                isPlaying = false;
                listenButton.setText("Listen");
            });
            mediaPlayer.setOnError(() -> {
                isPlaying = false;
                listenButton.setText("Listen");
            });
            mediaPlayer.play();
            isPlaying = true;
        } catch (Exception e) {
            listenButton.setText("Error");
            isPlaying = false;
        }
    }

    private void stopPlayback() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
        isPlaying = false;
    }

    private void setCoverImage(String url) {
        coverPane.getChildren().clear();
        if (url != null && !url.isEmpty()) {
            try {
                Image image = new Image(url, 200, 200, true, true, true);
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(200);
                imageView.setFitHeight(200);
                imageView.setPreserveRatio(true);
                Rectangle clip = new Rectangle(200, 200);
                clip.setArcWidth(16);
                clip.setArcHeight(16);
                imageView.setClip(clip);
                coverPane.getChildren().add(imageView);
            } catch (Exception e) {
                showPlaceholder();
            }
        } else {
            showPlaceholder();
        }
    }

    private void showPlaceholder() {
        coverPane.getChildren().clear();
        Label placeholder = new Label("🎵");
        placeholder.setStyle("-fx-font-size: 48px;");
        coverPane.getChildren().add(placeholder);
    }

    public void clear() {
        nameLabel.setText("Select a track");
        artistLabel.setText("");
        showPlaceholder();
        currentPreviewUrl = null;
        isPlaying = false;
        listenButton.setText("Listen");
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
    }

    private Button createActionButton(String text) {
        Button btn = new Button(text);
        btn.setPrefWidth(180);
        btn.setPrefHeight(40);
        btn.setStyle(
            "-fx-background-color: " + toHex(Theme.BUTTON_BG) + ";" +
            "-fx-text-fill: " + toHex(Theme.TEXT_LIGHT) + ";" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: " + Theme.BUTTON_RADIUS + ";" +
            "-fx-border-color: " + toHex(Theme.BUTTON_BORDER) + ";" +
            "-fx-border-radius: " + Theme.BUTTON_RADIUS + ";" +
            "-fx-border-width: 1px;" +
            "-fx-cursor: hand;"
        );
        btn.setOnMouseEntered(e -> btn.setStyle(btn.getStyle().replace(toHex(Theme.BUTTON_BG), toHex(Color.web("rgba(255,255,255,0.4)")))));
        btn.setOnMouseExited(e -> btn.setStyle(btn.getStyle().replace(toHex(Color.web("rgba(255,255,255,0.4)")), toHex(Theme.BUTTON_BG))));
        return btn;
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

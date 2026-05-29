package com.dorpine.ui.components;

import com.dorpine.model.Note;
import com.dorpine.model.Playlist;
import com.dorpine.model.Track;
import com.dorpine.util.Theme;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.function.Consumer;

public class TrackCard extends StackPane {
    private final Object item;
    private final Consumer<Object> clickHandler;

    public TrackCard(Track track, Consumer<Object> clickHandler) {
        this.item = track;
        this.clickHandler = clickHandler;
        build(track.getCoverUrl(), track.getTitle(), track.getArtist());
    }

    public TrackCard(Note note, Consumer<Object> clickHandler) {
        this.item = note;
        this.clickHandler = clickHandler;
        build(note.getCoverUrl() != null ? note.getCoverUrl() : note.getImageUrl(), note.getTitle(), note.getArtist());
    }

    public TrackCard(Playlist playlist, Consumer<Object> clickHandler) {
        this.item = playlist;
        this.clickHandler = clickHandler;
        String cover = playlist.getCoverUrl();
        if (cover == null && playlist.getTracks() != null && !playlist.getTracks().isEmpty()) {
            cover = playlist.getTracks().get(0).getCoverUrl();
        }
        build(cover, playlist.getName(), playlist.getDescription());
    }

    public TrackCard(String title, String subtitle, String coverUrl, Consumer<Object> clickHandler) {
        this.item = null;
        this.clickHandler = clickHandler;
        build(coverUrl, title, subtitle);
    }

    private void build(String coverUrl, String title, String artist) {
        setAlignment(Pos.TOP_CENTER);
        setPadding(new Insets(8));
        setStyle(
            "-fx-background-color: " + toHex(Theme.CARD_BG) + ";" +
            "-fx-background-radius: " + Theme.CARD_RADIUS + ";" +
            "-fx-border-color: " + toHex(Theme.CARD_BORDER) + ";" +
            "-fx-border-radius: " + Theme.CARD_RADIUS + ";" +
            "-fx-border-width: 1px;" +
            "-fx-cursor: hand;"
        );

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web("rgba(0,0,0,0.1)"));
        shadow.setRadius(8);
        shadow.setOffsetY(2);
        setEffect(shadow);

        VBox content = new VBox(6);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(6));

        StackPane imagePane = new StackPane();
        imagePane.setPrefSize(120, 120);
        imagePane.setMinSize(120, 120);
        imagePane.setMaxSize(120, 120);

        if (coverUrl != null && !coverUrl.isEmpty()) {
            try {
                Image image = new Image(coverUrl, 120, 120, true, true, true);
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(120);
                imageView.setFitHeight(120);
                imageView.setPreserveRatio(true);
                Rectangle clip = new Rectangle(120, 120);
                clip.setArcWidth(12);
                clip.setArcHeight(12);
                imageView.setClip(clip);
                imagePane.getChildren().add(imageView);
            } catch (Exception e) {
                imagePane.getChildren().add(createPlaceholder(title));
            }
        } else {
            imagePane.getChildren().add(createPlaceholder(title));
        }

        Label titleLabel = new Label(title != null ? title : "Unknown");
        titleLabel.setStyle("-fx-text-fill: " + toHex(Theme.TEXT_PRIMARY) + "; -fx-font-size: 13px; -fx-font-weight: bold;");
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(120);
        titleLabel.setAlignment(Pos.CENTER);

        Label artistLabel = new Label(artist != null && !artist.isEmpty() ? artist : " ");
        artistLabel.setStyle("-fx-text-fill: " + toHex(Theme.TEXT_SECONDARY) + "; -fx-font-size: 11px;");
        artistLabel.setWrapText(true);
        artistLabel.setMaxWidth(120);
        artistLabel.setAlignment(Pos.CENTER);

        content.getChildren().addAll(imagePane, titleLabel, artistLabel);
        getChildren().add(content);

        setOnMouseClicked(e -> {
            if (clickHandler != null) {
                clickHandler.accept(item);
            }
        });

        setOnMouseEntered(e -> {
            setStyle(getStyle().replace(toHex(Theme.CARD_BG), toHex(Color.web("rgba(255,255,255,0.5)"))));
        });
        setOnMouseExited(e -> {
            setStyle(getStyle().replace(toHex(Color.web("rgba(255,255,255,0.5)")), toHex(Theme.CARD_BG)));
        });
    }

    private StackPane createPlaceholder(String title) {
        StackPane placeholder = new StackPane();
        placeholder.setPrefSize(120, 120);
        placeholder.setStyle("-fx-background-color: " + toHex(Color.web("rgba(200,190,255,0.5)")) + "; -fx-background-radius: 12px;");

        Label label = new Label(title != null && title.length() > 0 ? title.substring(0, 1).toUpperCase() : "?");
        label.setStyle("-fx-text-fill: " + toHex(Theme.ACCENT) + "; -fx-font-size: 32px; -fx-font-weight: bold;");
        placeholder.getChildren().add(label);
        return placeholder;
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

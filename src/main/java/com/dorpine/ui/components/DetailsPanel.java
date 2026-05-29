package com.dorpine.ui.components;

import com.dorpine.model.Note;
import com.dorpine.model.Track;
import com.dorpine.util.Fonts;
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
    private final Consumer<String> navHandler;
    private MediaPlayer mediaPlayer;
    private Button listenBtn;
    private String previewUrl;
    private boolean playing;

    private final Label nameLabel;
    private final Label artistLabel;
    private final StackPane coverPane;

    public DetailsPanel(Consumer<String> navHandler) {
        this.navHandler = navHandler;
        setAlignment(Pos.TOP_CENTER);
        setSpacing(16);
        setPadding(new Insets(20));
        setPrefWidth(280);
        setMinWidth(260);
        setMaxWidth(320);
        style(this, "-fx-background-radius: 16px", "-fx-background-color: " + Theme.toCss(Theme.detailsBg()),
              "-fx-border-color: " + Theme.toCss(Theme.cardBorder()), "-fx-border-radius: 16px", "-fx-border-width: 1px");

        Label title = new Label("Details");
        title.setFont(Fonts.heading(18));
        title.setStyle("-fx-text-fill: " + Theme.toCss(Theme.textPrimary()) + ";");

        coverPane = new StackPane();
        coverPane.setPrefSize(200, 200);
        coverPane.setMinSize(200, 200);
        coverPane.setMaxSize(200, 200);
        coverPane.setStyle("-fx-background-color: " + Theme.toCss(Color.web("rgba(200,190,255,0.2)")) + "; -fx-background-radius: 16px;");
        showPlaceholder();

        nameLabel = new Label("Select a track");
        nameLabel.setFont(Fonts.heading(16));
        nameLabel.setStyle("-fx-text-fill: " + Theme.toCss(Theme.textPrimary()) + ";");
        nameLabel.setWrapText(true);
        nameLabel.setAlignment(Pos.CENTER);

        artistLabel = new Label("");
        artistLabel.setFont(Fonts.body(14));
        artistLabel.setStyle("-fx-text-fill: " + Theme.toCss(Theme.textSecondary()) + ";");
        artistLabel.setWrapText(true);
        artistLabel.setAlignment(Pos.CENTER);

        listenBtn = btn("Listen");
        listenBtn.setOnAction(e -> toggleListen());

        Button playBtn = btn("Play");
        playBtn.setOnAction(e -> {
            stop();
            navHandler.accept("piano");
        });

        getChildren().addAll(title, coverPane, nameLabel, artistLabel, listenBtn, playBtn);
    }

    public void showTrack(Track t) {
        if (t == null) { clear(); return; }
        nameLabel.setText(t.getTitle());
        artistLabel.setText(t.getArtist());
        setCover(t.getCoverUrl());
        previewUrl = null; playing = false; listenBtn.setText("Listen"); stop();
    }

    public void showNote(Note n) {
        if (n == null) { clear(); return; }
        nameLabel.setText(n.getTitle());
        artistLabel.setText(n.getArtist());
        setCover(n.getCoverUrl() != null ? n.getCoverUrl() : n.getImageUrl());
        previewUrl = null; playing = false; listenBtn.setText("Listen"); stop();
    }

    public void setPreviewUrl(String url) {
        this.previewUrl = url;
    }

    private void toggleListen() {
        if (previewUrl == null || previewUrl.isEmpty()) {
            listenBtn.setText("No preview");
            return;
        }
        if (playing) { pause(); listenBtn.setText("Listen"); }
        else { play(); listenBtn.setText("Pause"); }
    }

    private void play() {
        try {
            if (mediaPlayer != null) mediaPlayer.dispose();
            mediaPlayer = new MediaPlayer(new Media(previewUrl));
            mediaPlayer.setOnEndOfMedia(() -> { playing = false; listenBtn.setText("Listen"); });
            mediaPlayer.play(); playing = true;
        } catch (Exception e) { listenBtn.setText("Error"); playing = false; }
    }

    private void pause() { if (mediaPlayer != null) mediaPlayer.pause(); playing = false; }
    private void stop() { if (mediaPlayer != null) { mediaPlayer.stop(); mediaPlayer.dispose(); mediaPlayer = null; } playing = false; }

    private void setCover(String url) {
        coverPane.getChildren().clear();
        if (url != null && !url.isEmpty()) {
            try {
                Image img = new Image(url, 200, 200, true, true, true);
                ImageView iv = new ImageView(img);
                iv.setFitWidth(200); iv.setFitHeight(200); iv.setPreserveRatio(true);
                Rectangle clip = new Rectangle(200, 200); clip.setArcWidth(16); clip.setArcHeight(16);
                iv.setClip(clip);
                coverPane.getChildren().add(iv);
            } catch (Exception e) { showPlaceholder(); }
        } else { showPlaceholder(); }
    }

    private void showPlaceholder() {
        coverPane.getChildren().clear();
        Label l = new Label("\uD83C\uDFB5");
        l.setStyle("-fx-font-size: 48px;");
        coverPane.getChildren().add(l);
    }

    public void clear() {
        nameLabel.setText("Select a track"); artistLabel.setText(""); showPlaceholder();
        previewUrl = null; playing = false; listenBtn.setText("Listen"); stop();
    }

    private Button btn(String text) {
        Button b = new Button(text);
        b.setPrefWidth(180); b.setPrefHeight(40);
        style(b, "-fx-background-color: " + Theme.toCss(Theme.btnBg()), "-fx-text-fill: " + Theme.toCss(Theme.textPrimary()),
              "-fx-font-size: 14px", "-fx-font-weight: bold", "-fx-background-radius: 24px",
              "-fx-border-color: " + Theme.toCss(Theme.btnBorder()), "-fx-border-radius: 24px", "-fx-border-width: 1px", "-fx-cursor: hand");
        b.setOnMouseEntered(e -> b.setStyle(b.getStyle().replace(Theme.toCss(Theme.btnBg()), "rgba(255,255,255,0.4)")));
        b.setOnMouseExited(e -> b.setStyle(b.getStyle().replace("rgba(255,255,255,0.4)", Theme.toCss(Theme.btnBg()))));
        return b;
    }

    private static void style(javafx.scene.Node n, String... props) {
        n.setStyle(String.join(";", props));
    }
}

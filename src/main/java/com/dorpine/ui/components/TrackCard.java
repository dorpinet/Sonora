package com.dorpine.ui.components;

import com.dorpine.model.Note;
import com.dorpine.model.Playlist;
import com.dorpine.model.Track;
import com.dorpine.util.Fonts;
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
    private final Consumer<Object> handler;

    public TrackCard(Track t, Consumer<Object> handler) { this.item = t; this.handler = handler; build(t.getCoverUrl(), t.getTitle(), t.getArtist()); }
    public TrackCard(Note n, Consumer<Object> handler) { this.item = n; this.handler = handler; build(n.getCoverUrl() != null ? n.getCoverUrl() : n.getImageUrl(), n.getTitle(), n.getArtist()); }
    public TrackCard(Playlist p, Consumer<Object> handler) {
        this.item = p; this.handler = handler;
        String cover = p.getCoverUrl();
        if (cover == null && p.getTracks() != null && !p.getTracks().isEmpty()) cover = p.getTracks().get(0).getCoverUrl();
        build(cover, p.getName(), p.getDescription());
    }

    public TrackCard(String title, String subtitle, String cover, Consumer<Object> handler) {
        this.item = null; this.handler = handler; build(cover, title, subtitle);
    }

    private void build(String cover, String title, String artist) {
        setAlignment(Pos.TOP_CENTER);
        setPadding(new Insets(10));
        String baseStyle = String.join(";",
            "-fx-background-color: " + Theme.toCss(Theme.cardBg()),
            "-fx-background-radius: 16px",
            "-fx-border-color: " + Theme.toCss(Theme.cardBorder()),
            "-fx-border-radius: 16px",
            "-fx-border-width: 1px",
            "-fx-cursor: hand"
        );
        setStyle(baseStyle);

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web("rgba(0,0,0,0.08)"));
        shadow.setRadius(12);
        shadow.setOffsetY(4);
        setEffect(shadow);

        VBox content = new VBox(8);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(8));

        StackPane imgPane = new StackPane();
        imgPane.setPrefSize(150, 150);
        imgPane.setMinSize(150, 150);
        imgPane.setMaxSize(150, 150);

        if (cover != null && !cover.isEmpty()) {
            try {
                Image img = new Image(cover, 150, 150, true, true, true);
                ImageView iv = new ImageView(img);
                iv.setFitWidth(150);
                iv.setFitHeight(150);
                iv.setPreserveRatio(true);
                Rectangle clip = new Rectangle(150, 150);
                clip.setArcWidth(14);
                clip.setArcHeight(14);
                iv.setClip(clip);
                imgPane.getChildren().add(iv);
            } catch (Exception e) {
                imgPane.getChildren().add(placeholder(title));
            }
        } else {
            imgPane.getChildren().add(placeholder(title));
        }

        Label titleLbl = new Label(title != null ? title : "Unknown");
        titleLbl.setFont(Fonts.heading(14));
        titleLbl.setStyle("-fx-text-fill: " + Theme.toCss(Theme.textPrimary()) + ";");
        titleLbl.setWrapText(true);
        titleLbl.setAlignment(Pos.CENTER);
        titleLbl.setPrefWidth(150);
        titleLbl.setMaxWidth(150);

        Label artistLbl = new Label(artist != null && !artist.isEmpty() ? artist : " ");
        artistLbl.setFont(Fonts.body(12));
        artistLbl.setStyle("-fx-text-fill: " + Theme.toCss(Theme.textSecondary()) + ";");
        artistLbl.setWrapText(true);
        artistLbl.setAlignment(Pos.CENTER);
        artistLbl.setPrefWidth(150);
        artistLbl.setMaxWidth(150);

        content.getChildren().addAll(imgPane, titleLbl, artistLbl);
        getChildren().add(content);

        String hoverBg = Theme.toCss(Color.web("rgba(255,255,255,0.5)"));
        setOnMouseClicked(e -> { if (handler != null) handler.accept(item); });
        setOnMouseEntered(e -> setStyle(baseStyle + "; -fx-background-color: " + hoverBg));
        setOnMouseExited(e -> setStyle(baseStyle));
    }

    private StackPane placeholder(String title) {
        StackPane p = new StackPane();
        p.setPrefSize(150, 150);
        p.setStyle("-fx-background-color: " + Theme.toCss(Color.web("rgba(200,190,255,0.5)")) + "; -fx-background-radius: 14px;");
        Label l = new Label(title != null && !title.isEmpty() ? title.substring(0, 1).toUpperCase() : "?");
        l.setFont(Fonts.heading(36));
        l.setStyle("-fx-text-fill: " + Theme.toCss(Theme.accent()) + ";");
        p.getChildren().add(l);
        return p;
    }
}

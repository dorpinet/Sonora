package com.dorpine.ui.screens;

import com.dorpine.model.Playlist;
import com.dorpine.ui.components.TopBar;
import com.dorpine.util.Fonts;
import com.dorpine.util.Theme;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.function.Consumer;

public class PlaylistDetailScreen extends StackPane {
    private final Consumer<String> navHandler;

    public PlaylistDetailScreen(Playlist playlist, Consumer<String> navHandler) {
        this.navHandler = navHandler;
        build(playlist);
    }

    private void build(Playlist playlist) {
        setStyle("-fx-background-color: " + Theme.GRADIENT_CSS() + ";");

        VBox mainBox = new VBox(16);
        mainBox.setAlignment(Pos.TOP_CENTER);
        mainBox.setPadding(new Insets(20, 24, 24, 24));
        mainBox.setFillWidth(true);

        // Full top bar
        TopBar topBar = new TopBar(navHandler, null);
        topBar.setPadding(new Insets(0, 0, 8, 0));

        // Main content: compact left card + right tracklist
        HBox contentBox = new HBox(24);
        contentBox.setAlignment(Pos.TOP_CENTER);
        HBox.setHgrow(contentBox, Priority.ALWAYS);
        VBox.setVgrow(contentBox, Priority.ALWAYS);

        // Left: compact playlist card
        VBox playlistCard = new VBox(10);
        playlistCard.setAlignment(Pos.TOP_CENTER);
        playlistCard.setPadding(new Insets(16));
        playlistCard.setStyle(String.join(";",
            "-fx-background-color: " + (Theme.isDark() ? "rgba(255,255,255,0.08)" : "rgba(255,255,255,0.45)"),
            "-fx-background-radius: 16px",
            "-fx-border-color: " + (Theme.isDark() ? "rgba(255,255,255,0.15)" : "rgba(255,255,255,0.7)"),
            "-fx-border-radius: 16px",
            "-fx-border-width: 1px"
        ));
        playlistCard.setPrefWidth(220);
        playlistCard.setMaxWidth(240);
        playlistCard.setMinWidth(200);

        StackPane coverPane = new StackPane();
        coverPane.setPrefSize(140, 140);
        coverPane.setMinSize(140, 140);
        coverPane.setMaxSize(140, 140);
        coverPane.setStyle("-fx-background-color: " + Theme.toCss(Color.web("rgba(200,190,255,0.2)")) + "; -fx-background-radius: 12px;");

        String coverUrl = playlist.getCoverUrl();
        if (coverUrl != null && !coverUrl.isEmpty()) {
            try {
                Image img = new Image(coverUrl, 140, 140, true, true, true);
                ImageView iv = new ImageView(img);
                iv.setFitWidth(140);
                iv.setFitHeight(140);
                iv.setPreserveRatio(true);
                Rectangle c = new Rectangle(140, 140);
                c.setArcWidth(12);
                c.setArcHeight(12);
                iv.setClip(c);
                coverPane.getChildren().add(iv);
            } catch (Exception e) {
                showCoverPlaceholder(coverPane, playlist.getName());
            }
        } else {
            showCoverPlaceholder(coverPane, playlist.getName());
        }

        Label nameLbl = new Label(playlist.getName());
        nameLbl.setFont(Fonts.heading(16));
        nameLbl.setStyle("-fx-text-fill: " + Theme.toCss(Theme.textPrimary()) + ";");
        nameLbl.setAlignment(Pos.CENTER);
        nameLbl.setWrapText(true);
        nameLbl.setMaxWidth(180);

        Label descLbl = new Label(playlist.getDescription() != null ? playlist.getDescription() : "");
        descLbl.setFont(Fonts.body(12));
        descLbl.setStyle("-fx-text-fill: " + Theme.toCss(Theme.textSecondary()) + ";");
        descLbl.setAlignment(Pos.CENTER);
        descLbl.setWrapText(true);
        descLbl.setMaxWidth(180);

        playlistCard.getChildren().addAll(coverPane, nameLbl, descLbl);

        // Right: tracklist panel
        StackPane tracklistPanel = new StackPane();
        tracklistPanel.setStyle(String.join(";",
            "-fx-background-color: " + (Theme.isDark() ? "rgba(255,255,255,0.08)" : "rgba(255,255,255,0.45)"),
            "-fx-background-radius: 20px",
            "-fx-border-color: " + (Theme.isDark() ? "rgba(255,255,255,0.15)" : "rgba(255,255,255,0.7)"),
            "-fx-border-radius: 20px",
            "-fx-border-width: 1px"
        ));
        tracklistPanel.setPadding(new Insets(20));
        HBox.setHgrow(tracklistPanel, Priority.ALWAYS);

        VBox tracklistBox = new VBox(8);
        tracklistBox.setAlignment(Pos.TOP_CENTER);
        tracklistBox.setFillWidth(true);

        Label header = new Label("Tracklist");
        header.setFont(Fonts.heading(20));
        header.setStyle("-fx-text-fill: " + Theme.toCss(Theme.textPrimary()) + ";");
        header.setAlignment(Pos.CENTER);
        header.setMaxWidth(Double.MAX_VALUE);
        tracklistBox.getChildren().add(header);

        ScrollPane trackScroll = new ScrollPane();
        trackScroll.setFitToWidth(true);
        trackScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        trackScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        trackScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        VBox.setVgrow(trackScroll, Priority.ALWAYS);

        VBox trackContent = new VBox(6);
        trackContent.setAlignment(Pos.TOP_CENTER);
        trackContent.setFillWidth(true);

        var tracks = playlist.getTracks();
        if (tracks != null && !tracks.isEmpty()) {
            int idx = 1;
            for (var pt : tracks) {
                HBox trackRow = new HBox(12);
                trackRow.setAlignment(Pos.CENTER_LEFT);
                trackRow.setPadding(new Insets(8, 12, 8, 12));
                trackRow.setStyle(String.join(";",
                    "-fx-background-color: " + (Theme.isDark() ? "rgba(255,255,255,0.05)" : "rgba(255,255,255,0.3)"),
                    "-fx-background-radius: 12px"
                ));

                Label num = new Label(String.valueOf(idx));
                num.setFont(Fonts.body(13));
                num.setStyle("-fx-text-fill: " + Theme.toCss(Theme.textSecondary()) + "; -fx-min-width: 24px;");

                VBox info = new VBox(2);
                Label title = new Label(pt.getTitle() != null ? pt.getTitle() : "Unknown");
                title.setFont(Fonts.heading(14));
                title.setStyle("-fx-text-fill: " + Theme.toCss(Theme.textPrimary()) + ";");
                Label artist = new Label(pt.getArtist() != null ? pt.getArtist() : "");
                artist.setFont(Fonts.body(12));
                artist.setStyle("-fx-text-fill: " + Theme.toCss(Theme.textSecondary()) + ";");
                info.getChildren().addAll(title, artist);
                HBox.setHgrow(info, Priority.ALWAYS);

                trackRow.getChildren().addAll(num, info);
                trackContent.getChildren().add(trackRow);
                idx++;
            }
        } else {
            Label empty = new Label("No tracks yet");
            empty.setFont(Fonts.body(16));
            empty.setStyle("-fx-text-fill: " + Theme.toCss(Theme.textSecondary()) + ";");
            trackContent.getChildren().add(empty);
        }

        trackScroll.setContent(trackContent);
        tracklistBox.getChildren().add(trackScroll);
        tracklistPanel.getChildren().add(tracklistBox);

        contentBox.getChildren().addAll(playlistCard, tracklistPanel);

        mainBox.getChildren().addAll(topBar, contentBox);
        getChildren().add(mainBox);
    }

    private void showCoverPlaceholder(StackPane pane, String name) {
        pane.getChildren().clear();
        Label l = new Label(name != null && !name.isEmpty() ? name.substring(0, 1).toUpperCase() : "?");
        l.setFont(Fonts.heading(36));
        l.setStyle("-fx-text-fill: " + Theme.toCss(Theme.accent()) + ";");
        pane.getChildren().add(l);
    }
}

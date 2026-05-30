package com.dorpine.ui.screens;

import com.dorpine.ui.components.TopBar;
import com.dorpine.util.Fonts;
import com.dorpine.util.Theme;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class TheoryScreen extends StackPane {
    private final Consumer<String> navHandler;
    private final Runnable themeToggleHandler;

    public TheoryScreen(Consumer<String> navHandler, Runnable themeToggleHandler) {
        this.navHandler = navHandler;
        this.themeToggleHandler = themeToggleHandler;
        build();
    }

    private void build() {
        setStyle("-fx-background-color: " + Theme.GRADIENT_CSS() + ";");

        VBox mainBox = new VBox(16);
        mainBox.setAlignment(Pos.TOP_LEFT);
        mainBox.setPadding(new Insets(20, 24, 24, 24));
        mainBox.setFillWidth(true);

        TopBar topBar = new TopBar(navHandler, themeToggleHandler);
        topBar.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(topBar, Priority.NEVER);

        VBox content = new VBox(24);
        content.setAlignment(Pos.CENTER);
        VBox.setVgrow(content, Priority.ALWAYS);
        content.setPadding(new Insets(40, 0, 0, 0));

        Label t = new Label("Theory");
        t.setFont(Fonts.heading(32));
        t.setStyle("-fx-text-fill: " + Theme.toCss(Theme.textPrimary()) + ";");

        Label s = new Label("Music theory coming soon...");
        s.setFont(Fonts.body(16));
        s.setStyle("-fx-text-fill: " + Theme.toCss(Theme.textSecondary()) + ";");

        content.getChildren().addAll(t, s);

        mainBox.getChildren().addAll(topBar, content);
        getChildren().add(mainBox);
    }
}

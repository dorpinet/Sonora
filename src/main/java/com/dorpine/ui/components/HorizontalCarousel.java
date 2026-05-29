package com.dorpine.ui.components;

import com.dorpine.util.Theme;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

import java.util.List;

public class HorizontalCarousel extends StackPane {
    private final HBox contentBox;
    private int currentIndex;
    private final int visibleCount;

    public HorizontalCarousel(String title, List<TrackCard> cards, int visibleCount) {
        this.visibleCount = visibleCount;

        VBox container = new VBox(16);
        container.setAlignment(Pos.CENTER);

        HBox carousel = new HBox();
        carousel.setAlignment(Pos.CENTER);
        carousel.setSpacing(16);

        Button leftBtn = arrow(true);
        Button rightBtn = arrow(false);

        contentBox = new HBox(16);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new Insets(8));

        Region leftSpacer = new Region();
        leftSpacer.setPrefWidth(40);
        Region rightSpacer = new Region();
        rightSpacer.setPrefWidth(40);
        HBox.setHgrow(contentBox, Priority.ALWAYS);

        carousel.getChildren().addAll(leftBtn, leftSpacer, contentBox, rightSpacer, rightBtn);
        container.getChildren().addAll(carousel);
        getChildren().add(container);
        setAlignment(Pos.CENTER);

        leftBtn.setOnAction(e -> scroll(-1));
        rightBtn.setOnAction(e -> scroll(1));

        setCards(cards);
    }

    private Button arrow(boolean left) {
        Button b = new Button();
        b.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-min-width: 40px; -fx-min-height: 40px;");
        Polygon p = new Polygon();
        if (left) p.getPoints().addAll(12.0, 0.0, 0.0, 12.0, 12.0, 24.0);
        else p.getPoints().addAll(0.0, 0.0, 12.0, 12.0, 0.0, 24.0);
        p.setFill(Theme.textPrimary());
        p.setStroke(Theme.textPrimary());
        p.setStrokeWidth(2);
        b.setGraphic(p);
        return b;
    }

    private void scroll(int dir) {
        currentIndex += dir;
        int max = Math.max(0, contentBox.getChildren().size() - visibleCount);
        if (currentIndex < 0) currentIndex = 0;
        if (currentIndex > max) currentIndex = max;
        for (int i = 0; i < contentBox.getChildren().size(); i++) {
            Node n = contentBox.getChildren().get(i);
            boolean vis = i >= currentIndex && i < currentIndex + visibleCount;
            n.setVisible(vis);
            n.setManaged(vis);
        }
    }

    public void setCards(List<TrackCard> cards) {
        contentBox.getChildren().clear();
        if (cards != null) contentBox.getChildren().addAll(cards);
        currentIndex = 0;
        scroll(0);
    }
}

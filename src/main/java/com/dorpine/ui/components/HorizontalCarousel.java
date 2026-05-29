package com.dorpine.ui.components;

import com.dorpine.util.Theme;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
    private int currentIndex = 0;
    private final int visibleCount;

    public HorizontalCarousel(String title, List<TrackCard> cards, int visibleCount) {
        this.visibleCount = visibleCount;

        VBox container = new VBox(12);
        container.setAlignment(Pos.CENTER);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: " + toHex(Theme.TEXT_PRIMARY) + "; -fx-font-size: 20px; -fx-font-weight: bold;");
        titleLabel.setAlignment(Pos.CENTER);
        container.getChildren().add(titleLabel);

        HBox carouselBox = new HBox();
        carouselBox.setAlignment(Pos.CENTER);
        carouselBox.setSpacing(12);

        Button leftBtn = createArrowButton(true);
        Button rightBtn = createArrowButton(false);

        contentBox = new HBox(12);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new Insets(8));

        if (cards != null) {
            for (TrackCard card : cards) {
                contentBox.getChildren().add(card);
            }
        }

        Region leftSpacer = new Region();
        leftSpacer.setPrefWidth(40);
        Region rightSpacer = new Region();
        rightSpacer.setPrefWidth(40);

        HBox.setHgrow(contentBox, Priority.ALWAYS);

        carouselBox.getChildren().addAll(leftBtn, leftSpacer, contentBox, rightSpacer, rightBtn);
        container.getChildren().add(carouselBox);

        getChildren().add(container);
        setAlignment(Pos.CENTER);

        leftBtn.setOnAction(e -> scroll(-1));
        rightBtn.setOnAction(e -> scroll(1));

        updateVisibility();
    }

    private Button createArrowButton(boolean left) {
        Button btn = new Button();
        btn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-cursor: hand;" +
            "-fx-min-width: 40px;" +
            "-fx-min-height: 40px;"
        );

        Polygon arrow = new Polygon();
        if (left) {
            arrow.getPoints().addAll(12.0, 0.0, 0.0, 12.0, 12.0, 24.0);
        } else {
            arrow.getPoints().addAll(0.0, 0.0, 12.0, 12.0, 0.0, 24.0);
        }
        arrow.setFill(Color.web("#2D3436"));
        arrow.setStroke(Color.web("#2D3436"));
        arrow.setStrokeWidth(2);

        btn.setGraphic(arrow);
        return btn;
    }

    private void scroll(int direction) {
        int maxIndex = Math.max(0, contentBox.getChildren().size() - visibleCount);
        currentIndex += direction;
        if (currentIndex < 0) currentIndex = 0;
        if (currentIndex > maxIndex) currentIndex = maxIndex;
        updateVisibility();
    }

    private void updateVisibility() {
        for (int i = 0; i < contentBox.getChildren().size(); i++) {
            javafx.scene.Node node = contentBox.getChildren().get(i);
            node.setVisible(i >= currentIndex && i < currentIndex + visibleCount);
            node.setManaged(i >= currentIndex && i < currentIndex + visibleCount);
        }
    }

    public void setCards(List<TrackCard> cards) {
        contentBox.getChildren().clear();
        if (cards != null) {
            contentBox.getChildren().addAll(cards);
        }
        currentIndex = 0;
        updateVisibility();
    }

    private static String toHex(Color color) {
        return String.format("#%02X%02X%02X",
            (int) (color.getRed() * 255),
            (int) (color.getGreen() * 255),
            (int) (color.getBlue() * 255));
    }
}

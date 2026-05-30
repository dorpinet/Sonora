package com.dorpine.ui.screens;

import com.dorpine.util.Fonts;
import com.dorpine.util.Theme;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class PianoKeys extends Region {

    private static final int NUM_WHITE = 14;
    private static final int[] BLACK_KEY_INDICES = {0, 1, 3, 4, 5, 7, 8, 10, 11, 12};
    private static final double BLACK_OFFSETS[] = {0.55, 1.55, 3.55, 4.55, 5.55, 7.55, 8.55, 10.55, 11.55, 12.55};

    public PianoKeys() {
        setStyle("-fx-background-color: transparent;");
    }

    @Override
    protected void layoutChildren() {
        double w = getWidth();
        double h = getHeight();
        if (w == 0 || h == 0) return;

        double whiteW = w / NUM_WHITE;
        double blackW = whiteW * 0.65;
        double blackH = h * 0.62;

        getChildren().clear();

        // White keys
        for (int i = 0; i < NUM_WHITE; i++) {
            Rectangle key = new Rectangle(i * whiteW, 0, whiteW - 1, h);
            key.setArcWidth(8);
            key.setArcHeight(8);
            key.setFill(Color.web("#F5F0E8"));
            key.setStroke(Color.web("#D4CFC7"));
            key.setStrokeWidth(0.5);
            getChildren().add(key);
        }

        // Black keys
        for (int i = 0; i < BLACK_OFFSETS.length; i++) {
            double x = BLACK_OFFSETS[i] * whiteW - blackW / 2;
            Rectangle key = new Rectangle(x, 0, blackW, blackH);
            key.setArcWidth(6);
            key.setArcHeight(6);
            key.setFill(Color.web("#1A1A1A"));
            key.setStroke(Color.web("#333333"));
            key.setStrokeWidth(0.5);
            getChildren().add(key);
        }
    }
}

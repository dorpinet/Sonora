package com.dorpine.util;

import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

public class Theme {
    public static final Color BACKGROUND_START = Color.web("#D4C8FF");
    public static final Color BACKGROUND_END = Color.web("#A898FF");
    public static final Color CARD_BG = Color.web("rgba(255,255,255,0.35)");
    public static final Color CARD_BORDER = Color.web("rgba(255,255,255,0.6)");
    public static final Color ACCENT = Color.web("#6C5CE7");
    public static final Color ACCENT_HOVER = Color.web("#5B4BC4");
    public static final Color TEXT_PRIMARY = Color.web("#2D3436");
    public static final Color TEXT_SECONDARY = Color.web("#636E72");
    public static final Color TEXT_LIGHT = Color.web("#FFFFFF");
    public static final Color TOP_BAR_BG = Color.web("#8B7BFF");
    public static final Color DETAILS_PANEL_BG = Color.web("rgba(140,130,230,0.4)");
    public static final Color BUTTON_BG = Color.web("rgba(255,255,255,0.25)");
    public static final Color BUTTON_BORDER = Color.web("rgba(255,255,255,0.5)");

    public static final String CARD_RADIUS = "16px";
    public static final String BUTTON_RADIUS = "24px";
    public static final String TOP_BAR_RADIUS = "28px";

    public static LinearGradient backgroundGradient() {
        return new LinearGradient(
            0, 0, 0, 1, true, null,
            new Stop(0, BACKGROUND_START),
            new Stop(1, BACKGROUND_END)
        );
    }

    public static String toCssColor(Color color) {
        int r = (int) (color.getRed() * 255);
        int g = (int) (color.getGreen() * 255);
        int b = (int) (color.getBlue() * 255);
        double a = color.getOpacity();
        if (a >= 1.0) {
            return String.format("#%02X%02X%02X", r, g, b);
        }
        return String.format("rgba(%d,%d,%d,%.2f)", r, g, b, a).replace("0.", ".").replace("1.00", "1");
    }

    private Theme() {}
}

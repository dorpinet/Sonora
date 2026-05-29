package com.dorpine.util;

import javafx.scene.paint.Color;

public class Theme {

    private static boolean darkMode = false;

    public static boolean isDark() { return darkMode; }

    public static void toggle() {
        darkMode = !darkMode;
    }

    public static Color bgStart()    { return darkMode ? Color.web("#2D1B69") : Color.web("#D4C8FF"); }
    public static Color bgEnd()      { return darkMode ? Color.web("#1a0a3e") : Color.web("#A898FF"); }
    public static Color cardBg()     { return darkMode ? Color.web("rgba(255,255,255,0.08)") : Color.web("rgba(255,255,255,0.3)"); }
    public static Color cardBorder() { return darkMode ? Color.web("rgba(255,255,255,0.15)") : Color.web("rgba(255,255,255,0.5)"); }
    public static Color accent()     { return darkMode ? Color.web("#9B8CFF") : Color.web("#6C5CE7"); }
    public static Color textPrimary(){ return darkMode ? Color.web("#F0F0F0") : Color.web("#000000"); }
    public static Color textSecondary(){ return darkMode ? Color.web("#B0B0B0") : Color.web("#333333"); }
    public static Color topBarBg()   { return darkMode ? Color.web("#3D2B8A") : Color.web("#8B7BFF"); }
    public static Color detailsBg()  { return darkMode ? Color.web("rgba(60,40,120,0.5)") : Color.web("rgba(140,130,230,0.4)"); }
    public static Color btnBg()      { return darkMode ? Color.web("rgba(255,255,255,0.12)") : Color.web("rgba(255,255,255,0.25)"); }
    public static Color btnBorder()  { return darkMode ? Color.web("rgba(255,255,255,0.25)") : Color.web("rgba(255,255,255,0.5)"); }

    public static String GRADIENT_CSS() {
        return String.format("linear-gradient(to bottom, %s, %s)", toCss(bgStart()), toCss(bgEnd()));
    }

    public static String toCss(Color c) {
        int r = (int) (c.getRed() * 255);
        int g = (int) (c.getGreen() * 255);
        int b = (int) (c.getBlue() * 255);
        double a = c.getOpacity();
        if (a >= 1.0) return String.format("#%02X%02X%02X", r, g, b);
        return String.format("rgba(%d,%d,%d,%.2f)", r, g, b, a);
    }

    private Theme() {}
}

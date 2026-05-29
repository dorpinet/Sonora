package com.dorpine.util;

import javafx.scene.paint.Color;

public class Theme {

    private static boolean darkMode = false;

    public static boolean isDark() { return darkMode; }

    public static void toggle() {
        darkMode = !darkMode;
    }

    public static Color bgStart()    { return darkMode ? Color.web("#0a0a12") : Color.web("#E8E0FF"); }
    public static Color bgEnd()      { return darkMode ? Color.web("#1a0a2e") : Color.web("#C4B5FD"); }
    public static Color cardBg()     { return darkMode ? Color.web("rgba(255,255,255,0.06)") : Color.web("rgba(255,255,255,0.5)"); }
    public static Color cardBorder() { return darkMode ? Color.web("rgba(255,255,255,0.12)") : Color.web("rgba(255,255,255,0.6)"); }
    public static Color accent()     { return darkMode ? Color.web("#8B5CF6") : Color.web("#7C3AED"); }
    public static Color textPrimary(){ return darkMode ? Color.web("#FFFFFF") : Color.web("#000000"); }
    public static Color textSecondary(){ return darkMode ? Color.web("#A0A0A0") : Color.web("#333333"); }
    public static Color topBarBg()   { return darkMode ? Color.web("#0f0f1e") : Color.web("#A78BFA"); }
    public static Color detailsBg()  { return darkMode ? Color.web("rgba(30,20,60,0.6)") : Color.web("rgba(140,130,230,0.4)"); }
    public static Color btnBg()      { return darkMode ? Color.web("rgba(139,92,246,0.2)") : Color.web("rgba(124,58,237,0.15)"); }
    public static Color btnBorder()  { return darkMode ? Color.web("rgba(139,92,246,0.4)") : Color.web("rgba(124,58,237,0.3)"); }

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

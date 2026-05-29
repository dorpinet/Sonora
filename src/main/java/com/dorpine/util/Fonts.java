package com.dorpine.util;

import javafx.scene.text.Font;

public class Fonts {
    private static String headingFamily = "System";
    private static String bodyFamily = "System";

    public static void load() {
        try {
            Font headingFont = Font.loadFont(Fonts.class.getResourceAsStream("/fonts/GangeDEMO.otf"), 12);
            Font bodyFont = Font.loadFont(Fonts.class.getResourceAsStream("/fonts/halfre.otf"), 12);

            if (headingFont != null) {
                headingFamily = headingFont.getFamily();
                System.out.println("[Fonts] Heading family: " + headingFamily);
            } else {
                System.err.println("[Fonts] Failed to load heading font");
            }

            if (bodyFont != null) {
                bodyFamily = bodyFont.getFamily();
                System.out.println("[Fonts] Body family: " + bodyFamily);
            } else {
                System.err.println("[Fonts] Failed to load body font");
            }
        } catch (Exception e) {
            System.err.println("[Fonts] Error loading fonts: " + e.getMessage());
        }
    }

    public static Font heading(double size) {
        return Font.font(headingFamily, size);
    }

    public static Font body(double size) {
        return Font.font(bodyFamily, size);
    }
}

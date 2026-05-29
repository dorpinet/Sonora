package com.dorpine.util;

import javafx.scene.text.Font;

public class Fonts {
    private static Font heading;
    private static Font body;

    public static void load() {
        try {
            heading = Font.loadFont(Fonts.class.getResourceAsStream("/fonts/GangeDEMO.otf"), 12);
            body = Font.loadFont(Fonts.class.getResourceAsStream("/fonts/halfre.otf"), 12);
            System.out.println("[Fonts] Loaded heading: " + (heading != null ? heading.getName() : "null"));
            System.out.println("[Fonts] Loaded body: " + (body != null ? body.getName() : "null"));
        } catch (Exception e) {
            System.err.println("[Fonts] Failed to load fonts: " + e.getMessage());
        }
    }

    public static Font heading(double size) {
        return heading != null ? Font.font(heading.getName(), size) : Font.font("System", size);
    }

    public static Font body(double size) {
        return body != null ? Font.font(body.getName(), size) : Font.font("System", size);
    }

    public static String headingFamily() {
        return heading != null ? heading.getFamily() : "System";
    }

    public static String bodyFamily() {
        return body != null ? body.getFamily() : "System";
    }
}

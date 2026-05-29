package com.dorpine.util;

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class FontManager {
    private static Font heading;
    private static Font body;

    public static void load() {
        try {
            heading = Font.loadFont(FontManager.class.getResourceAsStream("/fonts/GangeDEMO.otf"), 12);
        } catch (Exception e) {
            heading = Font.font("System", FontWeight.BOLD, 12);
        }
        try {
            body = Font.loadFont(FontManager.class.getResourceAsStream("/fonts/halfre.otf"), 12);
        } catch (Exception e) {
            body = Font.font("System", 12);
        }
    }

    public static String headingFamily() { return heading != null ? heading.getFamily() : "System"; }
    public static String bodyFamily()   { return body != null ? body.getFamily() : "System"; }

    public static Font heading(double size) {
        return Font.font(headingFamily(), FontWeight.BOLD, size);
    }

    public static Font body(double size) {
        return Font.font(bodyFamily(), size);
    }
}

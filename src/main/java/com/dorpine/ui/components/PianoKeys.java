package com.dorpine.ui.components;

import com.dorpine.util.Theme;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.HashMap;
import java.util.Map;

public class PianoKeys extends Region {

    private static final int NUM_WHITE = 14;
    private static final int[] BLACK_LEFT_WHITE = {0, 1, 3, 4, 5, 7, 8, 10, 11, 12};

    private final Map<String, Integer> keyMap = new HashMap<>();
    private final Rectangle[] whiteKeys = new Rectangle[NUM_WHITE];
    private final Rectangle[] blackKeys = new Rectangle[BLACK_LEFT_WHITE.length];
    private final boolean[] keyPressed = new boolean[NUM_WHITE + BLACK_LEFT_WHITE.length];

    public PianoKeys() {
        setFocusTraversable(true);
        setOnKeyPressed(this::handleKeyPress);
        setOnKeyReleased(this::handleKeyRelease);
        buildKeyMap();
    }

    private void buildKeyMap() {
        // Lower octave (C3-B3): white Z X C V B N M , black S D G H J
        keyMap.put("z", 0);   // C3
        keyMap.put("s", 14);  // C#3
        keyMap.put("x", 1);   // D3
        keyMap.put("d", 15);  // D#3
        keyMap.put("c", 2);   // E3
        keyMap.put("v", 3);   // F3
        keyMap.put("g", 16);  // F#3
        keyMap.put("b", 4);   // G3
        keyMap.put("h", 17);  // G#3
        keyMap.put("n", 5);   // A3
        keyMap.put("j", 18);  // A#3
        keyMap.put("m", 6);   // B3
        keyMap.put(",", 7);   // C4
        // Upper octave (C4-B4): white Q W E R T Y U black 2 3 5 6 7
        keyMap.put("l", 19);  // C#4
        keyMap.put(".", 8);   // D4
        keyMap.put(";", 20);  // D#4
        keyMap.put("/", 9);   // E4
        keyMap.put("q", 10);  // F4
        keyMap.put("2", 21);  // F#4
        keyMap.put("w", 11);  // G4
        keyMap.put("3", 22);  // G#4
        keyMap.put("e", 12);  // A4
        keyMap.put("5", 23);  // A#4
        keyMap.put("r", 13);  // B4
    }

    @Override
    protected void layoutChildren() {
        double w = getWidth();
        double h = getHeight();
        if (w == 0 || h == 0) return;

        getChildren().clear();

        double whiteW = w / NUM_WHITE;
        double blackW = whiteW * 0.58;
        double blackH = h * 0.62;
        double radius = 6;

        // White keys
        for (int i = 0; i < NUM_WHITE; i++) {
            double x = i * whiteW;
            Rectangle key = new Rectangle(x + 0.5, 0, whiteW - 1, h);
            key.setArcWidth(radius * 2);
            key.setArcHeight(radius * 2);
            boolean pressed = i < keyPressed.length && keyPressed[i];
            key.setFill(pressed ? Color.web("#E8E4DC") : Color.web("#F8F4EC"));
            key.setStroke(Color.web("#C8C4BC"));
            key.setStrokeWidth(0.8);
            whiteKeys[i] = key;
            getChildren().add(key);
        }

        // Black keys
        for (int i = 0; i < BLACK_LEFT_WHITE.length; i++) {
            int leftWhite = BLACK_LEFT_WHITE[i];
            double center = (leftWhite + 1) * whiteW;
            double x = center - blackW * 0.58;
            Rectangle key = new Rectangle(x, 0, blackW, blackH);
            key.setArcWidth(radius * 2);
            key.setArcHeight(radius * 2);
            boolean pressed = (NUM_WHITE + i) < keyPressed.length && keyPressed[NUM_WHITE + i];
            key.setFill(pressed ? Color.web("#333333") : Color.web("#151515"));
            key.setStroke(Color.web("#444444"));
            key.setStrokeWidth(0.5);
            blackKeys[i] = key;
            getChildren().add(key);
        }
    }

    private void handleKeyPress(KeyEvent e) {
        String code = e.getText().toLowerCase();
        Integer idx = keyMap.get(code);
        if (idx != null && idx < keyPressed.length) {
            keyPressed[idx] = true;
            requestLayout();
        }
    }

    private void handleKeyRelease(KeyEvent e) {
        String code = e.getText().toLowerCase();
        Integer idx = keyMap.get(code);
        if (idx != null && idx < keyPressed.length) {
            keyPressed[idx] = false;
            requestLayout();
        }
    }
}

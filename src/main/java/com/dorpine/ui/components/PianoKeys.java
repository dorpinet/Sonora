package com.dorpine.ui.components;

import com.dorpine.util.PianoSound;
import com.dorpine.util.Theme;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.HashMap;
import java.util.Map;

public class PianoKeys extends Region {

    private static final int NUM_WHITE = 14;
    private static final int[] BLACK_AFTER_WHITE = {0, 1, 3, 4, 5, 7, 8, 10, 11, 12};
    private static final int[] WHITE_MIDI = {48,50,52,53,55,57,59,60,62,64,65,67,69,71};
    private static final int[] BLACK_MIDI = {49,51,54,56,58,61,63,66,68,70};

    private final Map<String, Integer> whiteKeyMap = new HashMap<>();
    private final Map<String, Integer> blackKeyMap = new HashMap<>();
    private final boolean[] pressed = new boolean[NUM_WHITE + BLACK_AFTER_WHITE.length];

    private Rectangle[] whiteRects = new Rectangle[NUM_WHITE];
    private Rectangle[] blackRects = new Rectangle[BLACK_AFTER_WHITE.length];

    public PianoKeys() {
        setFocusTraversable(true);
        setOnKeyPressed(this::handleKeyPress);
        setOnKeyReleased(this::handleKeyRelease);
        setOnMouseClicked(e -> requestFocus());
        buildMaps();
    }

    private void buildMaps() {
        String[] whiteChars = {"z","x","c","v","b","n","m",",",".","/","q","w","e","r"};
        for (int i = 0; i < whiteChars.length && i < NUM_WHITE; i++) {
            whiteKeyMap.put(whiteChars[i], i);
        }
        String[] blackChars = {"s","d","g","h","j","l",";","2","3","5"};
        for (int i = 0; i < blackChars.length && i < BLACK_AFTER_WHITE.length; i++) {
            blackKeyMap.put(blackChars[i], i);
        }
    }

    @Override
    protected void layoutChildren() {
        double w = getWidth();
        double h = getHeight();
        if (w == 0 || h == 0) return;

        getChildren().clear();
        double whiteW = w / NUM_WHITE;
        double blackW = whiteW * 0.55;
        double blackH = h * 0.60;

        for (int i = 0; i < NUM_WHITE; i++) {
            double x = i * whiteW + 0.5;
            Rectangle r = new Rectangle(x, 0, whiteW - 1, h);
            r.setArcWidth(8); r.setArcHeight(8);
            boolean p = pressed[i];
            r.setFill(p ? Color.web("#E0DCD4") : Color.web("#FAF6EE"));
            r.setStroke(Color.web("#C8C4BC"));
            r.setStrokeWidth(0.6);
            final int idx = i;
            r.setOnMousePressed(ev -> pressWhite(idx));
            r.setOnMouseReleased(ev -> releaseWhite(idx));
            r.setOnMouseEntered(ev -> { if (ev.isPrimaryButtonDown()) pressWhite(idx); });
            r.setOnMouseExited(ev -> { if (ev.isPrimaryButtonDown()) releaseWhite(idx); });
            whiteRects[i] = r;
            getChildren().add(r);
        }

        for (int i = 0; i < BLACK_AFTER_WHITE.length; i++) {
            int after = BLACK_AFTER_WHITE[i];
            double center = (after + 1) * whiteW;
            double x = center - blackW / 2;
            Rectangle r = new Rectangle(x, 0, blackW, blackH);
            r.setArcWidth(6); r.setArcHeight(6);
            boolean p = pressed[NUM_WHITE + i];
            r.setFill(p ? Color.web("#3A3A3A") : Color.web("#151515"));
            r.setStroke(Color.web("#444444"));
            r.setStrokeWidth(0.5);
            final int idx = i;
            r.setOnMousePressed(ev -> pressBlack(idx));
            r.setOnMouseReleased(ev -> releaseBlack(idx));
            r.setOnMouseEntered(ev -> { if (ev.isPrimaryButtonDown()) pressBlack(idx); });
            r.setOnMouseExited(ev -> { if (ev.isPrimaryButtonDown()) releaseBlack(idx); });
            blackRects[i] = r;
            getChildren().add(r);
        }
    }

    private void pressWhite(int i) {
        if (i < 0 || i >= NUM_WHITE) return;
        pressed[i] = true;
        PianoSound.play(WHITE_MIDI[i]);
        requestLayout();
    }

    private void releaseWhite(int i) {
        if (i < 0 || i >= NUM_WHITE) return;
        pressed[i] = false;
        PianoSound.stop(WHITE_MIDI[i]);
        requestLayout();
    }

    private void pressBlack(int i) {
        if (i < 0 || i >= BLACK_AFTER_WHITE.length) return;
        pressed[NUM_WHITE + i] = true;
        PianoSound.play(BLACK_MIDI[i]);
        requestLayout();
    }

    private void releaseBlack(int i) {
        if (i < 0 || i >= BLACK_AFTER_WHITE.length) return;
        pressed[NUM_WHITE + i] = false;
        PianoSound.stop(BLACK_MIDI[i]);
        requestLayout();
    }

    private void handleKeyPress(KeyEvent e) {
        String code = e.getText().toLowerCase();
        Integer wk = whiteKeyMap.get(code);
        if (wk != null) {
            pressWhite(wk);
            return;
        }
        Integer bk = blackKeyMap.get(code);
        if (bk != null) {
            pressBlack(bk);
        }
    }

    private void handleKeyRelease(KeyEvent e) {
        String code = e.getText().toLowerCase();
        Integer wk = whiteKeyMap.get(code);
        if (wk != null) {
            releaseWhite(wk);
            return;
        }
        Integer bk = blackKeyMap.get(code);
        if (bk != null) {
            releaseBlack(bk);
        }
    }
}

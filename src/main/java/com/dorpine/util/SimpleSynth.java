package com.dorpine.util;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

public class SimpleSynth {
    private static final int SAMPLE_RATE = 44100;
    private static final Map<Integer, Clip> clips = new HashMap<>();

    public static void play(int midiNote) {
        try {
            Clip clip = clips.computeIfAbsent(midiNote, n -> {
                try {
                    double freq = 440.0 * Math.pow(2.0, (n - 69) / 12.0);
                    int duration = (int)(SAMPLE_RATE * 0.5);
                    byte[] buffer = new byte[duration * 2];
                    for (int i = 0; i < duration; i++) {
                        double t = i / (double)SAMPLE_RATE;
                        double env = Math.exp(-t * 5.0);
                        short val = (short)(Math.sin(2 * Math.PI * freq * t) * env * 10000);
                        buffer[i * 2] = (byte)(val & 0xFF);
                        buffer[i * 2 + 1] = (byte)((val >> 8) & 0xFF);
                    }
                    AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 1, true, false);
                    ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
                    AudioInputStream ais = new AudioInputStream(bais, format, duration);
                    Clip c = AudioSystem.getClip();
                    c.open(ais);
                    return c;
                } catch (Exception e) {
                    return null;
                }
            });
            if (clip != null) {
                clip.setFramePosition(0);
                clip.start();
            }
        } catch (Exception e) {
            System.err.println("[SimpleSynth] Play error: " + e.getMessage());
        }
    }

    public static void stop(int midiNote) {
        Clip clip = clips.get(midiNote);
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

    public static boolean isAvailable() {
        try {
            AudioSystem.getClip();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

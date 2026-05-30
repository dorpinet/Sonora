package com.dorpine.util;

public class PianoSound {

    public static void play(int midiNote) {
        SimpleSynth.play(midiNote);
    }

    public static void stop(int midiNote) {
        SimpleSynth.stop(midiNote);
    }

    public static boolean isAvailable() {
        return SimpleSynth.isAvailable();
    }
}

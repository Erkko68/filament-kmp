package io.github.erkko68.filament;

public class Filament {
    private static boolean sInitialized = false;

    public static synchronized void init() {
        if (!sInitialized) {
            System.loadLibrary("filament-jni");
            sInitialized = true;
        }
    }
}

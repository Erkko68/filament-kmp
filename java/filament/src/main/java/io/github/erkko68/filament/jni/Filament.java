package io.github.erkko68.filament.jni;

import io.github.erkko68.filament.jni.internal.NativeLoader;

public class Filament {
    private static boolean sInitialized = false;

    public static synchronized void init() {
        if (!sInitialized) {
            NativeLoader.load("filament-jni");
            sInitialized = true;
        }
    }
}


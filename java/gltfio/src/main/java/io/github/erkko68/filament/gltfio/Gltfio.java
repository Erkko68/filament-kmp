package io.github.erkko68.filament.gltfio;

import io.github.erkko68.filament.Filament;

public class Gltfio {
    private static boolean sInitialized = false;

    public static synchronized void init() {
        if (!sInitialized) {
            Filament.init();
            System.loadLibrary("gltfio-jni");
            sInitialized = true;
        }
    }
}

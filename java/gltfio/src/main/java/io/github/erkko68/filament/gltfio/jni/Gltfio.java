package io.github.erkko68.filament.gltfio.jni;

import io.github.erkko68.filament.jni.Filament;

public class Gltfio {
    private static boolean sInitialized = false;

    public static synchronized void init() {
        if (sInitialized) return;
        // All JNI symbols (filament, filamat, gltfio, filament-utils) live in a single
        // combined dylib loaded by Filament.init().
        Filament.init();
        sInitialized = true;
    }
}

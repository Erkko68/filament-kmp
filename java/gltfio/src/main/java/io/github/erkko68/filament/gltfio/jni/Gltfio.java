package io.github.erkko68.filament.gltfio.jni;

import io.github.erkko68.filament.jni.Filament;
import io.github.erkko68.filament.jni.internal.NativeLoader;

public class Gltfio {
    private static boolean sInitialized = false;

    public static synchronized void init() {
        if (sInitialized) return;
        Filament.init();
        NativeLoader.load("gltfio-jni");
        sInitialized = true;
    }
}



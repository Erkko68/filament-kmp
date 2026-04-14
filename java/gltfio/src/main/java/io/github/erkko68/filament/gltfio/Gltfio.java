package io.github.erkko68.filament.gltfio;

import io.github.erkko68.filament.Filament;

public class Gltfio {
    public static void init() {
        Filament.init();
        System.loadLibrary("gltfio-jni");
    }
}


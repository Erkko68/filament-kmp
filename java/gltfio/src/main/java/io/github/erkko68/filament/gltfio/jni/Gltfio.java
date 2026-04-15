package io.github.erkko68.filament.gltfio.jni;

import io.github.erkko68.filament.jni.Filament;

public class Gltfio {
    public static void init() {
        Filament.init();
        System.loadLibrary("gltfio-jni");
    }
}


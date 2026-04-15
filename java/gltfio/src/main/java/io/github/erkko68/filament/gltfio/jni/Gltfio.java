package io.github.erkko68.filament.gltfio.jni;

import io.github.erkko68.filament.jni.Filament;
import io.github.erkko68.filament.jni.internal.NativeLoader;

public class Gltfio {
    public static void init() {
        Filament.init();
        NativeLoader.load("gltfio-jni");
    }
}



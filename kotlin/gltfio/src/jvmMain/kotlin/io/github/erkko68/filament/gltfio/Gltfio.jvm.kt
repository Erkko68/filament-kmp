package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.gltfio.jni.Gltfio as JniGltfio

actual object Gltfio {
    actual fun init() {
        JniGltfio.init()
    }
}
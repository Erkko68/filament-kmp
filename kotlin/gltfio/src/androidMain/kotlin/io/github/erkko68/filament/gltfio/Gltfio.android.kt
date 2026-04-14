package io.github.erkko68.filament.gltfio

actual object Gltfio {
    actual fun init() {
        com.google.android.filament.gltfio.Gltfio.init()
    }
}

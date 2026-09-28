package io.github.erkko68.filament.gltfio

actual object Gltfio {
    actual fun init() {
        // gltfio lives in libfilament-c, which loads with the first Fila* call; nothing else to set up.
    }
}

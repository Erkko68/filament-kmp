package io.github.erkko68.filament.gltfio

actual object Gltfio {
    actual fun init() {
        // Native initialization is handled by static linking or 
        // calling Filament.init() which is already handled in Engine.create()
    }
}

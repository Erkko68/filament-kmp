package io.github.erkko68.filament.gltfio

expect object Gltfio {
    /**
     * Initializes the gltfio JNI layer on Android and sets up any necessary
     * native state on other platforms. Must be called before using any
     * gltfio functionality.
     */
    fun init()
}

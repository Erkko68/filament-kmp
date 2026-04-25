package io.github.erkko68.filament.utils

actual object HDRLoader {
    actual fun createTexture(
        engine: io.github.erkko68.filament.Engine,
        buffer: ByteArray,
        internalFormat: io.github.erkko68.filament.Texture.InternalFormat
    ): io.github.erkko68.filament.Texture? {
        // HDR (Radiance/Rgbe) format loading is not directly supported in the JS Filament bindings.
        // Consider converting HDR files to KTX2 format for use in web applications.
        return null
    }
}
package io.github.erkko68.filament.utils

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Texture

expect object HDRLoader {
    fun createTexture(engine: Engine, buffer: ByteArray, internalFormat: Texture.InternalFormat): Texture?
}

package dev.filament.kmp.utils

import dev.filament.kmp.Engine
import dev.filament.kmp.Texture

expect object HDRLoader {
    fun createTexture(engine: Engine, buffer: ByteArray, internalFormat: Texture.InternalFormat): Texture?
}

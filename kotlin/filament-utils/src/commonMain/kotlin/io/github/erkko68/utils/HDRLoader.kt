package io.github.erkko68.utils

import io.github.erkko68.Engine
import io.github.erkko68.Texture

expect object HDRLoader {
    fun createTexture(engine: Engine, buffer: ByteArray, internalFormat: Texture.InternalFormat): Texture?
}

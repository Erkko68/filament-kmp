package io.github.erkko68.filament.utils

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Texture

actual object TextureLoader {
    actual fun loadTexture(
        engine: Engine,
        buffer: ByteArray,
        type: TextureType
    ): Texture? {
        TODO("Not yet implemented")
    }

    actual enum class TextureType { COLOR, NORMAL, DATA }
}
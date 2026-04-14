package io.github.erkko68.filament.utils

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Texture

expect object TextureLoader {
    enum class TextureType {
        COLOR,
        NORMAL,
        DATA
    }

    fun loadTexture(engine: Engine, buffer: ByteArray, type: TextureType): Texture?
}

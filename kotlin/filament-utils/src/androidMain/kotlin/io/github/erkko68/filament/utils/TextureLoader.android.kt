package io.github.erkko68.filament.utils

import com.google.android.filament.utils.TextureType as GoogleTextureType
import com.google.android.filament.utils.loadTexture as googleLoadTexture
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Texture

actual object TextureLoader {
    actual enum class TextureType {
        COLOR,
        NORMAL,
        DATA
    }

    actual fun loadTexture(engine: Engine, buffer: ByteArray, type: TextureType): Texture? {
        val googleType = GoogleTextureType.entries[type.ordinal]
        return Texture(googleLoadTexture(
            engine.nativeEngine,
            buffer,
            googleType
        ))
    }
}

@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package io.github.erkko68.filament.utils

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Texture
import io.github.erkko68.filament.utils.cinterop.*
import kotlinx.cinterop.*
import io.github.erkko68.filament.FilamentPlatform
import io.github.erkko68.filament.PlatformGap
import io.github.erkko68.filament.nativeObject

actual object HDRLoader {
    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "throws UnsupportedOperationException — filament.js exposes no Radiance/RGBE decoder.")
    actual fun createTexture(engine: Engine, buffer: ByteArray, internalFormat: Texture.InternalFormat): Texture? {
        val handle = buffer.usePinned { pinned ->
            FilaHDRLoader_createTexture(
                engine.nativeObject,
                pinned.addressOf(0),
                buffer.size.toULong(),
                internalFormat.ordinal
            )
        }
        return handle?.let { Texture(it) }
    }
}

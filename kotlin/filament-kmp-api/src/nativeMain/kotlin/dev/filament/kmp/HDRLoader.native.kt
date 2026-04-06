package dev.filament.kmp

import kotlinx.cinterop.*
import filament.*

actual object HDRLoader {
    actual class Options {
        actual var desiredFormat: Texture.InternalFormat = Texture.InternalFormat.RGBA8
    }

    actual fun createTexture(engine: Engine, buffer: Buffer, options: Options): Texture? {
        val nativeTexture = FilaHDRLoader_createTexture(
            engine.nativeObject,
            buffer.readDirect(),
            buffer.remaining().toULong(),
            options.desiredFormat.toNative().value.toInt()
        ) ?: return null
        return Texture(nativeTexture)
    }
}

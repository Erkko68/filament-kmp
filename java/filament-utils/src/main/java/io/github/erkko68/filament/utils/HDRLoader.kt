package io.github.erkko68.filament.utils

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Texture
import java.nio.Buffer

object HDRLoader {
    class Options {
        var desiredFormat = Texture.InternalFormat.RGB16F
    }

    fun createTexture(engine: Engine, buffer: Buffer, options: Options = Options()): Texture? {
        val nativeEngine = engine.nativeObject
        val nativeTexture = nCreateHDRTexture(nativeEngine, buffer, buffer.remaining(), options.desiredFormat.ordinal)
        if (nativeTexture == 0L) {
            return null
        }
        return Texture(nativeTexture)
    }

    private external fun nCreateHDRTexture(nativeEngine: Long, buffer: Buffer, remaining: Int, format: Int): Long
}

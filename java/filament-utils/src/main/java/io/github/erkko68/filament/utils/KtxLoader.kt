package io.github.erkko68.filament.utils

import io.github.erkko68.filament.*
import java.nio.Buffer

class KtxLoader {
    companion object {
        @JvmStatic
        fun createTexture(engine: Engine, buffer: Buffer, srgb: Boolean): Texture? {
            val nativeTexture = nCreateTexture(engine.nativeObject, buffer, buffer.remaining(), srgb)
            return if (nativeTexture != 0L) Texture(nativeTexture) else null
        }

        @JvmStatic
        fun createIndirectLight(engine: Engine, buffer: Buffer): IndirectLight? {
            val nativeIndirectLight = nCreateIndirectLight(engine.nativeObject, buffer, buffer.remaining())
            return if (nativeIndirectLight != 0L) IndirectLight(nativeIndirectLight) else null
        }

        @JvmStatic
        fun createSkybox(engine: Engine, buffer: Buffer): Skybox? {
            val nativeSkybox = nCreateSkybox(engine.nativeObject, buffer, buffer.remaining())
            return if (nativeSkybox != 0L) Skybox(nativeSkybox) else null
        }

        @JvmStatic
        private external fun nCreateTexture(nativeEngine: Long, buffer: Buffer, remaining: Int, srgb: Boolean): Long

        @JvmStatic
        private external fun nCreateIndirectLight(nativeEngine: Long, buffer: Buffer, remaining: Int): Long

        @JvmStatic
        private external fun nCreateSkybox(nativeEngine: Long, buffer: Buffer, remaining: Int): Long
    }
}

package io.github.erkko68.filament.utils

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.InternalFilamentApi
import io.github.erkko68.filament.Texture
import io.github.erkko68.filament.interop.*
import io.github.erkko68.filament.utils.capi.*

/**
 * Decodes an HDR image from raw bytes into a Filament [Texture].
 */
object HDRLoader {
    /**
     * Decodes HDR bytes into a Filament [Texture].
     *
     * @param engine the [Engine] to create the texture with
     * @param buffer the raw HDR image data
     * @param internalFormat the internal format to use for the resulting texture
     * @return the created [Texture], or null on failure
     */
    fun createTexture(engine: Engine, buffer: ByteArray, internalFormat: Texture.InternalFormat): Texture? =
        decode(buffer)?.createTexture(engine, internalFormat)

    /** Decodes HDR bytes without an [Engine], so on any thread; null on failure. */
    @InternalFilamentApi
    fun decode(buffer: ByteArray): DecodedImage? =
        DecodedImage.of(buffer.usePinned(buffer.size) { FilaHDRLoader_decode(it, buffer.size) })
}

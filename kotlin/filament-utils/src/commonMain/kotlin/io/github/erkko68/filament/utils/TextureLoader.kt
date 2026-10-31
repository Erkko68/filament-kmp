package io.github.erkko68.filament.utils

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.InternalFilamentApi
import io.github.erkko68.filament.Texture
import io.github.erkko68.filament.interop.*
import io.github.erkko68.filament.utils.capi.*

/**
 * Loads common image formats (PNG, JPG, etc.) into a Filament [Texture].
 */
object TextureLoader {
    /**
     * Specifies the intended use of the texture data, which influences the internal format selected.
     */
    enum class TextureType {
        /** sRGB color data. */
        COLOR,
        /** Linear normal map data. */
        NORMAL,
        /** Linear non-color data. */
        DATA
    }

    /**
     * Decodes image bytes (PNG, JPG, etc.) into a Filament [Texture].
     *
     * @param engine the [Engine] to create the texture with
     * @param buffer the raw encoded image data
     * @param type the intended use of the texture, which influences the internal format selected
     * @return the created [Texture], or null on failure
     */
    fun loadTexture(engine: Engine, buffer: ByteArray, type: TextureType): Texture? =
        decode(buffer)?.let { createTexture(engine, it, type) }

    /** Decodes image bytes without an [Engine], so on any thread; null on failure. Finish with [createTexture]. */
    @InternalFilamentApi
    fun decode(buffer: ByteArray): DecodedImage? =
        DecodedImage.of(buffer.usePinned(buffer.size) { FilaTextureLoader_decode(it, buffer.size) })

    /** Uploads [image] as [type], on the engine's thread; null on failure. */
    @InternalFilamentApi
    fun createTexture(engine: Engine, image: DecodedImage, type: TextureType): Texture? =
        image.createTexture(engine, if (type == TextureType.COLOR) Texture.InternalFormat.SRGB8_A8 else Texture.InternalFormat.RGBA8)
}

/**
 * An image decoded on any thread and not on the GPU yet: the slow half of [TextureLoader.loadTexture] and
 * [HDRLoader.createTexture]. Holds native memory until uploaded or closed.
 */
@InternalFilamentApi
class DecodedImage private constructor(private var nativeHandle: NativePointer) : AutoCloseable {
    /** Uploads the image into a mipmapped 2D texture and frees it, on the engine's thread; null if that fails or it's gone. */
    fun createTexture(engine: Engine, internalFormat: Texture.InternalFormat): Texture? {
        if (nativeHandle == NullPointer) return null
        val handle = FilaDecodedImage_createTexture(nativeHandle, engine.nativeObject, internalFormat.ordinal)
        nativeHandle = NullPointer
        return handle.takeIf { it != NullPointer }?.let { Texture(it) }
    }

    /** Frees the image, unless [createTexture] took it. */
    override fun close() { nativeHandle = nativeHandle.destroyOnce { FilaDecodedImage_destroy(it) } }

    internal companion object {
        fun of(handle: NativePointer): DecodedImage? = handle.takeIf { it != NullPointer }?.let(::DecodedImage)
    }
}

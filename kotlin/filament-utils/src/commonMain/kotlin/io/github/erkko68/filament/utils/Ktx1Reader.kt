package io.github.erkko68.filament.utils

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Texture
import io.github.erkko68.filament.interop.*
import io.github.erkko68.filament.utils.capi.*

/** Creates Filament textures from KTX1 bundles. */
object Ktx1Reader {
    /**
     * Creates a Texture object from a KTX file and populates all of its faces and miplevels.
     *
     * [ktx] must stay alive until [callback] runs, once all texture data has been uploaded to the GPU.
     *
     * @param srgb requests an sRGB format from the KTX file
     */
    fun createTexture(engine: Engine, ktx: Ktx1Bundle, srgb: Boolean, callback: () -> Unit): Texture? {
        val user = Callbacks.register(once = true) { callback() }
        val handle = FilaKtxreaderKtx1Reader_createTexture_Callback_void(engine.nativeObject, ktx.nativeObject, srgb, Callbacks.userOnly, user)
        if (handle == NullPointer) Callbacks.release(user)
        return handle.takeIf { it != NullPointer }?.let { Texture(it) }
    }

    /**
     * Creates a Texture object from a KTX file and populates all of its faces and miplevels.
     *
     * Takes [ktx]: it's destroyed after all the texture data has been uploaded, and can't be used afterwards.
     *
     * @param srgb requests an sRGB format from the KTX file
     */
    fun createTexture(engine: Engine, ktx: Ktx1Bundle, srgb: Boolean): Texture? =
        FilaKtxreaderKtx1Reader_createTexture(engine.nativeObject, ktx.release(), srgb).takeIf { it != NullPointer }?.let { Texture(it) }

    /** Whether [format] is an sRGB one. */
    fun isSrgbTextureFormat(format: Texture.InternalFormat): Boolean = FilaKtxreaderKtx1Reader_isSrgbTextureFormat(format.ordinal)
}

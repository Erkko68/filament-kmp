package io.github.erkko68.filament

import io.github.erkko68.filament.wasm.*

actual class RenderTarget @InternalFilamentApi constructor(internal var nativeHandle: Int, private val textures: Array<Texture?>) {
    actual enum class AttachmentPoint {
        COLOR, COLOR1, COLOR2, COLOR3, COLOR4, COLOR5, COLOR6, COLOR7, DEPTH
    }

    actual class Builder actual constructor() {
        private val nativeBuilder = FilaRenderTargetBuilder_create()
        private val textures = arrayOfNulls<Texture>(AttachmentPoint.entries.size)

        actual fun texture(attachment: AttachmentPoint, texture: Texture?): Builder {
            textures[attachment.ordinal] = texture
            FilaRenderTargetBuilder_texture(nativeBuilder, attachment.ordinal, texture?.nativeHandle ?: 0)
            return this
        }

        actual fun mipLevel(attachment: AttachmentPoint, level: Int): Builder {
            FilaRenderTargetBuilder_mipLevel(nativeBuilder, attachment.ordinal, level)
            return this
        }

        actual fun face(attachment: AttachmentPoint, face: Texture.CubemapFace): Builder {
            FilaRenderTargetBuilder_face(nativeBuilder, attachment.ordinal, face.ordinal)
            return this
        }

        actual fun layer(attachment: AttachmentPoint, layer: Int): Builder {
            FilaRenderTargetBuilder_layer(nativeBuilder, attachment.ordinal, layer)
            return this
        }

        actual fun build(engine: Engine): RenderTarget {
            val handle = FilaRenderTargetBuilder_build(nativeBuilder, engine.nativeHandle)
            FilaRenderTargetBuilder_destroy(nativeBuilder)
            return RenderTarget(handle, textures.copyOf())
        }
    }

    actual fun getTexture(attachment: AttachmentPoint): Texture? = textures[attachment.ordinal]

    actual fun getMipLevel(attachment: AttachmentPoint): Int =
        FilaRenderTarget_getMipLevel(nativeHandle, attachment.ordinal).toInt()

    actual fun getFace(attachment: AttachmentPoint): Texture.CubemapFace =
        Texture.CubemapFace.entries[FilaRenderTarget_getFace(nativeHandle, attachment.ordinal).toInt()]

    actual fun getLayer(attachment: AttachmentPoint): Int =
        FilaRenderTarget_getLayer(nativeHandle, attachment.ordinal).toInt()
}

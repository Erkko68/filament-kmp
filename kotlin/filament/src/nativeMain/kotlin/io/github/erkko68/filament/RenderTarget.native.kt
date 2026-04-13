@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package io.github.erkko68.filament

import kotlinx.cinterop.*
import io.github.erkko68.filament.cinterop.*
import cnames.structs.FilaRenderTarget

actual class RenderTarget internal constructor(internal var nativeHandle: CPointer<FilaRenderTarget>?, private val textures: Array<Texture?>) {
    actual enum class AttachmentPoint {
        COLOR, COLOR1, COLOR2, COLOR3, COLOR4, COLOR5, COLOR6, COLOR7, DEPTH
    }

    actual class Builder actual constructor() {
        private val nativeBuilder = FilaRenderTargetBuilder_create()
        private val textures = arrayOfNulls<Texture>(AttachmentPoint.values().size)

        actual fun texture(attachment: AttachmentPoint, texture: Texture?): Builder {
            textures[attachment.ordinal] = texture
            FilaRenderTargetBuilder_texture(nativeBuilder, attachment.ordinal.toUInt(), texture?.nativeHandle)
            return this
        }

        actual fun mipLevel(attachment: AttachmentPoint, level: Int): Builder {
            FilaRenderTargetBuilder_mipLevel(nativeBuilder, attachment.ordinal.toUInt(), level.toUByte())
            return this
        }

        actual fun face(attachment: AttachmentPoint, face: Texture.CubemapFace): Builder {
            FilaRenderTargetBuilder_face(nativeBuilder, attachment.ordinal.toUInt(), face.ordinal.toUInt())
            return this
        }

        actual fun layer(attachment: AttachmentPoint, layer: Int): Builder {
            FilaRenderTargetBuilder_layer(nativeBuilder, attachment.ordinal.toUInt(), layer.toUInt())
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
        FilaRenderTarget_getMipLevel(nativeHandle, attachment.ordinal.toUInt()).toInt()

    actual fun getFace(attachment: AttachmentPoint): Texture.CubemapFace =
        Texture.CubemapFace.values()[FilaRenderTarget_getFace(nativeHandle, attachment.ordinal.toUInt()).toInt()]

    actual fun getLayer(attachment: AttachmentPoint): Int =
        FilaRenderTarget_getLayer(nativeHandle, attachment.ordinal.toUInt()).toInt()
}

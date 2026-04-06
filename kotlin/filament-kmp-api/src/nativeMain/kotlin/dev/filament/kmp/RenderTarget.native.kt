package dev.filament.kmp

import kotlinx.cinterop.*
import filament.*

actual class RenderTarget internal constructor(
    internal val nativeObject: CPointer<FilaRenderTarget>
) {
    actual enum class AttachmentPoint {
        COLOR0, COLOR1, COLOR2, COLOR3, COLOR4, COLOR5, COLOR6, COLOR7, DEPTH;

        internal fun toNative(): FilaRenderTargetAttachmentPoint = when (this) {
            COLOR0 -> FILA_RENDER_TARGET_ATTACHMENT_POINT_COLOR0
            COLOR1 -> FILA_RENDER_TARGET_ATTACHMENT_POINT_COLOR1
            COLOR2 -> FILA_RENDER_TARGET_ATTACHMENT_POINT_COLOR2
            COLOR3 -> FILA_RENDER_TARGET_ATTACHMENT_POINT_COLOR3
            COLOR4 -> FILA_RENDER_TARGET_ATTACHMENT_POINT_COLOR4
            COLOR5 -> FILA_RENDER_TARGET_ATTACHMENT_POINT_COLOR5
            COLOR6 -> FILA_RENDER_TARGET_ATTACHMENT_POINT_COLOR6
            COLOR7 -> FILA_RENDER_TARGET_ATTACHMENT_POINT_COLOR7
            DEPTH -> FILA_RENDER_TARGET_ATTACHMENT_POINT_DEPTH
        }

        internal companion object {
            fun fromNative(v: FilaRenderTargetAttachmentPoint) = when (v) {
                FILA_RENDER_TARGET_ATTACHMENT_POINT_COLOR0 -> COLOR0
                FILA_RENDER_TARGET_ATTACHMENT_POINT_COLOR1 -> COLOR1
                FILA_RENDER_TARGET_ATTACHMENT_POINT_COLOR2 -> COLOR2
                FILA_RENDER_TARGET_ATTACHMENT_POINT_COLOR3 -> COLOR3
                FILA_RENDER_TARGET_ATTACHMENT_POINT_COLOR4 -> COLOR4
                FILA_RENDER_TARGET_ATTACHMENT_POINT_COLOR5 -> COLOR5
                FILA_RENDER_TARGET_ATTACHMENT_POINT_COLOR6 -> COLOR6
                FILA_RENDER_TARGET_ATTACHMENT_POINT_COLOR7 -> COLOR7
                FILA_RENDER_TARGET_ATTACHMENT_POINT_DEPTH -> DEPTH
                else -> COLOR0
            }
        }
    }

    actual class Builder actual constructor() {
        private val nativeBuilder = FilaRenderTargetBuilder_create()!!

        actual fun texture(attachment: AttachmentPoint, texture: Texture?): Builder {
            FilaRenderTargetBuilder_texture(nativeBuilder, attachment.toNative(), texture?.nativeObject)
            return this
        }

        actual fun mipLevel(attachment: AttachmentPoint, level: Int): Builder {
            FilaRenderTargetBuilder_mipLevel(nativeBuilder, attachment.toNative(), level.toUByte())
            return this
        }

        actual fun face(attachment: AttachmentPoint, face: Texture.CubemapFace): Builder {
            val nativeFace = when (face) {
                Texture.CubemapFace.POSITIVE_X -> FILA_RENDER_TARGET_CUBEMAP_FACE_POSITIVE_X
                Texture.CubemapFace.NEGATIVE_X -> FILA_RENDER_TARGET_CUBEMAP_FACE_NEGATIVE_X
                Texture.CubemapFace.POSITIVE_Y -> FILA_RENDER_TARGET_CUBEMAP_FACE_POSITIVE_Y
                Texture.CubemapFace.NEGATIVE_Y -> FILA_RENDER_TARGET_CUBEMAP_FACE_NEGATIVE_Y
                Texture.CubemapFace.POSITIVE_Z -> FILA_RENDER_TARGET_CUBEMAP_FACE_POSITIVE_Z
                Texture.CubemapFace.NEGATIVE_Z -> FILA_RENDER_TARGET_CUBEMAP_FACE_NEGATIVE_Z
            }
            FilaRenderTargetBuilder_face(nativeBuilder, attachment.toNative(), nativeFace)
            return this
        }

        actual fun layer(attachment: AttachmentPoint, layer: Int): Builder {
            FilaRenderTargetBuilder_layer(nativeBuilder, attachment.toNative(), layer.toUInt())
            return this
        }

        actual fun samples(samples: Int): Builder {
            FilaRenderTargetBuilder_samples(nativeBuilder, samples.toUByte())
            return this
        }

        actual fun build(engine: Engine): RenderTarget {
            val nativeRenderTarget = FilaRenderTargetBuilder_build(nativeBuilder, engine.nativeObject)
                ?: throw IllegalStateException("Couldn't create RenderTarget")
            FilaRenderTargetBuilder_destroy(nativeBuilder)
            return RenderTarget(nativeRenderTarget)
        }
    }

    actual fun getTexture(attachment: AttachmentPoint): Texture? {
        val nativeTexture = FilaRenderTarget_getTexture(nativeObject, attachment.toNative()) ?: return null
        return Texture(nativeTexture)
    }

    actual fun getMipLevel(attachment: AttachmentPoint): Int = FilaRenderTarget_getMipLevel(nativeObject, attachment.toNative()).toInt()

    actual fun getFace(attachment: AttachmentPoint): Texture.CubemapFace {
        val nativeFace = FilaRenderTarget_getFace(nativeObject, attachment.toNative())
        return when (nativeFace) {
            FILA_RENDER_TARGET_CUBEMAP_FACE_POSITIVE_X -> Texture.CubemapFace.POSITIVE_X
            FILA_RENDER_TARGET_CUBEMAP_FACE_NEGATIVE_X -> Texture.CubemapFace.NEGATIVE_X
            FILA_RENDER_TARGET_CUBEMAP_FACE_POSITIVE_Y -> Texture.CubemapFace.POSITIVE_Y
            FILA_RENDER_TARGET_CUBEMAP_FACE_NEGATIVE_Y -> Texture.CubemapFace.NEGATIVE_Y
            FILA_RENDER_TARGET_CUBEMAP_FACE_POSITIVE_Z -> Texture.CubemapFace.POSITIVE_Z
            FILA_RENDER_TARGET_CUBEMAP_FACE_NEGATIVE_Z -> Texture.CubemapFace.NEGATIVE_Z
            else -> Texture.CubemapFace.POSITIVE_X
        }
    }

    actual fun getLayer(attachment: AttachmentPoint): Int = FilaRenderTarget_getLayer(nativeObject, attachment.toNative()).toInt()

    actual val supportedColorAttachmentsCount: Int get() = FilaRenderTarget_getSupportedColorAttachmentsCount(nativeObject).toInt()

    actual val nativeObject: Long get() = nativeObject.toLong()
}

package io.github.erkko68.filament

import io.github.erkko68.filament.js.RenderTarget as JSRenderTarget
import io.github.erkko68.filament.js.`RenderTarget_Builder` as JSRenderTargetBuilder
import io.github.erkko68.filament.js.RenderTarget_AttachmentPoint
import io.github.erkko68.filament.js.Texture_CubemapFace

actual class RenderTarget internal constructor(
    internal val jsRenderTarget: JSRenderTarget,
    private val engine: Engine? = null,
) {
    actual fun getTexture(attachment: AttachmentPoint): Texture? {
        return jsRenderTarget.getTexture(mapAttachment(attachment))
            .let { Texture(it).also { t -> t.engine = engine } }
    }

    actual fun getMipLevel(attachment: AttachmentPoint): Int {
        return jsRenderTarget.getMipLevel(mapAttachment(attachment)).toInt()
    }

    actual fun getFace(attachment: AttachmentPoint): Texture.CubemapFace {
        return when (jsRenderTarget.getFace(mapAttachment(attachment))) {
            Texture_CubemapFace.POSITIVE_X -> Texture.CubemapFace.POSITIVE_X
            Texture_CubemapFace.NEGATIVE_X -> Texture.CubemapFace.NEGATIVE_X
            Texture_CubemapFace.POSITIVE_Y -> Texture.CubemapFace.POSITIVE_Y
            Texture_CubemapFace.NEGATIVE_Y -> Texture.CubemapFace.NEGATIVE_Y
            Texture_CubemapFace.POSITIVE_Z -> Texture.CubemapFace.POSITIVE_Z
            Texture_CubemapFace.NEGATIVE_Z -> Texture.CubemapFace.NEGATIVE_Z
        }
    }

    actual fun getLayer(attachment: AttachmentPoint): Int {
        return jsRenderTarget.getLayer(mapAttachment(attachment)).toInt()
    }

    private fun mapAttachment(attachment: AttachmentPoint): RenderTarget_AttachmentPoint {
        return when (attachment) {
            AttachmentPoint.COLOR -> RenderTarget_AttachmentPoint.COLOR
            AttachmentPoint.DEPTH -> RenderTarget_AttachmentPoint.DEPTH
            else -> RenderTarget_AttachmentPoint.COLOR
        }
    }

    actual enum class AttachmentPoint { COLOR, COLOR1, COLOR2, COLOR3, COLOR4, COLOR5, COLOR6, COLOR7, DEPTH }
    actual class Builder {
        private val jsBuilder = JSRenderTarget.Builder()

        actual fun texture(
            attachment: AttachmentPoint,
            texture: Texture?
        ): Builder {
            if (texture != null) {
                jsBuilder.texture(mapAttachment(attachment), texture.jsTexture)
            }
            return this
        }

        actual fun mipLevel(
            attachment: AttachmentPoint,
            level: Int
        ): Builder {
            jsBuilder.mipLevel(mapAttachment(attachment), level)
            return this
        }

        actual fun face(
            attachment: AttachmentPoint,
            face: Texture.CubemapFace
        ): Builder {
            val jsFace = when (face) {
                Texture.CubemapFace.POSITIVE_X -> Texture_CubemapFace.POSITIVE_X
                Texture.CubemapFace.NEGATIVE_X -> Texture_CubemapFace.NEGATIVE_X
                Texture.CubemapFace.POSITIVE_Y -> Texture_CubemapFace.POSITIVE_Y
                Texture.CubemapFace.NEGATIVE_Y -> Texture_CubemapFace.NEGATIVE_Y
                Texture.CubemapFace.POSITIVE_Z -> Texture_CubemapFace.POSITIVE_Z
                Texture.CubemapFace.NEGATIVE_Z -> Texture_CubemapFace.NEGATIVE_Z
            }
            jsBuilder.face(mapAttachment(attachment), jsFace)
            return this
        }

        actual fun layer(
            attachment: AttachmentPoint,
            layer: Int
        ): Builder {
            jsBuilder.layer(mapAttachment(attachment), layer)
            return this
        }

        actual fun build(engine: Engine): RenderTarget {
            return RenderTarget(jsBuilder.build(engine.jsEngine), engine)
        }

        private fun mapAttachment(attachment: AttachmentPoint): RenderTarget_AttachmentPoint {
            return when (attachment) {
                AttachmentPoint.COLOR -> RenderTarget_AttachmentPoint.COLOR
                AttachmentPoint.COLOR1 -> RenderTarget_AttachmentPoint.COLOR // JS only has COLOR and DEPTH
                else -> RenderTarget_AttachmentPoint.DEPTH
            }
        }
    }
}
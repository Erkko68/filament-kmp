package io.github.erkko68.filament

import io.github.erkko68.filament.js.RenderTarget as JSRenderTarget
import io.github.erkko68.filament.js.`RenderTarget_Builder` as JSRenderTargetBuilder
import io.github.erkko68.filament.js.RenderTarget_AttachmentPoint
import io.github.erkko68.filament.js.Texture_CubemapFace

actual class RenderTarget(internal val jsRenderTarget: JSRenderTarget) {
    actual fun getTexture(attachment: AttachmentPoint): Texture? {
        // No getTexture in JS bindings
        return null
    }

    actual fun getMipLevel(attachment: AttachmentPoint): Int {
        return 0
    }

    actual fun getFace(attachment: AttachmentPoint): Texture.CubemapFace {
        return Texture.CubemapFace.POSITIVE_X
    }

    actual fun getLayer(attachment: AttachmentPoint): Int {
        return 0
    }

    actual enum class AttachmentPoint { COLOR, COLOR1, COLOR2, COLOR3, COLOR4, COLOR5, COLOR6, COLOR7, DEPTH }
    actual class Builder {
        private val jsBuilder = JSRenderTargetBuilder()

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
            return RenderTarget(jsBuilder.build(engine.jsEngine))
        }

        private fun mapAttachment(attachment: AttachmentPoint): RenderTarget_AttachmentPoint {
            return when (attachment) {
                AttachmentPoint.COLOR -> RenderTarget_AttachmentPoint.COLOR
                AttachmentPoint.COLOR1 -> RenderTarget_AttachmentPoint.COLOR0 // JS might only have COLOR (0)
                else -> RenderTarget_AttachmentPoint.DEPTH
            }
        }
    }
}
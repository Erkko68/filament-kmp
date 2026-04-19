package io.github.erkko68.filament

import io.github.erkko68.filament.jni.RenderTarget as JniRenderTarget

actual class RenderTarget(val nativeRenderTarget: JniRenderTarget) {
    actual enum class AttachmentPoint {
        COLOR, COLOR1, COLOR2, COLOR3, COLOR4, COLOR5, COLOR6, COLOR7, DEPTH;
        internal fun toJni() = JniRenderTarget.AttachmentPoint.values()[ordinal]
    }

    actual class Builder actual constructor() {
        private val jni = JniRenderTarget.Builder()

        actual fun texture(attachment: AttachmentPoint, texture: Texture?): Builder {
            jni.texture(attachment.toJni(), texture?.nativeTexture)
            return this
        }

        actual fun mipLevel(attachment: AttachmentPoint, level: Int): Builder {
            jni.mipLevel(attachment.toJni(), level)
            return this
        }

        actual fun face(attachment: AttachmentPoint, face: Texture.CubemapFace): Builder {
            jni.face(attachment.toJni(), face.toJni())
            return this
        }

        actual fun layer(attachment: AttachmentPoint, layer: Int): Builder {
            jni.layer(attachment.toJni(), layer)
            return this
        }

        actual fun build(engine: Engine): RenderTarget =
            RenderTarget(jni.build(engine.nativeEngine))
    }

    actual fun getTexture(attachment: AttachmentPoint): Texture? {
        val jni = nativeRenderTarget.getTexture(attachment.toJni()) ?: return null
        return Texture(jni)
    }

    actual fun getMipLevel(attachment: AttachmentPoint): Int = nativeRenderTarget.getMipLevel(attachment.toJni())

    actual fun getFace(attachment: AttachmentPoint): Texture.CubemapFace {
        val jni = nativeRenderTarget.getFace(attachment.toJni())
        return Texture.CubemapFace.values()[jni.ordinal]
    }

    actual fun getLayer(attachment: AttachmentPoint): Int = nativeRenderTarget.getLayer(attachment.toJni())
}

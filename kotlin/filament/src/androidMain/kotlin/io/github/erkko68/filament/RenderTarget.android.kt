package io.github.erkko68.filament

import com.google.android.filament.RenderTarget as AndroidRenderTarget

actual class RenderTarget @InternalFilamentApi constructor(internal val nativeRenderTarget: AndroidRenderTarget, private val textures: Array<Texture?>) {
    actual enum class AttachmentPoint {
        COLOR, COLOR1, COLOR2, COLOR3, COLOR4, COLOR5, COLOR6, COLOR7, DEPTH
    }

    actual class Builder actual constructor() {
        private val nativeBuilder = AndroidRenderTarget.Builder()
        private val textures = arrayOfNulls<Texture>(AttachmentPoint.entries.size)

        actual fun texture(attachment: AttachmentPoint, texture: Texture?): Builder {
            textures[attachment.ordinal] = texture
            nativeBuilder.texture(AndroidRenderTarget.AttachmentPoint.entries[attachment.ordinal], texture?.nativeTexture)
            return this
        }

        actual fun mipLevel(attachment: AttachmentPoint, level: Int): Builder {
            nativeBuilder.mipLevel(AndroidRenderTarget.AttachmentPoint.entries[attachment.ordinal], level)
            return this
        }

        actual fun face(attachment: AttachmentPoint, face: Texture.CubemapFace): Builder {
            nativeBuilder.face(AndroidRenderTarget.AttachmentPoint.entries[attachment.ordinal], com.google.android.filament.Texture.CubemapFace.entries[face.ordinal])
            return this
        }

        actual fun layer(attachment: AttachmentPoint, layer: Int): Builder {
            nativeBuilder.layer(AndroidRenderTarget.AttachmentPoint.entries[attachment.ordinal], layer)
            return this
        }

        actual fun build(engine: Engine): RenderTarget {
            return RenderTarget(nativeBuilder.build(engine.nativeEngine), textures.copyOf())
        }
    }

    actual fun getTexture(attachment: AttachmentPoint): Texture? = textures[attachment.ordinal]

    actual fun getMipLevel(attachment: AttachmentPoint): Int =
        nativeRenderTarget.getMipLevel(AndroidRenderTarget.AttachmentPoint.entries[attachment.ordinal])

    actual fun getFace(attachment: AttachmentPoint): Texture.CubemapFace =
        Texture.CubemapFace.entries[nativeRenderTarget.getFace(AndroidRenderTarget.AttachmentPoint.entries[attachment.ordinal]).ordinal]

    actual fun getLayer(attachment: AttachmentPoint): Int =
        nativeRenderTarget.getLayer(AndroidRenderTarget.AttachmentPoint.entries[attachment.ordinal])
}

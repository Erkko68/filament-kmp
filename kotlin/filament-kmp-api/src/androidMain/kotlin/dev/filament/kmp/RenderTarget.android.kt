package dev.filament.kmp

import com.google.android.filament.RenderTarget as AndroidRenderTarget

actual class RenderTarget internal constructor(
    internal var androidRenderTarget: AndroidRenderTarget?,
    internal var attachments: Map<AttachmentPoint, Texture?> = emptyMap(),
) {
    actual fun getNativeObject(): Long {
        val target = requireNotNull(androidRenderTarget) { "Calling method on destroyed RenderTarget" }
        return target.nativeObject
    }

    actual fun getTexture(attachment: AttachmentPoint): Texture? = attachments[attachment]

    actual fun getMipLevel(attachment: AttachmentPoint): Int {
        val target = requireNotNull(androidRenderTarget) { "Calling method on destroyed RenderTarget" }
        return target.getMipLevel(attachment.toAndroid())
    }

    actual fun getFace(attachment: AttachmentPoint): Texture.CubemapFace {
        val target = requireNotNull(androidRenderTarget) { "Calling method on destroyed RenderTarget" }
        return target.getFace(attachment.toAndroid()).toKmp()
    }

    actual fun getLayer(attachment: AttachmentPoint): Int {
        val target = requireNotNull(androidRenderTarget) { "Calling method on destroyed RenderTarget" }
        return target.getLayer(attachment.toAndroid())
    }

    actual internal fun invalidate() {
        androidRenderTarget = null
        attachments = emptyMap()
    }

    actual class Builder {
        private val androidBuilder = AndroidRenderTarget.Builder()
        private val textures = mutableMapOf<AttachmentPoint, Texture?>()

        actual fun texture(attachment: AttachmentPoint, texture: Texture?): Builder {
            androidBuilder.texture(attachment.toAndroid(), texture?.androidTexture)
            textures[attachment] = texture
            return this
        }

        actual fun mipLevel(attachment: AttachmentPoint, level: Int): Builder {
            androidBuilder.mipLevel(attachment.toAndroid(), level)
            return this
        }

        actual fun face(attachment: AttachmentPoint, face: Texture.CubemapFace): Builder {
            androidBuilder.face(attachment.toAndroid(), face.toAndroid())
            return this
        }

        actual fun layer(attachment: AttachmentPoint, layer: Int): Builder {
            androidBuilder.layer(attachment.toAndroid(), layer)
            return this
        }

        actual fun build(engine: Engine): RenderTarget {
            val androidEngine = requireNotNull(engine.androidEngine) { "Engine is closed." }
            return RenderTarget(androidBuilder.build(androidEngine), textures.toMap())
        }
    }

    actual enum class AttachmentPoint {
        COLOR,
        COLOR1,
        COLOR2,
        COLOR3,
        COLOR4,
        COLOR5,
        COLOR6,
        COLOR7,
        DEPTH,
    }
}

private fun RenderTarget.AttachmentPoint.toAndroid(): AndroidRenderTarget.AttachmentPoint = when (this) {
    RenderTarget.AttachmentPoint.COLOR -> AndroidRenderTarget.AttachmentPoint.COLOR
    RenderTarget.AttachmentPoint.COLOR1 -> AndroidRenderTarget.AttachmentPoint.COLOR1
    RenderTarget.AttachmentPoint.COLOR2 -> AndroidRenderTarget.AttachmentPoint.COLOR2
    RenderTarget.AttachmentPoint.COLOR3 -> AndroidRenderTarget.AttachmentPoint.COLOR3
    RenderTarget.AttachmentPoint.COLOR4 -> AndroidRenderTarget.AttachmentPoint.COLOR4
    RenderTarget.AttachmentPoint.COLOR5 -> AndroidRenderTarget.AttachmentPoint.COLOR5
    RenderTarget.AttachmentPoint.COLOR6 -> AndroidRenderTarget.AttachmentPoint.COLOR6
    RenderTarget.AttachmentPoint.COLOR7 -> AndroidRenderTarget.AttachmentPoint.COLOR7
    RenderTarget.AttachmentPoint.DEPTH -> AndroidRenderTarget.AttachmentPoint.DEPTH
}


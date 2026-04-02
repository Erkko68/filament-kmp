package dev.filament.kmp

actual class RenderTarget {
    actual fun getNativeObject(): Long {
        TODO("Not yet implemented")
    }

    actual fun getTexture(attachment: AttachmentPoint): Texture? {
        TODO("Not yet implemented")
    }

    actual fun getMipLevel(attachment: AttachmentPoint): Int {
        TODO("Not yet implemented")
    }

    actual fun getFace(attachment: AttachmentPoint): Texture.CubemapFace {
        TODO("Not yet implemented")
    }

    actual fun getLayer(attachment: AttachmentPoint): Int {
        TODO("Not yet implemented")
    }

    actual internal fun invalidate() {
    }

    actual class Builder {
        actual fun texture(attachment: AttachmentPoint, texture: Texture?): Builder {
            TODO("Not yet implemented")
        }

        actual fun mipLevel(attachment: AttachmentPoint, level: Int): Builder {
            TODO("Not yet implemented")
        }

        actual fun face(attachment: AttachmentPoint, face: Texture.CubemapFace): Builder {
            TODO("Not yet implemented")
        }

        actual fun layer(attachment: AttachmentPoint, layer: Int): Builder {
            TODO("Not yet implemented")
        }

        actual fun build(engine: Engine): RenderTarget {
            TODO("Not yet implemented")
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


package io.github.erkko68.filament

expect class RenderTarget {
    enum class AttachmentPoint {
        COLOR, COLOR1, COLOR2, COLOR3, COLOR4, COLOR5, COLOR6, COLOR7, DEPTH
    }

    class Builder() {
        fun texture(attachment: AttachmentPoint, texture: Texture?): Builder
        fun mipLevel(attachment: AttachmentPoint, level: Int): Builder
        fun face(attachment: AttachmentPoint, face: Texture.CubemapFace): Builder
        fun layer(attachment: AttachmentPoint, layer: Int): Builder
        fun build(engine: Engine): RenderTarget
    }

    fun getTexture(attachment: AttachmentPoint): Texture?
    fun getMipLevel(attachment: AttachmentPoint): Int
    fun getFace(attachment: AttachmentPoint): Texture.CubemapFace
    fun getLayer(attachment: AttachmentPoint): Int
}

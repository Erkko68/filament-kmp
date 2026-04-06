package dev.filament.kmp

/**
 * An offscreen render target that can be associated with a [View] and contains
 * weak references to a set of attached [Texture] objects.
 */
expect class RenderTarget {

    /**
     * An attachment point is a slot that can be assigned to a [Texture].
     */
    enum class AttachmentPoint {
        COLOR,
        COLOR1,
        COLOR2,
        COLOR3,
        COLOR4,
        COLOR5,
        COLOR6,
        COLOR7,
        DEPTH
    }

    class Builder() {
        /**
         * Sets a texture to a given attachment point.
         */
        fun texture(attachment: AttachmentPoint, texture: Texture?): Builder

        /**
         * Sets the mipmap level for a given attachment point.
         */
        fun mipLevel(attachment: AttachmentPoint, level: Int): Builder

        /**
         * Sets the cubemap face for a given attachment point.
         */
        fun face(attachment: AttachmentPoint, face: Texture.CubemapFace): Builder

        /**
         * Sets the layer for a given attachment point (for 3D textures).
         */
        fun layer(attachment: AttachmentPoint, layer: Int): Builder

        /**
         * Creates the RenderTarget object.
         */
        fun build(engine: Engine): RenderTarget
    }

    /**
     * Gets the texture set on the given attachment point.
     */
    fun getTexture(attachment: AttachmentPoint): Texture?

    /**
     * Returns the mipmap level set on the given attachment point.
     */
    fun getMipLevel(attachment: AttachmentPoint): Int

    /**
     * Returns the face of a cubemap set on the given attachment point.
     */
    fun getFace(attachment: AttachmentPoint): Texture.CubemapFace

    /**
     * Returns the texture-layer set on the given attachment point.
     */
    fun getLayer(attachment: AttachmentPoint): Int
}

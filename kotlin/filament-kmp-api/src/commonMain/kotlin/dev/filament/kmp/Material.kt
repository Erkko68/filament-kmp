package dev.filament.kmp

/**
 * A Filament Material defines the visual appearance of an object.
 */
expect class Material {
    enum class CullingMode {
        NONE,
        FRONT,
        BACK,
        FRONT_AND_BACK,
    }

    enum class TransparencyMode {
        DEFAULT,
        TWO_PASSES_ONE_SIDE,
        TWO_PASSES_TWO_SIDES,
    }

    class Builder {
        /**
         * Provides the Material Package payload.
         */
        fun payload(buffer: Any, size: Int): Builder

        /**
         * Builds a Material object from the provided package.
         */
        fun build(engine: Engine): Material
    }

    /**
     * Creates a new MaterialInstance.
     */
    fun createInstance(): MaterialInstance

    /**
     * Creates a new named MaterialInstance.
     */
    fun createInstance(name: String): MaterialInstance

    /**
     * Returns the default material instance.
     */
    fun getDefaultInstance(): MaterialInstance

    /**
     * Returns the material name.
     */
    fun getName(): String

    fun getNativeObject(): Long

    internal fun invalidate()
}


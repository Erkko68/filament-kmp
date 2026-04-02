package dev.filament.kmp

/**
 * A Filament Material defines the visual appearance of an object. Materials function as a
 * templates from which {@link MaterialInstance}s can be spawned. Use {@link Builder} to construct
 * a Material object.
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
         * Specifies the material data. The material data is a binary blob produced by
         * libfilamat or by matc.
         *
         * @param buffer  buffer containing material data
         * @param size    size of the material data in bytes
         */
        fun payload(buffer: Any, size: Int): Builder

        /**
         * Creates and returns the Material object.
         *
         * @param engine reference to the Engine instance to associate this Material with
         *
         * @return the newly created object
         *
         * @exception IllegalStateException if the material could not be created
         */
        fun build(engine: Engine): Material
    }

    /**
     * Creates a new instance of this material. Material instances should be freed using
     * {@link Engine#destroyMaterialInstance(MaterialInstance)}.
     *
     * @return the new instance
     */
    fun createInstance(): MaterialInstance

    /**
     * Creates a new instance of this material with a specified name. Material instances should be
     * freed using {@link Engine#destroyMaterialInstance(MaterialInstance)}.
     *
     * @param name arbitrary label to associate with the given material instance
     *
     * @return the new instance
     */
    fun createInstance(name: String): MaterialInstance

    /** Returns the material's default instance. */
    fun getDefaultInstance(): MaterialInstance

    /**
     * Returns the name of this material. The material name is used for debugging purposes.
     *
     * @see
     * <a href="https://google.github.io/filament/Materials.html#materialdefinitions/materialblock/general:name">
     * General: name</a>
     */
    fun getName(): String

    fun getNativeObject(): Long

    internal fun invalidate()
}


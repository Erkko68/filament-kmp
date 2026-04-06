package dev.filament.kmp

/**
 * Creates and initializes GPU state common to all environment map filters supported.
 */
expect class IBLPrefilterContext(engine: Engine) {
    fun destroy()

    class EquirectangularToCubemap(context: IBLPrefilterContext) {
        fun run(equirect: Texture): Texture
        fun destroy()
    }

    class SpecularFilter(context: IBLPrefilterContext) {
        fun run(skybox: Texture): Texture
        fun destroy()
    }
}

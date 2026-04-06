package dev.filament.kmp

/**
 * Utility for decoding an HDR file and producing a Filament texture.
 */
expect object HDRLoader {
    class Options {
        var desiredFormat: Texture.InternalFormat
    }

    /**
     * Consumes the content of an HDR file and produces a [Texture] object.
     *
     * @param engine Gets passed to the builder.
     * @param buffer The content of the HDR File.
     * @param options Loader options.
     * @return The resulting Filament texture, or null on failure.
     */
    fun createTexture(engine: Engine, buffer: Buffer, options: Options = Options()): Texture?
}

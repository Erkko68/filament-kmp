package io.github.erkko68.filament.filamat

/**
 * MaterialPackage holds the compiled binary data of a Filament material.
 *
 * A MaterialPackage is produced by MaterialBuilder after material source code compilation.
 * It contains optimized shader code for multiple backends (OpenGL, Vulkan, Metal, WebGPU),
 * material metadata, and parameter definitions.
 *
 * **Usage:**
 * After building a MaterialPackage with MaterialBuilder, pass the package data to
 * Filament's Material system via Engine.Material creation. The package is self-contained
 * and can be serialized, cached, or transmitted.
 *
 * **Validity:**
 * Always check isValid() before using a package. An invalid package indicates compilation
 * failure and should be discarded.
 *
 * @see MaterialBuilder
 * @see Filamat
 */
expect class MaterialPackage {
    /**
     * Get the binary package data as a byte array.
     *
     * This is the raw compiled material data that can be loaded by Filament's Material system.
     * The data is in a proprietary format specific to Filament.
     *
     * @return ByteArray containing the compiled material binary.
     */
    val buffer: ByteArray

    /**
     * Check if this package is valid.
     *
     * A package is valid if compilation succeeded. Invalid packages should be discarded
     * and not used to create Materials.
     *
     * @return true if the package is valid and ready to use, false if compilation failed.
     */
    val isValid: Boolean
}

package io.github.erkko68.filament.filamat

/**
 * Filamat is the Filament Material Compiler.
 *
 * It compiles material source code (.mat files) into binary material packages that can be
 * loaded by Filament's Material system. Filamat generates shader code for multiple backends
 * (OpenGL, Vulkan, Metal, WebGPU) and optimizes them for performance.
 *
 * **Initialization:**
 * Call Filamat.init() before building any materials. This initializes internal compiler resources.
 * Call Filamat.shutdown() when finished to release resources.
 *
 * **Typical usage:**
 * ```
 * Filamat.init()
 * val builder = MaterialBuilder()
 *     .name("MyMaterial")
 *     .shading(MaterialBuilder.Shading.LIT)
 *     // ... configure material ...
 *     .build()
 *
 * val package = builder.package
 * // Use package with Engine.Material creation
 * Filamat.shutdown()
 * ```
 *
 * @see MaterialBuilder
 * @see MaterialPackage
 */
expect object Filamat {
    /**
     * Initialize the Filamat compiler.
     *
     * Must be called once before creating any MaterialBuilder instances. This initializes
     * internal compiler resources and shader compilation infrastructure.
     */
    fun init()

    /**
     * Release the compiler's global state. Call when done building materials.
     */
    fun shutdown()
}

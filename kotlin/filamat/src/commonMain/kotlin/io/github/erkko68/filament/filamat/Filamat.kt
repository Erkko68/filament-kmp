package io.github.erkko68.filament.filamat

import io.github.erkko68.filament.Filament
import io.github.erkko68.filament.interop.ExternalSymbolName

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
object Filamat {
    /**
     * Initialize the Filamat compiler.
     *
     * Must be called once before creating any MaterialBuilder instances. This initializes
     * internal compiler resources and shader compilation infrastructure.
     */
    fun init() {
        // init()/shutdown() bracket glslang's process init; shutdown() tears it down whether or not init() ran.
        Filament.init()
        FilaMaterialBuilder_init()
    }

    /**
     * Release the compiler's global state. Call when done building materials.
     */
    fun shutdown() = FilaMaterialBuilder_shutdown()
}

@ExternalSymbolName("FilaMaterialBuilder_init")
private external fun FilaMaterialBuilder_init()

@ExternalSymbolName("FilaMaterialBuilder_shutdown")
private external fun FilaMaterialBuilder_shutdown()

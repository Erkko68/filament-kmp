package io.github.erkko68.filament.utils

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Texture
import io.github.erkko68.filament.interop.*

/**
 * Creates and initializes GPU state common to all environment map filters supported.
 *
 * Typically, only one instance per [Engine] needs to exist.
 *
 * Usage example:
 * ```kotlin
 * val context = IBLPrefilterContext(engine)
 * val filter = SpecularFilter(context)
 * val texture = filter.run(environmentCubemap)
 * val indirectLight = IndirectLight.Builder()
 *     .reflections(texture)
 *     .build(engine)
 * ```
 *
 * @param engine the [Engine] to use for all GPU operations
 */
class IBLPrefilterContext(engine: Engine) : AutoCloseable {
    internal val nativeHandle = FilaIBLPrefilterContext_create(engine.nativeObject)

    /**
     * Destroys all GPU resources created during initialization.
     */
    fun destroy() {
        FilaIBLPrefilterContext_destroy(nativeHandle)
    }

    /** Same as [destroy]; lets this be used with `use { }` and try-with-resources. */
    override fun close() = destroy()
}

/**
 * Converts an equirectangular image to a cubemap.
 *
 * The input equirectangular texture must:
 * - Be a 2D texture (width == 2 * height)
 * - Have all mip levels allocated
 * - Have SAMPLEABLE usage
 *
 * @param context the [IBLPrefilterContext] to use
 */
class EquirectangularToCubemap(context: IBLPrefilterContext) : AutoCloseable {
    private val nativeHandle = FilaIBLPrefilterEquirectangularToCubemap_create(context.nativeHandle)

    /**
     * Destroys all GPU resources created during initialization.
     */
    fun destroy() {
        FilaIBLPrefilterEquirectangularToCubemap_destroy(nativeHandle)
    }

    /** Same as [destroy]; lets this be used with `use { }` and try-with-resources. */
    override fun close() = destroy()

    /**
     * Converts the given equirectangular [Texture] to a cubemap.
     *
     * The output cubemap is automatically created with default parameters (256 size, 9 levels)
     * if not provided via a platform-specific overload.
     *
     * @param equirect the equirectangular texture to convert; must be SAMPLEABLE, 2D, width == 2*height, all mips allocated
     * @return the resulting cubemap [Texture]
     */
    fun run(equirect: Texture): Texture {
        val handle = FilaIBLPrefilterEquirectangularToCubemap_run(nativeHandle, equirect.nativeObject)
        check(handle != NullPointer) { "EquirectangularToCubemap failed" }
        return Texture(handle)
    }
}

/**
 * GPU-based implementation of the specular probe pre-integration filter.
 *
 * An instance is needed per filter configuration. The filter uses D_GGX kernel with 1024 samples
 * and 5 roughness levels by default.
 *
 * @param context the [IBLPrefilterContext] to use
 */
class SpecularFilter(context: IBLPrefilterContext) : AutoCloseable {
    private val nativeHandle = FilaIBLPrefilterSpecularFilter_create(context.nativeHandle)

    /**
     * Destroys all GPU resources created during initialization.
     */
    fun destroy() {
        FilaIBLPrefilterSpecularFilter_destroy(nativeHandle)
    }

    /** Same as [destroy]; lets this be used with `use { }` and try-with-resources. */
    override fun close() = destroy()

    /**
     * Generates a prefiltered specular cubemap from the given environment cubemap.
     *
     * The environment cubemap must be SAMPLEABLE and have all mip levels allocated.
     * The output is automatically created with default parameters if not provided via
     * a platform-specific overload.
     *
     * @param skybox the environment cubemap to prefilter; must be SAMPLEABLE with all levels allocated
     * @return the prefiltered specular [Texture]
     */
    fun run(skybox: Texture): Texture {
        val handle = FilaIBLPrefilterSpecularFilter_run(nativeHandle, skybox.nativeObject)
        check(handle != NullPointer) { "SpecularFilter failed" }
        return Texture(handle)
    }
}

@ExternalSymbolName("FilaIBLPrefilterContext_create")
private external fun FilaIBLPrefilterContext_create(engine: NativePointer): NativePointer

@ExternalSymbolName("FilaIBLPrefilterContext_destroy")
private external fun FilaIBLPrefilterContext_destroy(context: NativePointer)

@ExternalSymbolName("FilaIBLPrefilterEquirectangularToCubemap_create")
private external fun FilaIBLPrefilterEquirectangularToCubemap_create(context: NativePointer): NativePointer

@ExternalSymbolName("FilaIBLPrefilterEquirectangularToCubemap_destroy")
private external fun FilaIBLPrefilterEquirectangularToCubemap_destroy(helper: NativePointer)

@ExternalSymbolName("FilaIBLPrefilterEquirectangularToCubemap_run")
private external fun FilaIBLPrefilterEquirectangularToCubemap_run(helper: NativePointer, equirect: NativePointer): NativePointer

@ExternalSymbolName("FilaIBLPrefilterSpecularFilter_create")
private external fun FilaIBLPrefilterSpecularFilter_create(context: NativePointer): NativePointer

@ExternalSymbolName("FilaIBLPrefilterSpecularFilter_destroy")
private external fun FilaIBLPrefilterSpecularFilter_destroy(helper: NativePointer)

@ExternalSymbolName("FilaIBLPrefilterSpecularFilter_run")
private external fun FilaIBLPrefilterSpecularFilter_run(helper: NativePointer, skybox: NativePointer): NativePointer

package io.github.erkko68.filament.utils

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.InternalFilamentApi
import io.github.erkko68.filament.Texture
import io.github.erkko68.filament.interop.*
import io.github.erkko68.filament.utils.capi.*

/**
 * Creates and initializes GPU state common to all environment map filters supported.
 *
 * Typically, only one instance per [Engine] needs to exist.
 *
 * Usage example:
 * ```kotlin
 * val context = IBLPrefilterContext(engine)
 * val filter = IBLPrefilterContext.SpecularFilter(context)
 * val texture = filter(environmentCubemap)
 * val indirectLight = IndirectLight.Builder()
 *     .reflections(texture)
 *     .build(engine)
 * ```
 */
class IBLPrefilterContext(engine: Engine) : AutoCloseable {
    internal val nativeHandle = FilaIBLPrefilterContext_create(engine.nativeObject)

    /** The native object, for interop with code calling the Fila* C API directly. Read-only: this wrapper owns it. */
    @InternalFilamentApi
    val nativeObject: NativePointer get() = nativeHandle

    /** The filter kernel. */
    enum class Kernel {
        /** Trowbridge-reitz distribution. */
        D_GGX,
    }

    /** Destroys all GPU resources created during initialization. */
    fun destroy() = FilaIBLPrefilterContext_destroy(nativeHandle)

    /** Same as [destroy]; lets this be used with `use { }` and try-with-resources. */
    override fun close() = destroy()

    /** Converts an equirectangular image to a cubemap. */
    class EquirectangularToCubemap(context: IBLPrefilterContext, config: Config = Config()) : AutoCloseable {
        private val nativeHandle = config.useNative { FilaIBLPrefilterContextEquirectangularToCubemap_create_Config(context.nativeHandle, it) }

        /** @property mirror mirror the source horizontally */
        class Config(var mirror: Boolean = true)

        /** Destroys all GPU resources created during initialization. */
        fun destroy() = FilaIBLPrefilterContextEquirectangularToCubemap_destroy(nativeHandle)

        /** Same as [destroy]; lets this be used with `use { }` and try-with-resources. */
        override fun close() = destroy()

        /**
         * Converts an equirectangular image to a cubemap.
         *
         * @param equirectangular Texture to convert to a cubemap: a SAMPLEABLE 2D texture with
         *                        equirectangular geometry (width == 2 * height), allocated with all mip levels.
         * @param outCubemap Output cubemap, with SAMPLEABLE and COLOR_ATTACHMENT usage bits. If null the
         *                   texture is automatically created with default parameters (size of 256 with 9 levels).
         * @return returns outCubemap
         */
        operator fun invoke(equirectangular: Texture, outCubemap: Texture? = null): Texture =
            texture(FilaIBLPrefilterContextEquirectangularToCubemap_invoke(nativeHandle, equirectangular.nativeObject, outCubemap.handle), outCubemap)

        private fun Config.useNative(block: (NativePointer) -> NativePointer): NativePointer {
            val c = FilaIBLPrefilterContextEquirectangularToCubemapConfig_create()
            try {
                FilaIBLPrefilterContextEquirectangularToCubemapConfig_setMirror(c, mirror)
                return block(c)
            } finally {
                FilaIBLPrefilterContextEquirectangularToCubemapConfig_destroy(c)
            }
        }
    }

    /** Generates an irradiance cubemap. An instance is needed per filter configuration. */
    class IrradianceFilter(context: IBLPrefilterContext, config: Config = Config()) : AutoCloseable {
        private val nativeHandle = config.useNative { FilaIBLPrefilterContextIrradianceFilter_create_Config(context.nativeHandle, it) }

        /**
         * Filter configuration.
         *
         * @property sampleCount filter sample count (max 2048)
         * @property kernel filter kernel
         */
        class Config(var sampleCount: Int = 1024, var kernel: Kernel = Kernel.D_GGX)

        /**
         * Filtering options for the current environment.
         *
         * @property hdrLinear no HDR compression up to this value
         * @property hdrMax HDR compression between hdrLinear and hdrMax
         * @property lodOffset Good values are 2.0 or 3.0. Higher values help with heavily HDR inputs.
         * @property generateMipmap set to false if the input environment map already has mipmaps
         */
        class Options(
            var hdrLinear: Float = 1024.0f,
            var hdrMax: Float = 16384.0f,
            var lodOffset: Float = 2.0f,
            var generateMipmap: Boolean = true,
        )

        /** Destroys all GPU resources created during initialization. */
        fun destroy() = FilaIBLPrefilterContextIrradianceFilter_destroy(nativeHandle)

        /** Same as [destroy]; lets this be used with `use { }` and try-with-resources. */
        override fun close() = destroy()

        /**
         * Generates an irradiance cubemap. Mipmaps are used in the generation, so the
         * environment cubemap must be SAMPLEABLE with all its levels allocated. If
         * [Options.generateMipmap] is true, the mipmap levels are overwritten.
         *
         * @param outIrradianceTexture Output cubemap with at least COLOR_ATTACHMENT and SAMPLEABLE
         *                             usages, or null to create one with default parameters.
         * @return returns outIrradianceTexture
         */
        operator fun invoke(options: Options, environmentCubemap: Texture, outIrradianceTexture: Texture? = null): Texture =
            options.useNative { o ->
                texture(FilaIBLPrefilterContextIrradianceFilter_invoke_Options_Texture_Texture(nativeHandle, o, environmentCubemap.nativeObject, outIrradianceTexture.handle), outIrradianceTexture)
            }

        /** [invoke] with the default [Options]. */
        operator fun invoke(environmentCubemap: Texture, outIrradianceTexture: Texture? = null): Texture =
            texture(FilaIBLPrefilterContextIrradianceFilter_invoke_Texture_Texture(nativeHandle, environmentCubemap.nativeObject, outIrradianceTexture.handle), outIrradianceTexture)

        private fun Config.useNative(block: (NativePointer) -> NativePointer): NativePointer {
            val c = FilaIBLPrefilterContextIrradianceFilterConfig_create()
            try {
                FilaIBLPrefilterContextIrradianceFilterConfig_setSampleCount(c, sampleCount)
                FilaIBLPrefilterContextIrradianceFilterConfig_setKernel(c, kernel.ordinal)
                return block(c)
            } finally {
                FilaIBLPrefilterContextIrradianceFilterConfig_destroy(c)
            }
        }

        private fun <R> Options.useNative(block: (NativePointer) -> R): R {
            val o = FilaIBLPrefilterContextIrradianceFilterOptions_create()
            try {
                FilaIBLPrefilterContextIrradianceFilterOptions_setHdrLinear(o, hdrLinear)
                FilaIBLPrefilterContextIrradianceFilterOptions_setHdrMax(o, hdrMax)
                FilaIBLPrefilterContextIrradianceFilterOptions_setLodOffset(o, lodOffset)
                FilaIBLPrefilterContextIrradianceFilterOptions_setGenerateMipmap(o, generateMipmap)
                return block(o)
            } finally {
                FilaIBLPrefilterContextIrradianceFilterOptions_destroy(o)
            }
        }
    }

    /** Generates a prefiltered (specular) cubemap. An instance is needed per filter configuration. */
    class SpecularFilter(context: IBLPrefilterContext, config: Config = Config()) : AutoCloseable {
        private val nativeHandle = config.useNative { FilaIBLPrefilterContextSpecularFilter_create_Config(context.nativeHandle, it) }

        /**
         * Filter configuration.
         *
         * @property sampleCount filter sample count (max 2048)
         * @property levelCount number of roughness levels
         * @property kernel filter kernel
         */
        class Config(var sampleCount: Int = 1024, var levelCount: Int = 5, var kernel: Kernel = Kernel.D_GGX)

        /**
         * Filtering options for the current environment.
         *
         * @property hdrLinear no HDR compression up to this value
         * @property hdrMax HDR compression between hdrLinear and hdrMax
         * @property lodOffset Good values are 1.0 or 2.0. Higher values help with heavily HDR inputs.
         * @property generateMipmap set to false if the input environment map already has mipmaps
         */
        class Options(
            var hdrLinear: Float = 1024.0f,
            var hdrMax: Float = 16384.0f,
            var lodOffset: Float = 1.0f,
            var generateMipmap: Boolean = true,
        )

        /** Destroys all GPU resources created during initialization. */
        fun destroy() = FilaIBLPrefilterContextSpecularFilter_destroy(nativeHandle)

        /** Same as [destroy]; lets this be used with `use { }` and try-with-resources. */
        override fun close() = destroy()

        /**
         * Generates a prefiltered cubemap. The environment cubemap must be SAMPLEABLE with all its
         * levels allocated. If [Options.generateMipmap] is true, the mipmap levels are overwritten.
         *
         * @param outReflectionsTexture Output cubemap with at least COLOR_ATTACHMENT and SAMPLEABLE usages
         *                              and the levels [Config.levelCount] asks for, or null to create one
         *                              with default parameters.
         * @return returns outReflectionsTexture
         */
        operator fun invoke(options: Options, environmentCubemap: Texture, outReflectionsTexture: Texture? = null): Texture =
            options.useNative { o ->
                texture(FilaIBLPrefilterContextSpecularFilter_invoke_Options_Texture_Texture(nativeHandle, o, environmentCubemap.nativeObject, outReflectionsTexture.handle), outReflectionsTexture)
            }

        /** [invoke] with the default [Options]: all mipmap levels are overwritten. */
        operator fun invoke(environmentCubemap: Texture, outReflectionsTexture: Texture? = null): Texture =
            texture(FilaIBLPrefilterContextSpecularFilter_invoke_Texture_Texture(nativeHandle, environmentCubemap.nativeObject, outReflectionsTexture.handle), outReflectionsTexture)

        private fun Config.useNative(block: (NativePointer) -> NativePointer): NativePointer {
            val c = FilaIBLPrefilterContextSpecularFilterConfig_create()
            try {
                FilaIBLPrefilterContextSpecularFilterConfig_setSampleCount(c, sampleCount)
                FilaIBLPrefilterContextSpecularFilterConfig_setLevelCount(c, levelCount)
                FilaIBLPrefilterContextSpecularFilterConfig_setKernel(c, kernel.ordinal)
                return block(c)
            } finally {
                FilaIBLPrefilterContextSpecularFilterConfig_destroy(c)
            }
        }

        private fun <R> Options.useNative(block: (NativePointer) -> R): R {
            val o = FilaIBLPrefilterContextSpecularFilterOptions_create()
            try {
                FilaIBLPrefilterContextSpecularFilterOptions_setHdrLinear(o, hdrLinear)
                FilaIBLPrefilterContextSpecularFilterOptions_setHdrMax(o, hdrMax)
                FilaIBLPrefilterContextSpecularFilterOptions_setLodOffset(o, lodOffset)
                FilaIBLPrefilterContextSpecularFilterOptions_setGenerateMipmap(o, generateMipmap)
                return block(o)
            } finally {
                FilaIBLPrefilterContextSpecularFilterOptions_destroy(o)
            }
        }
    }
}

private val Texture?.handle: NativePointer get() = this?.nativeObject ?: NullPointer

/** The filters return [out], or the texture they created when it's null. */
private fun texture(handle: NativePointer, out: Texture?): Texture {
    check(handle != NullPointer) { "IBL prefilter produced no texture" }
    return out ?: Texture(handle)
}

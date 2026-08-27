package io.github.erkko68.filament

// Engine construction options. No platform state — the JVM, native and web actuals
// all declared the same fields with the same defaults.

/**
 * Advanced parameters for customizing Engine initialization.
 *
 * These settings control memory allocation, threading, and rendering behavior.
 */
data class EngineConfig(
    /** Size of the command buffer in MB (default depends on backend). */
    var commandBufferSizeMB: Long = 3 * 1,
    /** Per-render-pass arena size in MB. */
    var perRenderPassArenaSizeMB: Long = 3,
    /** Driver handle arena size in MB. */
    var driverHandleArenaSizeMB: Long = 0,
    /** Minimum command buffer size in MB. */
    var minCommandBufferSizeMB: Long = 1,
    /** Size of per-frame commands in MB. */
    var perFrameCommandsSizeMB: Long = 2,
    /** Number of threads for the job system (0 = CPU count). */
    var jobSystemThreadCount: Long = 0,
    /** Disable backend parallel shader compilation (forces serial compilation). */
    var disableParallelShaderCompile: Boolean = false,
    /** Stereoscopic rendering technique to use. */
    var stereoscopicType: Engine.StereoscopicType = Engine.StereoscopicType.NONE,
    /** Number of stereoscopic eyes (usually 2 for VR). */
    var stereoscopicEyeCount: Long = 2,
    /** Size of the resource allocator cache in MB. */
    var resourceAllocatorCacheSizeMB: Long = 64,
    /** Maximum age of cached resources (in frames). */
    var resourceAllocatorCacheMaxAge: Long = 1,
    /** Disable the debug check that catches use of a destroyed backend handle. */
    var disableHandleUseAfterFreeCheck: Boolean = false,

    /** Preferred shader language to use. */
    var preferredShaderLanguage: ShaderLanguage = ShaderLanguage.DEFAULT,
    /** Force OpenGL ES 2.0 context (if applicable). */
    var forceGLES2Context: Boolean = false,
    /** Assert that the native window passed to `createSwapChain` is valid. */
    var assertNativeWindowIsValid: Boolean = false,
    /** GPU context priority hint for the driver. */
    var gpuContextPriority: Engine.GpuContextPriority = Engine.GpuContextPriority.DEFAULT,
    /** Initial size of shared uniform buffer objects in bytes. */
    var sharedUboInitialSizeInBytes: Long = 256 * 64,
) {

        /**
         * Preferred shader language for platform.
         */
        enum class ShaderLanguage {
            /** Use platform default. */
            DEFAULT,
            /** Metal Shading Language (Apple). */
            MSL,
            /** Pre-compiled Metal library. */
            METAL_LIBRARY,
        }
}

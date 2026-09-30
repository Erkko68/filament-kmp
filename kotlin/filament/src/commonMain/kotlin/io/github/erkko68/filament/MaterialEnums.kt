package io.github.erkko68.filament

/**
 * The domain a material's shaders apply to.
 *
 * - SURFACE: Shaders applied to renderables
 * - POST_PROCESS: Shaders applied to rendered buffers
 * - COMPUTE: Compute shader
 */
enum class MaterialDomain { SURFACE, POST_PROCESS, COMPUTE }

/**
 * Refraction rendering mode.
 *
 * - NONE: No refraction
 * - CUBEMAP: Refracted rays go to the IBL cubemap
 * - SCREEN_SPACE: Refracted rays go to screen space
 */
enum class RefractionMode { NONE, CUBEMAP, SCREEN_SPACE }

/**
 * Type of refracted surface.
 *
 * - SOLID: Refraction through solid objects (e.g. a sphere)
 * - THIN: Refraction through thin objects (e.g. a window)
 */
enum class RefractionType { SOLID, THIN }

/**
 * Reflection rendering mode.
 *
 * - DEFAULT: Reflections sample from the scene's IBL only
 * - SCREEN_SPACE: Reflections sample from screen space, and fall back to the scene's IBL
 */
enum class ReflectionMode { DEFAULT, SCREEN_SPACE }

/**
 * Variant filter bits, combined into a mask (`UserVariantFilterMask` in C++) for [Material.compile] and
 * [MaterialInstance.compile].
 */
object UserVariantFilterBit {
    /** Directional lighting */
    const val DIRECTIONAL_LIGHTING: Int = 0x01
    /** Dynamic lighting */
    const val DYNAMIC_LIGHTING: Int = 0x02
    /** Shadow receiver */
    const val SHADOW_RECEIVER: Int = 0x04
    /** Skinning */
    const val SKINNING: Int = 0x08
    /** Fog */
    const val FOG: Int = 0x10
    /** Variance shadow maps */
    const val VSM: Int = 0x20
    /** Screen-space reflections */
    const val SSR: Int = 0x40
    /** Instanced stereo rendering */
    const val STE: Int = 0x80
    /** All variants */
    const val ALL: Int = 0xFF
}

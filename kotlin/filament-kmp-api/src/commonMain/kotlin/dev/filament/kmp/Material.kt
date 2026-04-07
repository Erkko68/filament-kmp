package dev.filament.kmp

/**
 * A Filament Material defines the visual appearance of an object. Materials function as a
 * templates from which [MaterialInstance]s can be spawned.
 */
expect class Material {
    enum class Shading { UNLIT, LIT, SUBSURFACE, CLOTH, SPECULAR_GLOSSINESS }
    enum class Interpolation { SMOOTH, FLAT }
    enum class BlendingMode { OPAQUE, TRANSPARENT, ADD, MASKED, FADE, MULTIPLY, SCREEN }
    enum class TransparencyMode { DEFAULT, TWO_PASSES_ONE_SIDE, TWO_PASSES_TWO_SIDES }
    enum class RefractionMode { NONE, CUBEMAP, SCREEN_SPACE }
    enum class RefractionType { SOLID, THIN }
    enum class ReflectionMode { DEFAULT, SCREEN_SPACE }
    enum class VertexDomain { OBJECT, WORLD, VIEW, DEVICE }
    enum class CullingMode { NONE, FRONT, BACK, FRONT_AND_BACK }
    enum class CompilerPriorityQueue { CRITICAL, HIGH, LOW }
    enum class UboBatchingMode { DEFAULT, DISABLED }

    class Builder() {
        enum class ShadowSamplingQuality { HARD, LOW }
        fun payload(buffer: Any, size: Int): Builder
        fun sphericalHarmonicsBandCount(shBandCount: Int): Builder
        fun shadowSamplingQuality(quality: ShadowSamplingQuality): Builder
        fun uboBatching(mode: UboBatchingMode): Builder
        fun build(engine: Engine): Material
    }

    fun compile(priority: CompilerPriorityQueue, variants: Int, handler: Any? = null, callback: (() -> Unit)? = null)
    fun createInstance(): MaterialInstance
    fun createInstance(name: String): MaterialInstance
    fun getDefaultInstance(): MaterialInstance
    
    fun getName(): String
    fun getShading(): Shading
    fun getInterpolation(): Interpolation
    fun getBlendingMode(): BlendingMode
    fun getTransparencyMode(): TransparencyMode
    fun getRefractionMode(): RefractionMode
    fun getRefractionType(): RefractionType
    fun getReflectionMode(): ReflectionMode
    fun getVertexDomain(): VertexDomain
    fun getCullingMode(): CullingMode
    fun isColorWriteEnabled(): Boolean
    fun isDepthWriteEnabled(): Boolean
    fun isDepthCullingEnabled(): Boolean
    fun isDoubleSided(): Boolean
    fun isAlphaToCoverageEnabled(): Boolean
    fun getMaskThreshold(): Float
    fun getSpecularAntiAliasingVariance(): Float
    fun getSpecularAntiAliasingThreshold(): Float
    fun getFeatureLevel(): Engine.FeatureLevel
    
    fun getParameterCount(): Int
    // fun getParameters(out: Array<Parameter>? = null): Array<Parameter> // This might be complex due to KMP Array differences
    
    fun getRequiredAttributes(): Set<VertexBuffer.VertexAttribute>
}

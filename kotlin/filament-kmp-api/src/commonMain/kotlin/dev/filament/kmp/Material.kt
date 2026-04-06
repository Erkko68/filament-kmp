package dev.filament.kmp

expect class Material {
    class Builder() {
        fun payload(buffer: Any, size: Int): Builder
        fun build(engine: Engine): Material
    }

    enum class Shading { UNLIT, LIT, SUBSURFACE, CLOTH, SPECULAR_GLOSSINESS }
    enum class BlendingMode { OPAQUE, TRANSPARENT, ADD, MASKED, FADE, MULTIPLY, SCREEN }
    enum class CullingMode { NONE, FRONT, BACK, FRONT_AND_BACK }
    enum class TransparencyMode { DEFAULT, TWO_PASSES_ONE_SIDE, TWO_PASSES_TWO_SIDES }

    fun getName(): String
    fun getShading(): Shading
    fun getBlendingMode(): BlendingMode
    fun getTransparencyMode(): TransparencyMode
    fun getCullingMode(): CullingMode

    fun isColorWriteEnabled(): Boolean
    fun isDepthWriteEnabled(): Boolean
    fun isDepthCullingEnabled(): Boolean
    fun isDoubleSided(): Boolean
    fun isAlphaToCoverageEnabled(): Boolean

    fun getMaskThreshold(): Float
    fun getSpecularAntiAliasingVariance(): Float
    fun getSpecularAntiAliasingThreshold(): Float

    fun createInstance(): MaterialInstance
    fun createInstance(name: String): MaterialInstance
    fun getDefaultInstance(): MaterialInstance
}

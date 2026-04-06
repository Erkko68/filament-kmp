package dev.filament.kmp

expect class Material {
    class Builder() {
        fun payload(buffer: Any, size: Int): Builder
        fun build(engine: Engine): Material
    }

    enum class Shading { UNLIT, LIT, SUBSURFACE, CLOTH, SPECULAR_GLOSSINESS }
    enum class BlendingMode { OPAQUE, TRANSPARENT, ADD, MASKED, FADE, MULTIPLY, SCREEN }
    enum class CullingMode { NONE, FRONT, BACK, FRONT_AND_BACK }

    fun getName(): String
    fun getShading(): Shading
    fun getBlendingMode(): BlendingMode
    fun createInstance(): MaterialInstance
    fun createInstance(name: String): MaterialInstance
    fun getDefaultInstance(): MaterialInstance
}

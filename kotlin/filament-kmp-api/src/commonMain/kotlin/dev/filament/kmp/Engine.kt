package dev.filament.kmp

/**
 * Kotlin-style wrapper over Filament's Engine lifecycle.
 */
expect class Engine {
    val isValid: Boolean

    fun createRenderer(): Renderer

    fun destroyRenderer(renderer: Renderer)

    fun createScene(): Scene

    fun destroyScene(scene: Scene)

    fun createView(): View

    fun destroyView(view: View)

    fun close()

    companion object {
        fun create(config: EngineConfig = EngineConfig()): Engine
    }
}

enum class EngineStereoscopicType {
    NONE,
    INSTANCED,
    MULTIVIEW,
}

data class EngineConfig(
    val stereoscopicType: EngineStereoscopicType = EngineStereoscopicType.NONE,
    val stereoscopicEyeCount: UByte = 1u,
)


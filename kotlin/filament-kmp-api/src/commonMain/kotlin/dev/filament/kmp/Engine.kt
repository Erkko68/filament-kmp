package dev.filament.kmp

/**
 * Kotlin-style wrapper over Filament's Engine lifecycle.
 */
expect class Engine {
    val isValid: Boolean

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


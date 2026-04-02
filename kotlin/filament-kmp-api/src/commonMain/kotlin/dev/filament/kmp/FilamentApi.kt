package dev.filament.kmp

object FilamentApi {
    fun platformLabel(): String = FilamentPlatform.name

    fun interopMode(): String = if (FilamentPlatform.nativeInteropEnabled) {
        "native"
    } else {
        "managed"
    }
}


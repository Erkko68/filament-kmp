package io.github.erkko68.filament.compose.internal

// Top-level js()-body form: the one shape that compiles on both JS and wasmJs.
private fun consoleWarn(message: String): Unit = js("console.warn(message)")

internal actual fun logWarn(message: String) {
    consoleWarn("filament-compose: $message")
}

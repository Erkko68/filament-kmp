package io.github.erkko68.filament.compose.internal

internal actual fun logWarn(message: String) {
    System.err.println("filament-compose: $message")
}

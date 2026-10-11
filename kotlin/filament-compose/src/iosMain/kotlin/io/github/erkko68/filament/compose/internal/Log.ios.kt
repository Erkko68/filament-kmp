package io.github.erkko68.filament.compose.internal

import platform.Foundation.NSLog

internal actual fun logWarn(message: String) {
    // As the format itself: a Kotlin String reaches a C variadic as a char*, which %@ reads as an object and crashes.
    NSLog("filament-compose: $message".replace("%", "%%"))
}

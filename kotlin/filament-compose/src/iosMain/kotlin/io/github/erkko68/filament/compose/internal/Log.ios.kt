package io.github.erkko68.filament.compose.internal

import platform.Foundation.NSLog

internal actual fun logWarn(message: String) {
    NSLog("filament-compose: %@", message)
}

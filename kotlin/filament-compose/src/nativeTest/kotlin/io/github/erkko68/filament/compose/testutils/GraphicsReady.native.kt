package io.github.erkko68.filament.compose.testutils

/** Native (iOS) skiko is loaded synchronously; readiness is `Unit`. */
actual typealias GraphicsReady = Unit

actual fun awaitGraphicsReady(): GraphicsReady = Unit

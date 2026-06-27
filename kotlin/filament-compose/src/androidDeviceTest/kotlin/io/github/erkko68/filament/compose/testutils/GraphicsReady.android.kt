package io.github.erkko68.filament.compose.testutils

/** Android uses the on-device renderer; readiness is `Unit`. */
actual typealias GraphicsReady = Unit

actual fun awaitGraphicsReady(): GraphicsReady = Unit

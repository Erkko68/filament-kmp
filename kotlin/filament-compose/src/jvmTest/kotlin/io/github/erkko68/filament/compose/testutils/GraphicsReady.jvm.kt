package io.github.erkko68.filament.compose.testutils

/** JVM skiko is loaded synchronously; readiness is `Unit` (so the `@BeforeTest` stays `void`). */
actual typealias GraphicsReady = Unit

actual fun awaitGraphicsReady(): GraphicsReady = Unit

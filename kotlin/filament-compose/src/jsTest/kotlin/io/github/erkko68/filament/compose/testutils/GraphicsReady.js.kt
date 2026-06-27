package io.github.erkko68.filament.compose.testutils

import org.jetbrains.skiko.wasm.onWasmReady
import kotlin.js.Promise

/** On JS readiness is a `Promise`; kotlin.test duck-types the `then` of a value returned from a test. */
actual typealias GraphicsReady = Any

/**
 * Resolves once skiko's WASM module has loaded (via skiko's own `onWasmReady`, against the same
 * bundled instance `runComposeUiTest` uses), so the test body runs only after Skia is callable.
 */
actual fun awaitGraphicsReady(): GraphicsReady =
    Promise<Unit> { resolve, _ -> onWasmReady { resolve(Unit) } }

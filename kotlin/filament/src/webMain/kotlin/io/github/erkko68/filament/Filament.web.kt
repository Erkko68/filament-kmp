package io.github.erkko68.filament

import io.github.erkko68.filament.wasm.loadFilament

actual object Filament {
    actual fun init() {
        // filament-kmp.wasm loads asynchronously; use initJs(onReady) to wait for it.
    }

    /**
     * Loads filament-kmp.wasm (the page must include `filament-kmp.js`). [onReady] fires once the
     * module is instantiated; all Filament API usage must happen inside or after it. Calling this
     * again is safe — later calls resolve with the already-loaded module.
     */
    fun initJs(onReady: () -> Unit) {
        loadFilament().then { onReady(); null }
    }
}

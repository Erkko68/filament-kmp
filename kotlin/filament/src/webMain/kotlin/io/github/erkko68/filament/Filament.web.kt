package io.github.erkko68.filament

actual object Filament {
    private var initialized = false

    actual fun init() {
        // On the web the WASM module loads asynchronously.
        // Use initJs(onReady) to wait for readiness.
    }

    /**
     * Initializes the Filament WASM module and exposes the `Filament`
     * namespace members as globals so the external Kotlin declarations
     * (which resolve against the global scope) can find them.
     *
     * [onReady] fires once the WASM module is fully loaded. All Filament
     * API usage must happen inside (or after) this callback.
     *
     * Calling this more than once is safe — subsequent calls invoke
     * [onReady] immediately.
     */
    fun initJs(onReady: () -> Unit) {
        if (initialized) {
            onReady()
            return
        }
        initWasm {
            initialized = true
            onReady()
        }
    }
}

/**
 * Calls the JS-side `Filament.init()`, waits for WASM readiness, then exposes
 * the `Filament` namespace members as globals. Only missing names are added
 * (never overwrites, so e.g. `window.fetch` is untouched); `$`-separated names
 * (`Texture$Builder`) are declared as-is via `@JsName` on the externals, so no
 * `_`-alias copies are needed.
 */
private fun initWasm(onReady: () -> Unit) {
    js("""
        Filament.init([], function() {
            Object.getOwnPropertyNames(Filament).forEach(function(k) {
                if (!(k in globalThis)) globalThis[k] = Filament[k];
            });
            onReady();
        });
    """)
}

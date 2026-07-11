package io.github.erkko68.filament

actual object Filament {
    private var initialized = false

    actual fun init() {
        // On JS the WASM module loads asynchronously.
        // Use initJs(onReady) to wait for readiness.
    }

    /**
     * Initializes the Filament WASM module and spreads the `Filament`
     * namespace onto the global scope so the Karakum-generated externals
     * (which expect top-level globals) can resolve correctly.
     *
     * [onReady] fires once the WASM module is fully loaded and the
     * global aliases are in place. All Filament API usage must happen
     * inside (or after) this callback.
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
 * Calls the JS-side `Filament.init()`, waits for WASM readiness,
 * then spreads the `Filament` namespace onto `window` and creates
 * `_`-separated aliases for every `$`-separated property name.
 */
private fun initWasm(onReady: () -> Unit) {
    js("""
        Filament.init([], function() {
            var nativeFetch = window.fetch;
            Object.assign(window, Filament);
            window.fetch = nativeFetch;
            Object.getOwnPropertyNames(Filament).forEach(function(k) {
                if (k.indexOf('${'$'}') !== -1) window[k.replace(/\${'$'}/g, '_')] = Filament[k];
            });
            onReady();
        });
    """)
}

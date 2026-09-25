package io.github.erkko68.filament.filamat

import io.github.erkko68.filament.filamat.wasm.FilaMaterialBuilder_init
import io.github.erkko68.filament.filamat.wasm.FilaMaterialBuilder_shutdown

actual object Filamat {
    // init()/shutdown() bracket glslang's process init; see Filamat.native.kt.
    actual fun init() = FilaMaterialBuilder_init()

    actual fun shutdown() = FilaMaterialBuilder_shutdown()

    /**
     * Loads filamat-kmp.wasm (the page must include `filamat-kmp.js`). Call [init] and use
     * [MaterialBuilder] only after [onReady] fires. Safe to call again.
     *
     * The compiler runs on a fixed 4 MB wasm stack: a very large shader can overflow it
     * ("memory access out of bounds"), leaving the module unusable until reload. Precompile such
     * materials with matc.
     */
    fun initJs(onReady: () -> Unit) {
        loadingFilamat.then { onReady(); null }
    }
}

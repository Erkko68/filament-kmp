package io.github.erkko68.filament.filamat

/**
 * Loads filamat-kmp.wasm (the page must include `filamat-kmp.js`). Call [Filamat.init] and use
 * [MaterialBuilder] only after [onReady] fires. Safe to call again.
 *
 * The compiler runs on a fixed 4 MB wasm stack: a very large shader can overflow it
 * ("memory access out of bounds"), leaving the module unusable until reload. Precompile such
 * materials with matc.
 */
fun Filamat.initJs(onReady: () -> Unit) {
    loadingFilamat.then { onReady(); null }
}

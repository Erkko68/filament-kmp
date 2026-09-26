package io.github.erkko68.filament.filamat

import io.github.erkko68.filament.filamat.wasm.FilamatC
import kotlin.js.Promise

// filamat-kmp.wasm: the runtime material compiler, a separate module from filament-kmp.wasm (it
// shares no pointers with the engine). Loaded like it: the page includes `filamat-kmp.js`.

private var instance: FilamatC? = null

/** The loaded filamat-kmp.wasm instance; valid once [Filamat.initJs] has called back. */
internal val filamatWasm: FilamatC
    get() = instance ?: (published() ?: error("filamat-kmp.wasm is not loaded: wait for Filamat.initJs")).also { instance = it }

internal val loadingFilamat: Promise<FilamatC> by lazy {
    published()?.let { m -> Promise { resolve, _ -> resolve(m) } }
        ?: createFilamatModule().then { m -> publish(m); m }
}

/** Global factory defined by filamat-kmp.js (`-sEXPORT_NAME=createFilamatModule`). */
private external fun createFilamatModule(): Promise<FilamatC>

private fun published(): FilamatC? = js("globalThis.filamatKmp || null")
private fun publish(module: FilamatC): Unit = js("globalThis.filamatKmp = module")

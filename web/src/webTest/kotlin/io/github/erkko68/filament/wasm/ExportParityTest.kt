package io.github.erkko68.filament.wasm

import kotlin.js.Promise
import kotlin.test.Test
import kotlin.test.assertEquals

/** Every generated external is exported by the wasm with the same arity (catches ABI drift like sret). */
class ExportParityTest {
    @Test
    fun everyExternalIsExportedWithMatchingArity(): Promise<JsAny?> = loadFilament().then { m ->
        val mismatches = WASM_ARITIES.filter { (name, arity) -> exportArity(m, name) != arity }
            .map { (name, arity) -> "$name: expected $arity, wasm ${exportArity(m, name)}" }
        assertEquals(emptyList(), mismatches)
        null
    }
}

// -1 when the export is missing.
private fun exportArity(module: JsAny, name: String): Int =
    js("typeof module[name] === 'function' ? module[name].length : -1")

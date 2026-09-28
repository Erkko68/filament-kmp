package io.github.erkko68.filament.wasm

import kotlin.js.Promise
import kotlin.test.Test
import kotlin.test.assertContentEquals

class F32Test {
    @Test
    fun floatsReadBackEqualTheirLiterals(): Promise<JsAny?> = loadFilament().then { m ->
        val values = floatArrayOf(0.05f, 0.35f, 0.001f, -2.5e-8f, 123456.78f)
        val ptr = m._malloc(values.size * 4)
        m.writeFloats(ptr, values)
        assertContentEquals(values, m.readFloats(ptr, values.size))
        m._free(ptr)
        null
    }
}

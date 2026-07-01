package io.github.erkko68.filament.web.interop

import js.array.JsArray

/**
 * Marshaling helpers between Kotlin number collections and the JS `number[]` arrays
 * that Filament.js expects for vector/matrix parameters (typed `float3`/`mat4`/… =
 * `JsAny?` in the externals). Written against kotlin-wrappers `JsArray<JsNumber>`,
 * which is multiplatform (js + wasmJs) and is a `ReadonlyArray<JsNumber>` supertype,
 * so a single helper feeds both the `JsAny?` and `ReadonlyArray<JsNumber>` externals.
 */

// `Number` vararg/elements are fine here — they're only converted, never crossing the
// JS interop boundary (that's `JsArray<JsNumber>`), so wasmJs's ban on `Number` interop
// doesn't apply.
fun jsNumbers(vararg values: Number): JsArray<JsNumber> {
    val arr = JsArray<JsNumber>()
    values.forEachIndexed { i, v -> arr[i] = v.toDouble().toJsNumber() }
    return arr
}

fun FloatArray.toJsNumbers(): JsArray<JsNumber> {
    val arr = JsArray<JsNumber>()
    forEachIndexed { i, v -> arr[i] = v.toDouble().toJsNumber() }
    return arr
}

fun DoubleArray.toJsNumbers(): JsArray<JsNumber> {
    val arr = JsArray<JsNumber>()
    forEachIndexed { i, v -> arr[i] = v.toJsNumber() }
    return arr
}

fun List<Number>.toJsNumbers(): JsArray<JsNumber> {
    val arr = JsArray<JsNumber>()
    forEachIndexed { i, v -> arr[i] = v.toDouble().toJsNumber() }
    return arr
}

/** Reads a JS `number[]` (as returned by Filament.js) into a DoubleArray of [size]. */
fun JsArray<JsNumber>.toDoubleArray(size: Int): DoubleArray =
    DoubleArray(size) { i -> this[i]?.toDouble() ?: 0.0 }

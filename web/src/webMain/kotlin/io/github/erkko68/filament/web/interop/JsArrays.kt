package io.github.erkko68.filament.web.interop

import js.array.JsArray
import js.array.ReadonlyArray
import js.array.jsArrayOf
import js.array.toJsArray as toJsReadonlyArray

/**
 * Marshaling helpers between Kotlin number collections and the JS `number[]` arrays that
 * Filament.js expects for vector/matrix parameters. The generated externals type these as
 * `JsAny?` (`float3`/`mat4`/…) or `ReadonlyArray<JsNumber>`, so the builders return the
 * common `js.array.ReadonlyArray`, constructed via `jsArrayOf` — the mutable `js.array.JsArray`
 * has no constructor in the shared `webMain` metadata compile, and its bare `JsArray()` /
 * `unsafeCast<JsArray<…>>` forms would bind to stdlib `kotlin.js.JsArray` under Kotlin 2.4.0.
 */

// `Number` vararg/elements are fine here — they're only converted, never crossing the
// JS interop boundary (that's `JsNumber`), so wasmJs's ban on `Number` interop doesn't apply.
fun jsNumbers(vararg values: Number): ReadonlyArray<JsNumber> =
    jsArrayOf(*Array(values.size) { values[it].toDouble().toJsNumber() })

fun FloatArray.toJsNumbers(): ReadonlyArray<JsNumber> =
    jsArrayOf(*Array(size) { this[it].toDouble().toJsNumber() })

fun DoubleArray.toJsNumbers(): ReadonlyArray<JsNumber> =
    jsArrayOf(*Array(size) { this[it].toJsNumber() })

fun List<Number>.toJsNumbers(): ReadonlyArray<JsNumber> =
    jsArrayOf(*Array(size) { this[it].toDouble().toJsNumber() })

/** Reads a JS `number[]` (as returned by Filament.js) into a DoubleArray of [size]. */
fun JsArray<JsNumber>.toDoubleArray(size: Int): DoubleArray =
    DoubleArray(size) { i -> this[i].toDouble() }

/** Reads a JS `number[]` value (typed `JsAny?` in the externals) into a FloatArray of [size]. */
fun JsAny.toFloatArray(size: Int): FloatArray {
    val arr = unsafeCast<js.array.JsArray<JsNumber>>()
    return FloatArray(size) { i -> arr[i].toDouble().toFloat() }
}

/** Reads a JS `number[]` value into an existing [out] FloatArray (up to its size). */
fun JsAny.readNumbersInto(out: FloatArray): FloatArray {
    val arr = unsafeCast<js.array.JsArray<JsNumber>>()
    for (i in 0 until minOf(out.size, arr.size)) out[i] = arr[i].toDouble().toFloat()
    return out
}

/** Reads a JS `number[]` value into an existing [out] DoubleArray (up to its size). */
fun JsAny.readNumbersInto(out: DoubleArray): DoubleArray {
    val arr = unsafeCast<js.array.JsArray<JsNumber>>()
    for (i in 0 until minOf(out.size, arr.size)) out[i] = arr[i].toDouble()
    return out
}

/** Wraps a Kotlin list of JS values into a real JS array. */
fun <T : JsAny?> List<T>.toJsArray(): ReadonlyArray<T> =
    toJsReadonlyArray()

/** A JS boolean array (Filament.js material bool-array params). */
fun List<Boolean>.toJsBooleans(): ReadonlyArray<JsBoolean> =
    jsArrayOf(*Array(size) { this[it].toJsBoolean() })

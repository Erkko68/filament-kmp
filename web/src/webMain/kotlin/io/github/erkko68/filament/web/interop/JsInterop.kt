package io.github.erkko68.filament.web.interop

/**
 * Interop helpers whose `js(...)` bodies must be top-level single expressions on
 * Kotlin/Wasm. Call sites that used inline `js("{}")` / `js("BigInt")(...)` etc. route
 * through these so the same source compiles for both js and wasmJs.
 */

/** A fresh empty JS object, e.g. to populate a Filament option struct (a TS interface). */
fun emptyJsObject(): JsAny = js("{}")

/** `BigInt(s)` — Filament.js's steady-clock frame APIs take a JS BigInt. */
fun jsBigInt(s: String): JsAny = js("BigInt(s)")

/** `Number(x)` — coerces a possible BigInt down to a JS number. */
fun jsNumberOf(x: JsAny?): Double = js("Number(x)")

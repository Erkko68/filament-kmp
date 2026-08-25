package io.github.erkko68.filament.web.interop

/**
 * Interop helpers whose `js(...)` bodies must be top-level single expressions on
 * Kotlin/Wasm. Call sites that used inline `js("{}")` / `js("BigInt")(...)` etc. route
 * through these so the same source compiles for both js and wasmJs.
 */

/** A fresh empty JS object, e.g. to populate a Filament option struct (a TS interface). */
// `({})` — the parens make it an object-literal expression; a bare `{}` is parsed as an
// empty block (returns undefined -> NPE) by the Kotlin/Wasm js() compiler.
fun emptyJsObject(): JsAny = js("({})")

/** `obj[key] = value` — for JS maps keyed by a runtime string, e.g. engine feature flags. */
fun jsSetBoolean(obj: JsAny, key: String, value: Boolean) {
    js("obj[key] = value")
}

/** `BigInt(s)` — Filament.js's steady-clock frame APIs take a JS BigInt. */
fun jsBigInt(s: String): JsAny = js("BigInt(s)")

/** `Number(x)` — coerces a possible BigInt down to a JS number. */
fun jsNumberOf(x: JsAny?): Double = js("Number(x)")

/** Drops a canvas's WebGL context. Browsers cap how many are live at once. */
// No `?.` — the Kotlin/Wasm js() parser rejects optional chaining.
fun loseWebGlContext(canvas: JsAny): Unit =
    js("((gl) => gl && gl.getExtension('WEBGL_lose_context') && gl.getExtension('WEBGL_lose_context').loseContext())(canvas.getContext('webgl2') || canvas.getContext('webgl'))")

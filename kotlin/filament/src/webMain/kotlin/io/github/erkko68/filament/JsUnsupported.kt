package io.github.erkko68.filament

/**
 * Single signaling point for Filament APIs that are not bound on the JS/Web target.
 *
 * Some upstream APIs are simply absent from `filament.js` / `jsbindings.cpp`. The policy
 * for those is to **fail loudly** rather than silently no-op or return a fake value, so a
 * web caller can never mistake "did nothing" for "succeeded". Faithful local emulations
 * (e.g. state mirrored in the wrapper that round-trips correctly) are *not* routed here —
 * this is only for capabilities the web backend genuinely cannot provide.
 *
 * @param api human-readable name of the unbound API, e.g. `"SkinningBuffer.Builder.build"`.
 * @param workaround optional hint pointing the caller at a supported alternative.
 */
/**
 * True if [obj] has a callable member named [name] (own or inherited).
 *
 * Used to feature-detect optional filament.js methods without extracting the function — calling a
 * detached embind method (e.g. `val f = view.setX; f(...)`) loses `this` and aborts with a native
 * BindingError, so probe with this and then invoke as a normal method call.
 */
internal fun jsHasMember(obj: JsAny?, name: String): Boolean =
    js("obj != null && typeof obj[name] === 'function'")

internal fun jsUnsupported(api: String, workaround: String? = null): Nothing {
    val base = "$api is not available on the JS/Web target — it is not bound in upstream " +
        "filament.js (jsbindings.cpp)."
    throw UnsupportedOperationException(if (workaround != null) "$base $workaround" else base)
}

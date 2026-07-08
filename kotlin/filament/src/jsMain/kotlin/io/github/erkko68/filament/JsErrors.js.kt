package io.github.erkko68.filament

internal actual fun <T : Any> catchingJsThrows(block: () -> T): T? = try {
    block()
} catch (e: dynamic) { // catches raw non-Error values that `catch (Throwable)` misses on js
    null
}

package io.github.erkko68.filament

internal actual fun <T : Any> catchingJsThrows(block: () -> T): T? = try {
    block()
} catch (e: Throwable) { // wasmJs surfaces every JS throw as JsException
    null
}

package io.github.erkko68.filament

// Kotlin/JS `catch (Throwable)` misses non-Error JS throws — emscripten surfaces C++ exceptions as
// raw numbers ("the number N was thrown"). wasmJs wraps every JS throw in JsException instead.
internal expect fun <T : Any> catchingJsThrows(block: () -> T): T?

package io.github.erkko68.filament

actual object Filament {
    actual fun init() {
        // On native, Filament is statically linked — no dynamic library loading needed
    }
}

/**
 * Runs [block] inside a C callback. An exception escaping back into C terminates the process,
 * so anything the user's callback throws is printed and swallowed instead.
 */
internal inline fun upcall(block: () -> Unit) {
    try {
        block()
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}

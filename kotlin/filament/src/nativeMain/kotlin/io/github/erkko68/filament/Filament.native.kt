package io.github.erkko68.filament

actual object Filament {
    actual fun init() {
        // On native, Filament is statically linked — no dynamic library loading needed
    }
}

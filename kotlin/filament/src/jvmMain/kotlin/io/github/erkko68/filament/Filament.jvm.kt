package io.github.erkko68.filament

import io.github.erkko68.filament.desktop.FilamentLoader

actual object Filament {
    // Extracts libfilament-c from the platform's runtime jar (once, cached) and loads it.
    actual fun init() = FilamentLoader.load()
}

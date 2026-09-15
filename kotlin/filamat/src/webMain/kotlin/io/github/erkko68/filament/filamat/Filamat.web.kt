package io.github.erkko68.filament.filamat

actual object Filamat {
    actual fun init() {
        // In JS, filamat is part of the WASM bundle initialized via Filament.init()
    }

    actual fun shutdown() {
        // no runtime compiler on web; nothing to release
    }
}

package io.github.erkko68.filament.filamat

import io.github.erkko68.filament.ensureFilamentLoaded
import io.github.erkko68.filament.ffm.FilamentC

actual object Filamat {
    actual fun init() {
        // Load the combined libfilament-c image (idempotent), then init the MaterialBuilder
        // subsystem. filamat lives in the same shared library as filament, so this is the same
        // image kotlin:filament loads — one Filament copy, one EntityManager.
        ensureFilamentLoaded()
        FilamentC.FilaMaterialBuilder_init()
    }
}

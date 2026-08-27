@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package io.github.erkko68.filament.filamat

import io.github.erkko68.filament.filamat.cinterop.FilaMaterialBuilder_shutdown

actual object Filamat {
    actual fun init() {
        // On native, filamat is statically linked — no dynamic library loading needed
    }

    actual fun shutdown() = FilaMaterialBuilder_shutdown()
}

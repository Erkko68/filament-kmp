@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package io.github.erkko68.filament.filamat

import io.github.erkko68.filament.filamat.cinterop.FilaMaterialBuilder_init
import io.github.erkko68.filament.filamat.cinterop.FilaMaterialBuilder_shutdown

actual object Filamat {
    // filamat is statically linked here, but MaterialBuilder still has global compiler state:
    // init()/shutdown() bracket glslang's process init, and shutdown() tears it down whether or
    // not init() ran, so skipping init() both fails every build() and corrupts the pairing.
    actual fun init() = FilaMaterialBuilder_init()

    actual fun shutdown() = FilaMaterialBuilder_shutdown()
}

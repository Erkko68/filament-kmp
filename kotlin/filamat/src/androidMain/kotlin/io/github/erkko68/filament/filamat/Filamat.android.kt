package io.github.erkko68.filament.filamat

import io.github.erkko68.filament.jni.FilaMaterialBuilder_init
import io.github.erkko68.filament.jni.FilaMaterialBuilder_shutdown

actual object Filamat {
    // init()/shutdown() bracket glslang's process init; see Filamat.native.kt.
    actual fun init() = FilaMaterialBuilder_init()

    actual fun shutdown() = FilaMaterialBuilder_shutdown()
}

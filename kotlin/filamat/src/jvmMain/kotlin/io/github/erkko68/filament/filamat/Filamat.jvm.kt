package io.github.erkko68.filament.filamat

actual object Filamat {
    actual fun init() {
        io.github.erkko68.filament.filamat.jni.MaterialBuilder.init()
    }
}

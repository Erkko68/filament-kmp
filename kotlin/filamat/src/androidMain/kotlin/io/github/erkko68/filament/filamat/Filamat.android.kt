package io.github.erkko68.filament.filamat

actual object Filamat {
    actual fun init() {
        com.google.android.filament.filamat.MaterialBuilder.init()
    }

    actual fun shutdown() = com.google.android.filament.filamat.MaterialBuilder.shutdown()
}

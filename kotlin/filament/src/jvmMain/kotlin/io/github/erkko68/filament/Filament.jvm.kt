package io.github.erkko68.filament

actual object Filament {
    actual fun init() {
        io.github.erkko68.filament.jni.Filament.init()
    }
}

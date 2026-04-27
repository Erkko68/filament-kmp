package io.github.erkko68.filament

actual object Filament {
    actual fun init() {
        com.google.android.filament.Filament.init()
    }
}

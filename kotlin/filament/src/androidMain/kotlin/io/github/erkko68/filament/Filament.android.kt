package io.github.erkko68.filament

import io.github.erkko68.filament.jni.FilaJni

actual object Filament {
    actual fun init() = FilaJni.load()
}

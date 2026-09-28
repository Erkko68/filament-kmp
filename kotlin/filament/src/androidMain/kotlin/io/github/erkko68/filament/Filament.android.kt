package io.github.erkko68.filament

actual object Filament {
    // libfilament-c.so ships in the APK, from :android's AAR.
    actual fun init() = System.loadLibrary("filament-c")
}

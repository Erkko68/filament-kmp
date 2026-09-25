package io.github.erkko68.filament.filamat

// Copied out of the wasm heap at build(), so there's no native package to free.
actual class MaterialPackage internal constructor(private val bytes: ByteArray, actual val isValid: Boolean) {
    actual val buffer: ByteArray get() = bytes
}

package io.github.erkko68.filament.filamat

actual class MaterialPackage internal constructor(private val bytes: ByteArray = ByteArray(0)) {
    actual val buffer: ByteArray get() = bytes

    actual val isValid: Boolean get() = bytes.isNotEmpty()
}

package io.github.erkko68.filament.filamat

actual class MaterialPackage(private val buffer: ByteArray = ByteArray(0)) {
    actual fun getBuffer(): ByteArray {
        return buffer
    }

    actual fun isValid(): Boolean {
        return buffer.isNotEmpty()
    }
}
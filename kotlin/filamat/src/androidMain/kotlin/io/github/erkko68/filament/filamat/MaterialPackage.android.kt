package io.github.erkko68.filament.filamat

import com.google.android.filament.filamat.MaterialPackage as JavaMaterialPackage

actual class MaterialPackage internal constructor(private val javaPackage: JavaMaterialPackage) {
    actual fun getBuffer(): ByteArray {
        val buffer = javaPackage.buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        return bytes
    }

    actual fun isValid(): Boolean = javaPackage.isValid
}

package io.github.erkko68.filament.filamat

import com.google.android.filament.filamat.MaterialPackage as JavaMaterialPackage

actual class MaterialPackage internal constructor(internal val javaPackage: JavaMaterialPackage) {
    actual val buffer: ByteArray get() {
        val buffer = javaPackage.buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        return bytes
    }

    actual val isValid: Boolean get() = javaPackage.isValid
}

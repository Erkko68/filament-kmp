package io.github.erkko68.filament.filamat

import io.github.erkko68.filament.filamat.jni.MaterialPackage as JniMaterialPackage
import java.nio.ByteBuffer

actual class MaterialPackage(val nativePackage: JniMaterialPackage) {
    actual fun getBuffer(): ByteArray {
        val buffer = nativePackage.buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        return bytes
    }

    actual fun isValid(): Boolean = nativePackage.isValid
}

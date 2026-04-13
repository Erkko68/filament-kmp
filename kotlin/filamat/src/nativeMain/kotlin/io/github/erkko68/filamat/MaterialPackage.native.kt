package io.github.erkko68.filamat

import kotlinx.cinterop.*
import cnames.structs.FilaPackage
import io.github.erkko68.filamat.cinterop.*

@OptIn(ExperimentalForeignApi::class)
actual class MaterialPackage internal constructor(private val nativeHandle: CPointer<FilaPackage>?) {
    actual fun getBuffer(): ByteArray {
        val size = FilaPackage_getSize(nativeHandle).toInt()
        val data = FilaPackage_getData(nativeHandle)
        if (data == null || size <= 0) return ByteArray(0)
        
        val bytes = ByteArray(size)
        // Copy data from native to Kotlin ByteArray
        val bytePtr = data.reinterpret<ByteVar>()
        for (i in 0 until size) {
            bytes[i] = bytePtr[i]
        }
        return bytes
    }

    actual fun isValid(): Boolean = FilaPackage_isValid(nativeHandle)

    protected fun finalize() {
        FilaPackage_destroy(nativeHandle)
    }
}

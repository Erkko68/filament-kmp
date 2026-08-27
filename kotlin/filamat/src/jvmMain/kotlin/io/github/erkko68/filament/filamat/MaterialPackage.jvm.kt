package io.github.erkko68.filament.filamat

import io.github.erkko68.filament.ffm.FilamentC
import io.github.erkko68.filament.readBytes
import java.lang.foreign.MemorySegment
import io.github.erkko68.filament.InternalFilamentApi

actual class MaterialPackage @InternalFilamentApi constructor(internal var nativeHandle: MemorySegment?) {
    actual fun getBuffer(): ByteArray {
        val size = FilamentC.FilaPackage_getSize(nativeHandle).toInt()
        return FilamentC.FilaPackage_getData(nativeHandle).readBytes(size)
    }

    actual fun isValid(): Boolean = FilamentC.FilaPackage_isValid(nativeHandle)

    protected fun finalize() {
        nativeHandle?.let { FilamentC.FilaPackage_destroy(it) }
        nativeHandle = null
    }
}

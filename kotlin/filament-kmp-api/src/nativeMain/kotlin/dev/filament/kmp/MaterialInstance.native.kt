@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package dev.filament.kmp

import kotlinx.cinterop.*
import dev.filament.kmp.cinterop.*
import cnames.structs.FilaMaterialInstance

actual class MaterialInstance internal constructor(internal var nativeHandle: CPointer<FilaMaterialInstance>?) {
    actual fun getName(): String = FilaMaterialInstance_getName(nativeHandle)?.toKString() ?: ""
    actual fun setParameter(name: String, x: Float) = FilaMaterialInstance_setParameterFloat(nativeHandle, name, x)
    actual fun setParameter(name: String, x: Float, y: Float) = FilaMaterialInstance_setParameterFloat2(nativeHandle, name, x, y)
    actual fun setParameter(name: String, x: Float, y: Float, z: Float) = FilaMaterialInstance_setParameterFloat3(nativeHandle, name, x, y, z)
    actual fun setParameter(name: String, x: Float, y: Float, z: Float, w: Float) = FilaMaterialInstance_setParameterFloat4(nativeHandle, name, x, y, z, w)
    actual fun setParameter(name: String, x: Int) = FilaMaterialInstance_setParameterInt(nativeHandle, name, x)
    actual fun setParameter(name: String, x: Boolean) = FilaMaterialInstance_setParameterBool(nativeHandle, name, x)
}

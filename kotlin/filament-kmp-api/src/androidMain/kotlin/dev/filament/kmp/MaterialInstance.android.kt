package dev.filament.kmp

import com.google.android.filament.MaterialInstance as AndroidMaterialInstance

actual class MaterialInstance internal constructor(val nativeMaterialInstance: AndroidMaterialInstance) {
    actual fun getName(): String = nativeMaterialInstance.name
    actual fun setParameter(name: String, x: Float) = nativeMaterialInstance.setParameter(name, x)
    actual fun setParameter(name: String, x: Float, y: Float) = nativeMaterialInstance.setParameter(name, x, y)
    actual fun setParameter(name: String, x: Float, y: Float, z: Float) = nativeMaterialInstance.setParameter(name, x, y, z)
    actual fun setParameter(name: String, x: Float, y: Float, z: Float, w: Float) = nativeMaterialInstance.setParameter(name, x, y, z, w)
    actual fun setParameter(name: String, x: Int) = nativeMaterialInstance.setParameter(name, x)
    actual fun setParameter(name: String, x: Boolean) = nativeMaterialInstance.setParameter(name, x)
}

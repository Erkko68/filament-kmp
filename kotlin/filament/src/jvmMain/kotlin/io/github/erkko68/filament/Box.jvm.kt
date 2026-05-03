package io.github.erkko68.filament

import io.github.erkko68.filament.jni.Box as JniBox

actual class Box internal constructor(val nativeBox: JniBox) {
    actual constructor() : this(JniBox())
    actual constructor(centerX: Float, centerY: Float, centerZ: Float, halfExtentX: Float, halfExtentY: Float, halfExtentZ: Float) : 
        this(JniBox(centerX, centerY, centerZ, halfExtentX, halfExtentY, halfExtentZ))
    
    actual val center: FloatArray get() = nativeBox.center
    actual val halfExtent: FloatArray get() = nativeBox.halfExtent
    
    actual fun setCenter(x: Float, y: Float, z: Float) {
        nativeBox.setCenter(x, y, z)
    }
    actual fun setHalfExtent(x: Float, y: Float, z: Float) {
        nativeBox.setHalfExtent(x, y, z)
    }

    actual val min: FloatArray get() {
        val c = nativeBox.center; val h = nativeBox.halfExtent
        return floatArrayOf(c[0] - h[0], c[1] - h[1], c[2] - h[2])
    }
    actual val max: FloatArray get() {
        val c = nativeBox.center; val h = nativeBox.halfExtent
        return floatArrayOf(c[0] + h[0], c[1] + h[1], c[2] + h[2])
    }
}

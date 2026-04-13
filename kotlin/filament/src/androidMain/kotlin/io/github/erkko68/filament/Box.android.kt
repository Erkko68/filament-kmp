package io.github.erkko68.filament

actual class Box internal constructor(val nativeBox: com.google.android.filament.Box) {
    actual constructor() : this(com.google.android.filament.Box())
    actual constructor(centerX: Float, centerY: Float, centerZ: Float, halfExtentX: Float, halfExtentY: Float, halfExtentZ: Float) : 
        this(com.google.android.filament.Box(centerX, centerY, centerZ, halfExtentX, halfExtentY, halfExtentZ))
    
    actual val center: FloatArray get() = nativeBox.center
    actual val halfExtent: FloatArray get() = nativeBox.halfExtent
    
    actual fun setCenter(x: Float, y: Float, z: Float) {
        nativeBox.setCenter(x, y, z)
    }
    actual fun setHalfExtent(x: Float, y: Float, z: Float) {
        nativeBox.setHalfExtent(x, y, z)
    }

    actual fun getMin(): FloatArray {
        val center = nativeBox.center
        val half = nativeBox.halfExtent
        return floatArrayOf(center[0] - half[0], center[1] - half[1], center[2] - half[2])
    }

    actual fun getMax(): FloatArray {
        val center = nativeBox.center
        val half = nativeBox.halfExtent
        return floatArrayOf(center[0] + half[0], center[1] + half[1], center[2] + half[2])
    }
}

package io.github.erkko68.filament

actual class Box {
    actual val center: FloatArray
        get() = TODO("Not yet implemented")
    actual val halfExtent: FloatArray
        get() = TODO("Not yet implemented")

    actual fun setCenter(x: Float, y: Float, z: Float) {
    }

    actual fun setHalfExtent(x: Float, y: Float, z: Float) {
    }

    actual fun getMin(): FloatArray {
        TODO("Not yet implemented")
    }

    actual fun getMax(): FloatArray {
        TODO("Not yet implemented")
    }

    actual constructor() {
        TODO("Not yet implemented")
    }

    actual constructor(
        centerX: Float,
        centerY: Float,
        centerZ: Float,
        halfExtentX: Float,
        halfExtentY: Float,
        halfExtentZ: Float
    ) {
        TODO("Not yet implemented")
    }
}
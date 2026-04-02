package dev.filament.kmp

actual class Box {
    actual constructor() {
    }

    actual constructor(
        centerX: Float,
        centerY: Float,
        centerZ: Float,
        halfExtentX: Float,
        halfExtentY: Float,
        halfExtentZ: Float,
    ) {
    }

    actual constructor(center: FloatArray, halfExtent: FloatArray) {
    }

    actual fun setCenter(centerX: Float, centerY: Float, centerZ: Float) {
        TODO("Not yet implemented")
    }

    actual fun setHalfExtent(halfExtentX: Float, halfExtentY: Float, halfExtentZ: Float) {
        TODO("Not yet implemented")
    }

    actual fun getCenter(): FloatArray {
        TODO("Not yet implemented")
    }

    actual fun getHalfExtent(): FloatArray {
        TODO("Not yet implemented")
    }
}


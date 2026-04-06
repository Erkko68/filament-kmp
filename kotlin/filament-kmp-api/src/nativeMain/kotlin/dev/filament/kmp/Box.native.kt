package dev.filament.kmp

actual class Box {
    private val centerInternal = FloatArray(3)
    private val halfExtentInternal = FloatArray(3)

    actual constructor() {
        centerInternal[0] = 0f
        centerInternal[1] = 0f
        centerInternal[2] = 0f
        halfExtentInternal[0] = 0f
        halfExtentInternal[1] = 0f
        halfExtentInternal[2] = 0f
    }

    actual constructor(
        centerX: Float,
        centerY: Float,
        centerZ: Float,
        halfExtentX: Float,
        halfExtentY: Float,
        halfExtentZ: Float,
    ) {
        centerInternal[0] = centerX
        centerInternal[1] = centerY
        centerInternal[2] = centerZ
        halfExtentInternal[0] = halfExtentX
        halfExtentInternal[1] = halfExtentY
        halfExtentInternal[2] = halfExtentZ
    }

    actual constructor(center: FloatArray, halfExtent: FloatArray) {
        center.copyInto(centerInternal, 0, 0, 3)
        halfExtent.copyInto(halfExtentInternal, 0, 0, 3)
    }

    actual fun setCenter(centerX: Float, centerY: Float, centerZ: Float) {
        centerInternal[0] = centerX
        centerInternal[1] = centerY
        centerInternal[2] = centerZ
    }

    actual fun setHalfExtent(halfExtentX: Float, halfExtentY: Float, halfExtentZ: Float) {
        halfExtentInternal[0] = halfExtentX
        halfExtentInternal[1] = halfExtentY
        halfExtentInternal[2] = halfExtentZ
    }

    actual val center: FloatArray
        get() = centerInternal

    actual val halfExtent: FloatArray
        get() = halfExtentInternal
}

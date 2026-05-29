package io.github.erkko68.filament

actual class Box(
    actual val center: FloatArray = FloatArray(3),
    actual val halfExtent: FloatArray = FloatArray(3)
) {
    actual constructor() : this(FloatArray(3), FloatArray(3))

    actual constructor(centerX: Float, centerY: Float, centerZ: Float, halfExtentX: Float, halfExtentY: Float, halfExtentZ: Float) : this(
        floatArrayOf(centerX, centerY, centerZ),
        floatArrayOf(halfExtentX, halfExtentY, halfExtentZ)
    )

    actual fun setCenter(x: Float, y: Float, z: Float) {
        center[0] = x
        center[1] = y
        center[2] = z
    }

    actual fun setHalfExtent(x: Float, y: Float, z: Float) {
        halfExtent[0] = x
        halfExtent[1] = y
        halfExtent[2] = z
    }

    actual val min: FloatArray get() = floatArrayOf(center[0] - halfExtent[0], center[1] - halfExtent[1], center[2] - halfExtent[2])
    actual val max: FloatArray get() = floatArrayOf(center[0] + halfExtent[0], center[1] + halfExtent[1], center[2] + halfExtent[2])
}

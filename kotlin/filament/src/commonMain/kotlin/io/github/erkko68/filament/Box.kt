package io.github.erkko68.filament

/**
 * An axis-aligned 3D box represented by its center and half-extent.
 */
class Box(
    val center: FloatArray = FloatArray(3),
    val halfExtent: FloatArray = FloatArray(3)
) {
    constructor() : this(FloatArray(3), FloatArray(3))

    constructor(centerX: Float, centerY: Float, centerZ: Float, halfExtentX: Float, halfExtentY: Float, halfExtentZ: Float) : this(
        floatArrayOf(centerX, centerY, centerZ),
        floatArrayOf(halfExtentX, halfExtentY, halfExtentZ)
    )

    fun setCenter(x: Float, y: Float, z: Float) {
        center[0] = x
        center[1] = y
        center[2] = z
    }

    fun setHalfExtent(x: Float, y: Float, z: Float) {
        halfExtent[0] = x
        halfExtent[1] = y
        halfExtent[2] = z
    }

    val min: FloatArray get() = floatArrayOf(center[0] - halfExtent[0], center[1] - halfExtent[1], center[2] - halfExtent[2])
    val max: FloatArray get() = floatArrayOf(center[0] + halfExtent[0], center[1] + halfExtent[1], center[2] + halfExtent[2])
}

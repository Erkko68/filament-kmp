package dev.filament.kmp

/**
 * An axis-aligned 3D box represented by its center and half-extent.
 */
class Box(
    val center: FloatArray = floatArrayOf(0.0f, 0.0f, 0.0f),
    val halfExtent: FloatArray = floatArrayOf(0.0f, 0.0f, 0.0f)
) {
    constructor(
        centerX: Float, centerY: Float, centerZ: Float,
        halfExtentX: Float, halfExtentY: Float, halfExtentZ: Float
    ) : this(
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
}

package io.github.erkko68.filament

actual class Box {
    private val _center = FloatArray(3)
    private val _halfExtent = FloatArray(3)

    actual val center: FloatArray
        get() = _center
    actual val halfExtent: FloatArray
        get() = _halfExtent

    actual fun setCenter(x: Float, y: Float, z: Float) {
        _center[0] = x
        _center[1] = y
        _center[2] = z
    }

    actual fun setHalfExtent(x: Float, y: Float, z: Float) {
        _halfExtent[0] = x
        _halfExtent[1] = y
        _halfExtent[2] = z
    }

    actual val min: FloatArray get() = floatArrayOf(_center[0] - _halfExtent[0], _center[1] - _halfExtent[1], _center[2] - _halfExtent[2])
    actual val max: FloatArray get() = floatArrayOf(_center[0] + _halfExtent[0], _center[1] + _halfExtent[1], _center[2] + _halfExtent[2])

    actual constructor()

    actual constructor(
        centerX: Float,
        centerY: Float,
        centerZ: Float,
        halfExtentX: Float,
        halfExtentY: Float,
        halfExtentZ: Float
    ) {
        setCenter(centerX, centerY, centerZ)
        setHalfExtent(halfExtentX, halfExtentY, halfExtentZ)
    }
}
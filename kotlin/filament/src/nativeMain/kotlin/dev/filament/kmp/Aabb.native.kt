package dev.filament.kmp

actual class Aabb {
    actual val min: FloatArray = FloatArray(3)
    actual val max: FloatArray = FloatArray(3)
    
    actual constructor()
    actual constructor(minX: Float, minY: Float, minZ: Float, maxX: Float, maxY: Float, maxZ: Float) {
        setMin(minX, minY, minZ)
        setMax(maxX, maxY, maxZ)
    }
    
    actual fun setMin(x: Float, y: Float, z: Float) {
        min[0] = x; min[1] = y; min[2] = z
    }
    actual fun setMax(x: Float, y: Float, z: Float) {
        max[0] = x; max[1] = y; max[2] = z
    }
    
    actual fun center(): FloatArray = floatArrayOf(
        (min[0] + max[0]) * 0.5f,
        (min[1] + max[1]) * 0.5f,
        (min[2] + max[2]) * 0.5f
    )
    
    actual fun halfExtent(): FloatArray = floatArrayOf(
        (max[0] - min[0]) * 0.5f,
        (max[1] - min[1]) * 0.5f,
        (max[2] - min[2]) * 0.5f
    )
}

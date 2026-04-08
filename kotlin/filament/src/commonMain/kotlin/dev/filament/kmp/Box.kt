package dev.filament.kmp

/**
 * An axis-aligned 3D box represented by its center and half-extent.
 */
expect class Box {
    constructor()
    constructor(centerX: Float, centerY: Float, centerZ: Float, halfExtentX: Float, halfExtentY: Float, halfExtentZ: Float)
    
    val center: FloatArray
    val halfExtent: FloatArray
    
    fun setCenter(x: Float, y: Float, z: Float)
    fun setHalfExtent(x: Float, y: Float, z: Float)

    fun getMin(): FloatArray
    fun getMax(): FloatArray
}

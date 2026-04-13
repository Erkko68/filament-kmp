package io.github.erkko68

/**
 * An axis-aligned 3D box represented by its min and max coordinates.
 */
expect class Aabb {
    constructor()
    constructor(minX: Float, minY: Float, minZ: Float, maxX: Float, maxY: Float, maxZ: Float)
    
    val min: FloatArray
    val max: FloatArray
    
    fun setMin(x: Float, y: Float, z: Float)
    fun setMax(x: Float, y: Float, z: Float)
    
    fun center(): FloatArray
    fun halfExtent(): FloatArray
}

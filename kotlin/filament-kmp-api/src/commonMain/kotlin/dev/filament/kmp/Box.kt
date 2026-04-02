package dev.filament.kmp

/**
 * An axis-aligned 3D box represented by its center and half-extent.
 *
 * The half-extent is a vector representing the distance from the center to the edge of the box in
 * each dimension. For example, a box of size 2 units in X, 4 units in Y, and 10 units in Z would
 * have a half-extent of (1, 2, 5).
 */
expect class Box {
    /**
     * Default-initializes the 3D box to have a center and half-extent of (0,0,0).
     */
    constructor()

    /**
     * Initializes the 3D box from its center and half-extent.
     */
    constructor(
        centerX: Float,
        centerY: Float,
        centerZ: Float,
        halfExtentX: Float,
        halfExtentY: Float,
        halfExtentZ: Float,
    )

    /**
     * Initializes the 3D box from its center and half-extent.
     *
     * @param center a float array with XYZ coordinates representing the center of the box
     * @param halfExtent a float array with XYZ coordinates representing half the size of the box in
     * each dimension
     */
    constructor(center: FloatArray, halfExtent: FloatArray)

    /**
     * Sets the center of of the 3D box.
     */
    fun setCenter(centerX: Float, centerY: Float, centerZ: Float)

    /**
     * Sets the half-extent of the 3D box.
     */
    fun setHalfExtent(halfExtentX: Float, halfExtentY: Float, halfExtentZ: Float)

    /**
     * Returns the center of the 3D box.
     *
     * @return an XYZ float array of size 3
     */
    fun getCenter(): FloatArray

    /**
     * Returns the half-extent from the center of the 3D box.
     *
     * @return an XYZ float array of size 3
     */
    fun getHalfExtent(): FloatArray
}


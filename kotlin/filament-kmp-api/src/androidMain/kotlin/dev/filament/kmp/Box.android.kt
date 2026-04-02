package dev.filament.kmp

import com.google.android.filament.Box as AndroidBox

actual class Box {
    internal val androidBox: AndroidBox

    /**
     * Default-initializes the 3D box to have a center and half-extent of (0,0,0).
     */
    actual constructor() {
        androidBox = AndroidBox()
    }

    /**
     * Initializes the 3D box from its center and half-extent.
     */
    actual constructor(
        centerX: Float,
        centerY: Float,
        centerZ: Float,
        halfExtentX: Float,
        halfExtentY: Float,
        halfExtentZ: Float,
    ) {
        androidBox = AndroidBox(
            centerX,
            centerY,
            centerZ,
            halfExtentX,
            halfExtentY,
            halfExtentZ,
        )
    }

    /**
     * Initializes the 3D box from its center and half-extent.
     *
     * @param center a float array with XYZ coordinates representing the center of the box
     * @param halfExtent a float array with XYZ coordinates representing half the size of the box in
     * each dimension
     */
    actual constructor(center: FloatArray, halfExtent: FloatArray) {
        androidBox = AndroidBox(center, halfExtent)
    }

    /**
     * Sets the center of of the 3D box.
     */
    actual fun setCenter(centerX: Float, centerY: Float, centerZ: Float) {
        androidBox.setCenter(centerX, centerY, centerZ)
    }

    /**
     * Sets the half-extent of the 3D box.
     */
    actual fun setHalfExtent(halfExtentX: Float, halfExtentY: Float, halfExtentZ: Float) {
        androidBox.setHalfExtent(halfExtentX, halfExtentY, halfExtentZ)
    }

    /**
     * Returns the center of the 3D box.
     *
     * @return an XYZ float array of size 3
     */
    actual fun getCenter(): FloatArray = androidBox.center

    /**
     * Returns the half-extent from the center of the 3D box.
     *
     * @return an XYZ float array of size 3
     */
    actual fun getHalfExtent(): FloatArray = androidBox.halfExtent
}


package dev.filament.kmp

/**
 * Camera represents the eye through which the scene is viewed.
 * <p>
 * A Camera has a position and orientation and controls the projection and exposure parameters.
 *
 * <h1>Creation and destruction</h1>
 *
 * In Filament, Camera is a component that must be associated with an entity. To do so,
 * use [Engine.createCamera]. A Camera component is destroyed using
 * [Engine.destroyCameraComponent].
 *
 * <pre>
 *  val myCamera = engine.createCamera(myCameraEntity)
 *  myCamera.setProjection(Projection.PERSPECTIVE, 45.0, 16.0/9.0, 0.1, 1.0)
 *  myCamera.lookAt(0.0, 1.60, 1.0,
 *                  0.0, 0.0, 0.0,
 *                  0.0, 1.0, 0.0)
 *  engine.destroyCameraComponent(myCameraEntity)
 * </pre>
 *
 *
 * <h1>Coordinate system</h1>
 *
 * The camera coordinate system defines the <b>view space</b>. The camera points towards its -z axis
 * and is oriented such that its top side is in the direction of +y, and its right side in the
 * direction of +x.
 * <p>
 * Since the <b>near</b> and <b>far</b> planes are defined by the distance from the camera,
 * their respective coordinates are -distance<sub>near</sub> and -distance<sub>far</sub>.
 *
 * <h1>Clipping planes</h1>
 *
 * The camera defines six <b>clipping planes</b> which together create a <b>clipping volume</b>. The
 * geometry outside this volume is clipped.
 * <p>
 * The clipping volume can either be a box or a frustum depending on which projection is used,
 * respectively [Projection.ORTHO] or [Projection.PERSPECTIVE].
 * The six planes are specified either directly or indirectly using [setProjection] or
 * [setLensProjection].
 *
 * <h1>Choosing the near plane distance</h1>
 *
 * The <b>near</b> plane distance greatly affects the depth-buffer resolution.
 * <p>
 * Make sure to pick the highest <b>near</b> plane distance possible.
 *
 * <h1>Exposure</h1>
 *
 * The Camera is also used to set the scene's exposure, just like with a real camera. The lights
 * intensity and the Camera exposure interact to produce the final scene's brightness.
 *
 * @see View
 */
expect class Camera {
    /**
     * Denotes the projection type used by this camera.
     * @see setProjection
     */
    enum class Projection {
        /** Perspective projection, objects get smaller as they are farther.  */
        PERSPECTIVE,
        /** Orthonormal projection, preserves distances. */
        ORTHO
    }

    /**
     * Denotes a field-of-view direction.
     * @see setProjection
     */
    enum class Fov {
        /** The field-of-view angle is defined on the vertical axis. */
        VERTICAL,
        /** The field-of-view angle is defined on the horizontal axis. */
        HORIZONTAL
    }

    /**
     * Sets the projection matrix from a frustum defined by six planes.
     */
    fun setProjection(projection: Projection, left: Double, right: Double, 
                      bottom: Double, top: Double, near: Double, far: Double)

    /**
     * Sets the projection matrix from the field-of-view.
     */
    fun setProjection(fovInDegrees: Double, aspect: Double, near: Double, far: Double,
                      direction: Fov)

    /**
     * Sets the projection matrix from the focal length.
     */
    fun setLensProjection(focalLength: Double, aspect: Double, near: Double, far: Double)

    /**
     * Sets a custom projection matrix.
     */
    fun setCustomProjection(inProjection: DoubleArray, near: Double, far: Double)

    /**
     * Sets a custom projection matrix.
     */
    fun setCustomProjection(inProjection: DoubleArray, inProjectionForCulling: DoubleArray, 
                            near: Double, far: Double)

    /**
     * Sets an additional matrix that scales the projection matrix.
     */
    fun setScaling(xscaling: Double, yscaling: Double)

    /**
     * Sets a custom projection matrix for each eye.
     */
    fun setCustomEyeProjection(inProjection: DoubleArray, count: Int,
                               inProjectionForCulling: DoubleArray, near: Double, far: Double)

    /**
     * Sets an additional matrix that scales the projection matrix.
     */
    @Deprecated("use setScaling(double, double)")
    fun setScaling(inScaling: DoubleArray)

    /**
     * Sets an additional matrix that shifts (translates) the projection matrix.
     */
    fun setShift(xshift: Double, yshift: Double)

    /**
     * Returns the shift amount used to translate the projection matrix.
     */
    fun getShift(out: DoubleArray?): DoubleArray

    /**
     * Returns the camera's field of view in degrees.
     */
    fun getFieldOfViewInDegrees(direction: Fov): Double

    /**
     * Sets the camera's model matrix.
     */
    fun setModelMatrix(modelMatrix: FloatArray)

    /**
     * Sets the camera's model matrix.
     */
    fun setModelMatrix(modelMatrix: DoubleArray)

    /**
     * Sets the camera's model matrix.
     */
    fun lookAt(eyeX: Double, eyeY: Double, eyeZ: Double,
               centerX: Double, centerY: Double, centerZ: Double, 
               upX: Double, upY: Double, upZ: Double)

    /**
     * Gets the distance to the near plane
     */
    fun getNear(): Float

    /**
     * Gets the distance to the far plane
     */
    fun getCullingFar(): Float

    /**
     * Retrieves the camera's projection matrix.
     */
    fun getProjectionMatrix(out: DoubleArray?): DoubleArray

    /**
     * Retrieves the camera's culling matrix.
     */
    fun getCullingProjectionMatrix(out: DoubleArray?): DoubleArray

    /**
     * Returns the scaling amount used to scale the projection matrix.
     */
    fun getScaling(out: DoubleArray?): DoubleArray

    /**
     * Retrieves the camera's model matrix.
     */
    fun getModelMatrix(out: FloatArray?): FloatArray

    /**
     * Retrieves the camera's model matrix.
     */
    fun getModelMatrix(out: DoubleArray?): DoubleArray

    /**
     * Retrieves the camera's view matrix.
     */
    fun getViewMatrix(out: FloatArray?): FloatArray

    /**
     * Retrieves the camera's view matrix.
     */
    fun getViewMatrix(out: DoubleArray?): DoubleArray

    /**
     * Retrieves the camera position in world space.
     */
    fun getPosition(out: FloatArray?): FloatArray

    /**
     * Retrieves the camera left unit vector in world space.
     */
    fun getLeftVector(out: FloatArray?): FloatArray

    /**
     * Retrieves the camera up unit vector in world space.
     */
    fun getUpVector(out: FloatArray?): FloatArray

    /**
     * Retrieves the camera forward unit vector in world space.
     */
    fun getForwardVector(out: FloatArray?): FloatArray

    /**
     * Sets this camera's exposure (default is f/16, 1/125s, 100 ISO)
     */
    fun setExposure(aperture: Float, shutterSpeed: Float, sensitivity: Float)

    /**
     * Sets this camera's exposure directly.
     */
    fun setExposure(exposure: Float)

    /**
     * Gets the aperture in f-stops
     */
    fun getAperture(): Float

    /**
     * Gets the shutter speed in seconds
     */
    fun getShutterSpeed(): Float

    /**
     * Gets the focal length in meters
     */
    fun getFocalLength(): Double

    /**
     * Set the camera focus distance in world units
     */
    fun setFocusDistance(distance: Float)

    /**
     * Gets the distance from the camera to the focus plane in world units
     */
    fun getFocusDistance(): Float

    /**
     * Gets the sensitivity in ISO
     */
    fun getSensitivity(): Float

    /**
     * Gets the entity representing this Camera
     */
    fun getEntity(): Int

    /**
     * Sets the model matrix for a specific eye.
     */
    fun setEyeModelMatrix(eyeId: Int, model: DoubleArray)

    companion object {
        /**
         * Helper to compute the effective focal length taking into account the focus distance
         */
        fun computeEffectiveFocalLength(focalLength: Double, focusDistance: Double): Double

        /**
         * Helper to compute the effective field-of-view taking into account the focus distance
         */
        fun computeEffectiveFov(fovInDegrees: Double, focusDistance: Double): Double
    }
}

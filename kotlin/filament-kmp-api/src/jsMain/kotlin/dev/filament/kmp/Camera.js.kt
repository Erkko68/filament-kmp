package dev.filament.kmp

actual class Camera {
    actual fun setProjection(projection: Projection, left: Double, right: Double, bottom: Double, top: Double, near: Double, far: Double) {
        TODO("Not yet implemented")
    }

    actual fun setProjection(fovInDegrees: Double, aspect: Double, near: Double, far: Double, direction: Fov) {
        TODO("Not yet implemented")
    }

    actual fun setLensProjection(focalLength: Double, aspect: Double, near: Double, far: Double) {
        TODO("Not yet implemented")
    }

    actual fun setCustomProjection(inProjection: DoubleArray, near: Double, far: Double) {
        TODO("Not yet implemented")
    }

    actual fun setCustomProjection(inProjection: DoubleArray, inProjectionForCulling: DoubleArray, near: Double, far: Double) {
        TODO("Not yet implemented")
    }

    actual fun setScaling(xscaling: Double, yscaling: Double) {
        TODO("Not yet implemented")
    }

    @Deprecated("Use setScaling(xscaling, yscaling)")
    actual fun setScaling(inScaling: DoubleArray) {
        TODO("Not yet implemented")
    }

    actual fun setCustomEyeProjection(inProjection: DoubleArray, count: Int, inProjectionForCulling: DoubleArray, near: Double, far: Double) {
        TODO("Not yet implemented")
    }

    actual fun setShift(xshift: Double, yshift: Double) {
        TODO("Not yet implemented")
    }

    actual fun getShift(out: DoubleArray?): DoubleArray = TODO("Not yet implemented")

    actual fun getFieldOfViewInDegrees(direction: Fov): Double = TODO("Not yet implemented")

    actual fun setModelMatrix(modelMatrix: FloatArray) {
        TODO("Not yet implemented")
    }

    actual fun setModelMatrix(modelMatrix: DoubleArray) {
        TODO("Not yet implemented")
    }

    actual fun lookAt(eyeX: Double, eyeY: Double, eyeZ: Double, centerX: Double, centerY: Double, centerZ: Double, upX: Double, upY: Double, upZ: Double) {
        TODO("Not yet implemented")
    }

    actual fun getNear(): Float = TODO("Not yet implemented")

    actual fun getCullingFar(): Float = TODO("Not yet implemented")

    actual fun getProjectionMatrix(out: DoubleArray?): DoubleArray = TODO("Not yet implemented")

    actual fun getCullingProjectionMatrix(out: DoubleArray?): DoubleArray = TODO("Not yet implemented")

    actual fun getScaling(out: DoubleArray?): DoubleArray = TODO("Not yet implemented")

    actual fun getModelMatrix(out: FloatArray?): FloatArray = TODO("Not yet implemented")

    actual fun getModelMatrix(out: DoubleArray?): DoubleArray = TODO("Not yet implemented")

    actual fun getViewMatrix(out: FloatArray?): FloatArray = TODO("Not yet implemented")

    actual fun getViewMatrix(out: DoubleArray?): DoubleArray = TODO("Not yet implemented")

    actual fun getPosition(out: FloatArray?): FloatArray = TODO("Not yet implemented")

    actual fun getLeftVector(out: FloatArray?): FloatArray = TODO("Not yet implemented")

    actual fun getUpVector(out: FloatArray?): FloatArray = TODO("Not yet implemented")

    actual fun getForwardVector(out: FloatArray?): FloatArray = TODO("Not yet implemented")

    actual fun setExposure(aperture: Float, shutterSpeed: Float, sensitivity: Float) {
        TODO("Not yet implemented")
    }

    actual fun setExposure(exposure: Float) {
        TODO("Not yet implemented")
    }

    actual fun getAperture(): Float = TODO("Not yet implemented")

    actual fun getShutterSpeed(): Float = TODO("Not yet implemented")

    actual fun getFocalLength(): Double = TODO("Not yet implemented")

    actual fun setFocusDistance(distance: Float) {
        TODO("Not yet implemented")
    }

    actual fun getFocusDistance(): Float = TODO("Not yet implemented")

    actual fun getSensitivity(): Float = TODO("Not yet implemented")

    @Entity
    actual fun getEntity(): Int = TODO("Not yet implemented")

    actual fun setEyeModelMatrix(eyeId: Int, model: DoubleArray) {
        TODO("Not yet implemented")
    }

    actual fun getNativeObject(): Long = TODO("Not yet implemented")

    actual internal fun invalidate() {
    }

    actual enum class Projection {
        PERSPECTIVE,
        ORTHO,
    }

    actual enum class Fov {
        VERTICAL,
        HORIZONTAL,
    }
}


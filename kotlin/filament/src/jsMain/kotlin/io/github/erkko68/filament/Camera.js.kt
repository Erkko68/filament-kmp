package io.github.erkko68.filament

actual class Camera {
    actual fun setProjection(
        projection: Projection,
        left: Double,
        right: Double,
        bottom: Double,
        top: Double,
        near: Double,
        far: Double
    ) {
    }

    actual fun setProjection(
        fovInDegrees: Double,
        aspect: Double,
        near: Double,
        far: Double,
        direction: Fov
    ) {
    }

    actual fun setLensProjection(
        focalLength: Double,
        aspect: Double,
        near: Double,
        far: Double
    ) {
    }

    actual fun setCustomProjection(matrix: DoubleArray, near: Double, far: Double) {
    }

    actual fun setCustomProjection(
        matrix: DoubleArray,
        matrixForCulling: DoubleArray,
        near: Double,
        far: Double
    ) {
    }

    actual fun setCustomEyeProjection(
        projection: DoubleArray,
        count: Int,
        projectionForCulling: DoubleArray,
        near: Double,
        far: Double
    ) {
    }

    actual fun setEyeModelMatrix(eyeId: Int, modelMatrix: DoubleArray) {
    }

    actual fun setScaling(x: Double, y: Double) {
    }

    actual fun getScaling(out: DoubleArray?): DoubleArray {
        TODO("Not yet implemented")
    }

    actual fun setShift(x: Double, y: Double) {
    }

    actual fun getShift(out: DoubleArray?): DoubleArray {
        TODO("Not yet implemented")
    }

    actual fun lookAt(
        eyeX: Double,
        eyeY: Double,
        eyeZ: Double,
        centerX: Double,
        centerY: Double,
        centerZ: Double,
        upX: Double,
        upY: Double,
        upZ: Double
    ) {
    }

    actual fun setModelMatrix(modelMatrix: FloatArray) {
    }

    actual fun setModelMatrix(modelMatrix: DoubleArray) {
    }

    actual fun getProjectionMatrix(out: DoubleArray?): DoubleArray {
        TODO("Not yet implemented")
    }

    actual fun getCullingProjectionMatrix(out: DoubleArray?): DoubleArray {
        TODO("Not yet implemented")
    }

    actual fun getModelMatrix(out: FloatArray?): FloatArray {
        TODO("Not yet implemented")
    }

    actual fun getModelMatrix(out: DoubleArray?): DoubleArray {
        TODO("Not yet implemented")
    }

    actual fun getViewMatrix(out: FloatArray?): FloatArray {
        TODO("Not yet implemented")
    }

    actual fun getViewMatrix(out: DoubleArray?): DoubleArray {
        TODO("Not yet implemented")
    }

    actual fun getPosition(out: FloatArray?): FloatArray {
        TODO("Not yet implemented")
    }

    actual fun getLeftVector(out: FloatArray?): FloatArray {
        TODO("Not yet implemented")
    }

    actual fun getUpVector(out: FloatArray?): FloatArray {
        TODO("Not yet implemented")
    }

    actual fun getForwardVector(out: FloatArray?): FloatArray {
        TODO("Not yet implemented")
    }

    actual fun getNear(): Float {
        TODO("Not yet implemented")
    }

    actual fun getCullingFar(): Float {
        TODO("Not yet implemented")
    }

    actual fun setExposure(aperture: Float, shutterSpeed: Float, sensitivity: Float) {
    }

    actual fun setExposure(exposure: Float) {
    }

    actual fun getAperture(): Float {
        TODO("Not yet implemented")
    }

    actual fun getShutterSpeed(): Float {
        TODO("Not yet implemented")
    }

    actual fun getSensitivity(): Float {
        TODO("Not yet implemented")
    }

    actual fun getFocalLength(): Double {
        TODO("Not yet implemented")
    }

    actual fun setFocusDistance(distance: Float) {
    }

    actual fun getFocusDistance(): Float {
        TODO("Not yet implemented")
    }

    actual fun getFieldOfViewInDegrees(direction: Fov): Double {
        TODO("Not yet implemented")
    }

    actual fun getEntity(): Int {
        TODO("Not yet implemented")
    }

    actual enum class Projection { PERSPECTIVE, ORTHO }
    actual enum class Fov { VERTICAL, HORIZONTAL }
}
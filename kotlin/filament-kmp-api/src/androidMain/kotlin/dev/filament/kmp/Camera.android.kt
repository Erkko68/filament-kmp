package dev.filament.kmp

import com.google.android.filament.Camera as AndroidCamera

actual class Camera internal constructor(
    internal var androidCamera: AndroidCamera?,
) {
    actual fun setProjection(projection: Projection, left: Double, right: Double, bottom: Double, top: Double, near: Double, far: Double) {
        val camera = requireNotNull(androidCamera) { "Calling method on destroyed Camera" }
        camera.setProjection(projection.toAndroid(), left, right, bottom, top, near, far)
    }

    actual fun setProjection(fovInDegrees: Double, aspect: Double, near: Double, far: Double, direction: Fov) {
        val camera = requireNotNull(androidCamera) { "Calling method on destroyed Camera" }
        camera.setProjection(fovInDegrees, aspect, near, far, direction.toAndroid())
    }

    actual fun setLensProjection(focalLength: Double, aspect: Double, near: Double, far: Double) {
        val camera = requireNotNull(androidCamera) { "Calling method on destroyed Camera" }
        camera.setLensProjection(focalLength, aspect, near, far)
    }

    actual fun setCustomProjection(inProjection: DoubleArray, near: Double, far: Double) {
        val camera = requireNotNull(androidCamera) { "Calling method on destroyed Camera" }
        camera.setCustomProjection(inProjection, near, far)
    }

    actual fun setCustomProjection(inProjection: DoubleArray, inProjectionForCulling: DoubleArray, near: Double, far: Double) {
        val camera = requireNotNull(androidCamera) { "Calling method on destroyed Camera" }
        camera.setCustomProjection(inProjection, inProjectionForCulling, near, far)
    }

    actual fun setScaling(xscaling: Double, yscaling: Double) {
        val camera = requireNotNull(androidCamera) { "Calling method on destroyed Camera" }
        camera.setScaling(xscaling, yscaling)
    }

    @Deprecated("Use setScaling(xscaling, yscaling)")
    actual fun setScaling(inScaling: DoubleArray) {
        val camera = requireNotNull(androidCamera) { "Calling method on destroyed Camera" }
        camera.setScaling(inScaling)
    }

    actual fun setCustomEyeProjection(inProjection: DoubleArray, count: Int, inProjectionForCulling: DoubleArray, near: Double, far: Double) {
        val camera = requireNotNull(androidCamera) { "Calling method on destroyed Camera" }
        camera.setCustomEyeProjection(inProjection, count, inProjectionForCulling, near, far)
    }

    actual fun setShift(xshift: Double, yshift: Double) {
        val camera = requireNotNull(androidCamera) { "Calling method on destroyed Camera" }
        camera.setShift(xshift, yshift)
    }

    actual fun getShift(out: DoubleArray?): DoubleArray {
        val camera = requireNotNull(androidCamera) { "Calling method on destroyed Camera" }
        return camera.getShift(out)
    }

    actual fun getFieldOfViewInDegrees(direction: Fov): Double {
        val camera = requireNotNull(androidCamera) { "Calling method on destroyed Camera" }
        return camera.getFieldOfViewInDegrees(direction.toAndroid())
    }

    actual fun setModelMatrix(modelMatrix: FloatArray) {
        val camera = requireNotNull(androidCamera) { "Calling method on destroyed Camera" }
        camera.setModelMatrix(modelMatrix)
    }

    actual fun setModelMatrix(modelMatrix: DoubleArray) {
        val camera = requireNotNull(androidCamera) { "Calling method on destroyed Camera" }
        camera.setModelMatrix(modelMatrix)
    }

    actual fun lookAt(eyeX: Double, eyeY: Double, eyeZ: Double, centerX: Double, centerY: Double, centerZ: Double, upX: Double, upY: Double, upZ: Double) {
        val camera = requireNotNull(androidCamera) { "Calling method on destroyed Camera" }
        camera.lookAt(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ)
    }

    actual fun getNear(): Float {
        val camera = requireNotNull(androidCamera) { "Calling method on destroyed Camera" }
        return camera.near
    }

    actual fun getCullingFar(): Float {
        val camera = requireNotNull(androidCamera) { "Calling method on destroyed Camera" }
        return camera.cullingFar
    }

    actual fun getProjectionMatrix(out: DoubleArray?): DoubleArray {
        val camera = requireNotNull(androidCamera) { "Calling method on destroyed Camera" }
        return camera.getProjectionMatrix(out)
    }

    actual fun getCullingProjectionMatrix(out: DoubleArray?): DoubleArray {
        val camera = requireNotNull(androidCamera) { "Calling method on destroyed Camera" }
        return camera.getCullingProjectionMatrix(out)
    }

    actual fun getScaling(out: DoubleArray?): DoubleArray {
        val camera = requireNotNull(androidCamera) { "Calling method on destroyed Camera" }
        return camera.getScaling(out)
    }

    actual fun getModelMatrix(out: FloatArray?): FloatArray {
        val camera = requireNotNull(androidCamera) { "Calling method on destroyed Camera" }
        return camera.getModelMatrix(out)
    }

    actual fun getModelMatrix(out: DoubleArray?): DoubleArray {
        val camera = requireNotNull(androidCamera) { "Calling method on destroyed Camera" }
        return camera.getModelMatrix(out)
    }

    actual fun getViewMatrix(out: FloatArray?): FloatArray {
        val camera = requireNotNull(androidCamera) { "Calling method on destroyed Camera" }
        return camera.getViewMatrix(out)
    }

    actual fun getViewMatrix(out: DoubleArray?): DoubleArray {
        val camera = requireNotNull(androidCamera) { "Calling method on destroyed Camera" }
        return camera.getViewMatrix(out)
    }

    actual fun getPosition(out: FloatArray?): FloatArray {
        val camera = requireNotNull(androidCamera) { "Calling method on destroyed Camera" }
        return camera.getPosition(out)
    }

    actual fun getLeftVector(out: FloatArray?): FloatArray {
        val camera = requireNotNull(androidCamera) { "Calling method on destroyed Camera" }
        return camera.getLeftVector(out)
    }

    actual fun getUpVector(out: FloatArray?): FloatArray {
        val camera = requireNotNull(androidCamera) { "Calling method on destroyed Camera" }
        return camera.getUpVector(out)
    }

    actual fun getForwardVector(out: FloatArray?): FloatArray {
        val camera = requireNotNull(androidCamera) { "Calling method on destroyed Camera" }
        return camera.getForwardVector(out)
    }

    actual fun setExposure(aperture: Float, shutterSpeed: Float, sensitivity: Float) {
        val camera = requireNotNull(androidCamera) { "Calling method on destroyed Camera" }
        camera.setExposure(aperture, shutterSpeed, sensitivity)
    }

    actual fun setExposure(exposure: Float) {
        val camera = requireNotNull(androidCamera) { "Calling method on destroyed Camera" }
        camera.setExposure(exposure)
    }

    actual fun getAperture(): Float {
        val camera = requireNotNull(androidCamera) { "Calling method on destroyed Camera" }
        return camera.aperture
    }

    actual fun getShutterSpeed(): Float {
        val camera = requireNotNull(androidCamera) { "Calling method on destroyed Camera" }
        return camera.shutterSpeed
    }

    actual fun getFocalLength(): Double {
        val camera = requireNotNull(androidCamera) { "Calling method on destroyed Camera" }
        return camera.focalLength
    }

    actual fun setFocusDistance(distance: Float) {
        val camera = requireNotNull(androidCamera) { "Calling method on destroyed Camera" }
        camera.setFocusDistance(distance)
    }

    actual fun getFocusDistance(): Float {
        val camera = requireNotNull(androidCamera) { "Calling method on destroyed Camera" }
        return camera.focusDistance
    }

    actual fun getSensitivity(): Float {
        val camera = requireNotNull(androidCamera) { "Calling method on destroyed Camera" }
        return camera.sensitivity
    }

    actual fun getEntity(): Int {
        val camera = requireNotNull(androidCamera) { "Calling method on destroyed Camera" }
        return camera.entity
    }

    actual fun setEyeModelMatrix(eyeId: Int, model: DoubleArray) {
        val camera = requireNotNull(androidCamera) { "Calling method on destroyed Camera" }
        camera.setEyeModelMatrix(eyeId, model)
    }

    actual fun getNativeObject(): Long {
        val camera = requireNotNull(androidCamera) { "Calling method on destroyed Camera" }
        return camera.nativeObject
    }

    actual internal fun invalidate() {
        androidCamera = null
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

private fun Camera.Projection.toAndroid(): AndroidCamera.Projection = when (this) {
    Camera.Projection.PERSPECTIVE -> AndroidCamera.Projection.PERSPECTIVE
    Camera.Projection.ORTHO -> AndroidCamera.Projection.ORTHO
}

private fun Camera.Fov.toAndroid(): AndroidCamera.Fov = when (this) {
    Camera.Fov.VERTICAL -> AndroidCamera.Fov.VERTICAL
    Camera.Fov.HORIZONTAL -> AndroidCamera.Fov.HORIZONTAL
}


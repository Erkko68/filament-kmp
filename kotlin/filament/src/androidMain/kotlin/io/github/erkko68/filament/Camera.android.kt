package io.github.erkko68.filament

import com.google.android.filament.Camera as AndroidCamera

actual class Camera internal constructor(val nativeCamera: AndroidCamera) {
    actual enum class Projection {
        PERSPECTIVE, ORTHO;
        internal fun toAndroid() = AndroidCamera.Projection.values()[ordinal]
    }
    actual enum class Fov {
        VERTICAL, HORIZONTAL;
        internal fun toAndroid() = AndroidCamera.Fov.values()[ordinal]
    }
 
    actual fun setProjection(projection: Projection, left: Double, right: Double, bottom: Double, top: Double, near: Double, far: Double) {
        nativeCamera.setProjection(projection.toAndroid(), left, right, bottom, top, near, far)
    }
    actual fun setProjection(fovInDegrees: Double, aspect: Double, near: Double, far: Double, direction: Fov) {
        nativeCamera.setProjection(fovInDegrees, aspect, near, far, direction.toAndroid())
    }
    actual fun setLensProjection(focalLength: Double, aspect: Double, near: Double, far: Double) {
        nativeCamera.setLensProjection(focalLength, aspect, near, far)
    }
    actual fun setCustomProjection(matrix: DoubleArray, near: Double, far: Double) {
        nativeCamera.setCustomProjection(matrix, near, far)
    }
    actual fun setCustomProjection(matrix: DoubleArray, matrixForCulling: DoubleArray, near: Double, far: Double) {
        nativeCamera.setCustomProjection(matrix, matrixForCulling, near, far)
    }

    actual fun setCustomEyeProjection(projection: DoubleArray, count: Int, projectionForCulling: DoubleArray, near: Double, far: Double) {
        nativeCamera.setCustomEyeProjection(projection, count, projectionForCulling, near, far)
    }
    actual fun setEyeModelMatrix(eyeId: Int, modelMatrix: DoubleArray) {
        nativeCamera.setEyeModelMatrix(eyeId, modelMatrix)
    }
    
    actual fun setScaling(x: Double, y: Double) {
        nativeCamera.setScaling(x, y)
    }
    actual fun getScaling(out: DoubleArray?): DoubleArray = nativeCamera.getScaling(out)
    actual fun setShift(x: Double, y: Double) {
        nativeCamera.setShift(x, y)
    }
    actual fun getShift(out: DoubleArray?): DoubleArray = nativeCamera.getShift(out)
    
    actual fun lookAt(eyeX: Double, eyeY: Double, eyeZ: Double, centerX: Double, centerY: Double, centerZ: Double, upX: Double, upY: Double, upZ: Double) {
        nativeCamera.lookAt(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ)
    }
    
    actual fun setModelMatrix(modelMatrix: FloatArray) {
        nativeCamera.setModelMatrix(modelMatrix)
    }
    actual fun setModelMatrix(modelMatrix: DoubleArray) {
        nativeCamera.setModelMatrix(modelMatrix)
    }
    
    actual fun getProjectionMatrix(out: DoubleArray?): DoubleArray = nativeCamera.getProjectionMatrix(out)
    actual fun getCullingProjectionMatrix(out: DoubleArray?): DoubleArray = nativeCamera.getCullingProjectionMatrix(out)
    
    actual fun getModelMatrix(out: FloatArray?): FloatArray = nativeCamera.getModelMatrix(out)
    actual fun getModelMatrix(out: DoubleArray?): DoubleArray = nativeCamera.getModelMatrix(out)
    
    actual fun getViewMatrix(out: FloatArray?): FloatArray = nativeCamera.getViewMatrix(out)
    actual fun getViewMatrix(out: DoubleArray?): DoubleArray = nativeCamera.getViewMatrix(out)
    
    actual fun getPosition(out: FloatArray?): FloatArray = nativeCamera.getPosition(out)
    
    actual fun getLeftVector(out: FloatArray?): FloatArray = nativeCamera.getLeftVector(out)
    actual fun getUpVector(out: FloatArray?): FloatArray = nativeCamera.getUpVector(out)
    actual fun getForwardVector(out: FloatArray?): FloatArray = nativeCamera.getForwardVector(out)
    
    actual val near: Float get() = nativeCamera.near
    actual val cullingFar: Float get() = nativeCamera.cullingFar
    
    actual fun setExposure(aperture: Float, shutterSpeed: Float, sensitivity: Float) {
        nativeCamera.setExposure(aperture, shutterSpeed, sensitivity)
    }
    actual fun setExposure(exposure: Float) {
        nativeCamera.setExposure(exposure)
    }
    actual val aperture: Float get() = nativeCamera.aperture
    actual val shutterSpeed: Float get() = nativeCamera.shutterSpeed
    actual val sensitivity: Float get() = nativeCamera.sensitivity
    actual val focalLength: Double get() = nativeCamera.focalLength
    
    actual var focusDistance: Float
        get() = nativeCamera.focusDistance
        set(value) { nativeCamera.focusDistance = value }
    
    actual fun getFieldOfViewInDegrees(direction: Fov): Double = nativeCamera.getFieldOfViewInDegrees(direction.toAndroid())

    actual val entity: Entity get() = nativeCamera.entity
}

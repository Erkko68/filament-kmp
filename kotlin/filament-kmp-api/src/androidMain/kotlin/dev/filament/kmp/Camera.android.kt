package dev.filament.kmp

actual class Camera internal constructor(val nativeCamera: com.google.android.filament.Camera) {
    actual enum class Projection {
        PERSPECTIVE, ORTHO;
        internal fun toAndroid() = com.google.android.filament.Camera.Projection.values()[ordinal]
    }
    actual enum class Fov {
        VERTICAL, HORIZONTAL;
        internal fun toAndroid() = com.google.android.filament.Camera.Fov.values()[ordinal]
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
    
    actual fun getNear(): Float = nativeCamera.near
    actual fun getCullingFar(): Float = nativeCamera.cullingFar
    
    actual fun setExposure(aperture: Float, shutterSpeed: Float, sensitivity: Float) {
        nativeCamera.setExposure(aperture, shutterSpeed, sensitivity)
    }
    actual fun getAperture(): Float = nativeCamera.aperture
    actual fun getShutterSpeed(): Float = nativeCamera.shutterSpeed
    actual fun getSensitivity(): Float = nativeCamera.sensitivity
    
    actual fun setFocusDistance(distance: Float) {
        nativeCamera.focusDistance = distance
    }
    actual fun getFocusDistance(): Float = nativeCamera.focusDistance
    
    actual fun getEntity(): Int = nativeCamera.entity
}

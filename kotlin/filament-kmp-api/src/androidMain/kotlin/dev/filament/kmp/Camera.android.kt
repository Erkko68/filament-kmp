package dev.filament.kmp

actual class Camera internal constructor(val nativeCamera: com.google.android.filament.Camera) {
    actual enum class Projection { PERSPECTIVE, ORTHO }
    actual enum class Fov { VERTICAL, HORIZONTAL }

    actual fun setProjection(projection: Projection, left: Double, right: Double, bottom: Double, top: Double, near: Double, far: Double) {
        nativeCamera.setProjection(com.google.android.filament.Camera.Projection.values()[projection.ordinal], left, right, bottom, top, near, far)
    }
    actual fun setProjection(fovInDegrees: Double, aspect: Double, near: Double, far: Double, direction: Fov) {
        nativeCamera.setProjection(fovInDegrees, aspect, near, far, com.google.android.filament.Camera.Fov.values()[direction.ordinal])
    }
    actual fun setLensProjection(focalLength: Double, aspect: Double, near: Double, far: Double) {
        nativeCamera.setLensProjection(focalLength, aspect, near, far)
    }
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
    actual fun getViewMatrix(out: FloatArray?): FloatArray = nativeCamera.getViewMatrix(out)
    actual fun getViewMatrix(out: DoubleArray?): DoubleArray = nativeCamera.getViewMatrix(out)
    actual fun getPosition(out: FloatArray?): FloatArray = nativeCamera.getPosition(out)
    actual fun getEntity(): Int = nativeCamera.entity
}

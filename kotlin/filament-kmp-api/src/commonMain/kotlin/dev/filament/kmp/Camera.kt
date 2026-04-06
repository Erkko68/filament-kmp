package dev.filament.kmp

expect class Camera {
    enum class Projection { PERSPECTIVE, ORTHO }
    enum class Fov { VERTICAL, HORIZONTAL }

    fun setProjection(projection: Projection, left: Double, right: Double, bottom: Double, top: Double, near: Double, far: Double)
    fun setProjection(fovInDegrees: Double, aspect: Double, near: Double, far: Double, direction: Fov)
    fun setLensProjection(focalLength: Double, aspect: Double, near: Double, far: Double)
    fun lookAt(eyeX: Double, eyeY: Double, eyeZ: Double, centerX: Double, centerY: Double, centerZ: Double, upX: Double, upY: Double, upZ: Double)
    fun setModelMatrix(modelMatrix: FloatArray)
    fun setModelMatrix(modelMatrix: DoubleArray)
    fun getProjectionMatrix(out: DoubleArray?): DoubleArray
    fun getViewMatrix(out: FloatArray?): FloatArray
    fun getViewMatrix(out: DoubleArray?): DoubleArray
    fun getPosition(out: FloatArray?): FloatArray
    fun getEntity(): Int
}

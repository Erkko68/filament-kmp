package dev.filament.kmp

expect class Camera {
    enum class Projection { PERSPECTIVE, ORTHO }
    enum class Fov { VERTICAL, HORIZONTAL }
 
    fun setProjection(projection: Projection, left: Double, right: Double, bottom: Double, top: Double, near: Double, far: Double)
    fun setProjection(fovInDegrees: Double, aspect: Double, near: Double, far: Double, direction: Fov)
    fun setLensProjection(focalLength: Double, aspect: Double, near: Double, far: Double)
    fun setCustomProjection(matrix: DoubleArray, near: Double, far: Double)
    fun setCustomProjection(matrix: DoubleArray, matrixForCulling: DoubleArray, near: Double, far: Double)
    
    fun setCustomEyeProjection(projection: DoubleArray, count: Int, projectionForCulling: DoubleArray, near: Double, far: Double)
    fun setEyeModelMatrix(eyeId: Int, modelMatrix: DoubleArray)

    fun setScaling(x: Double, y: Double)
    fun getScaling(out: DoubleArray? = null): DoubleArray
    fun setShift(x: Double, y: Double)
    fun getShift(out: DoubleArray? = null): DoubleArray
    
    fun lookAt(eyeX: Double, eyeY: Double, eyeZ: Double, centerX: Double, centerY: Double, centerZ: Double, upX: Double, upY: Double, upZ: Double)
    
    fun setModelMatrix(modelMatrix: FloatArray)
    fun setModelMatrix(modelMatrix: DoubleArray)
    
    fun getProjectionMatrix(out: DoubleArray? = null): DoubleArray
    fun getCullingProjectionMatrix(out: DoubleArray? = null): DoubleArray
    
    fun getModelMatrix(out: FloatArray? = null): FloatArray
    fun getModelMatrix(out: DoubleArray? = null): DoubleArray
    
    fun getViewMatrix(out: FloatArray? = null): FloatArray
    fun getViewMatrix(out: DoubleArray? = null): DoubleArray
    
    fun getPosition(out: FloatArray? = null): FloatArray
    
    fun getLeftVector(out: FloatArray? = null): FloatArray
    fun getUpVector(out: FloatArray? = null): FloatArray
    fun getForwardVector(out: FloatArray? = null): FloatArray
    
    fun getNear(): Float
    fun getCullingFar(): Float
    
    fun setExposure(aperture: Float, shutterSpeed: Float, sensitivity: Float)
    fun setExposure(exposure: Float)
    fun getAperture(): Float
    fun getShutterSpeed(): Float
    fun getSensitivity(): Float
    fun getFocalLength(): Double
    
    fun setFocusDistance(distance: Float)
    fun getFocusDistance(): Float
    
    fun getFieldOfViewInDegrees(direction: Fov): Double

    fun getEntity(): Int
}

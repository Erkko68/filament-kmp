package io.github.erkko68.filament

import io.github.erkko68.filament.js.Camera as JSCamera
import io.github.erkko68.filament.js.Camera_Projection
import io.github.erkko68.filament.js.Camera_Fov

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
actual class Camera(internal val jsCamera: JSCamera) {
    private var _projectionMatrix = DoubleArray(16)
    private var _modelMatrix = DoubleArray(16)
    private var _near = 0.1
    private var _far = 100.0

    actual fun setProjection(
        projection: Projection,
        left: Double,
        right: Double,
        bottom: Double,
        top: Double,
        near: Double,
        far: Double
    ) {
        _near = near
        _far = far
        val jsProj = if (projection == Projection.PERSPECTIVE) Camera_Projection.PERSPECTIVE else Camera_Projection.ORTHO
        jsCamera.setProjection(jsProj, left, right, bottom, top, near, far)
    }

    actual fun setProjection(
        fovInDegrees: Double,
        aspect: Double,
        near: Double,
        far: Double,
        direction: Fov
    ) {
        _near = near
        _far = far
        val jsFov = if (direction == Fov.VERTICAL) Camera_Fov.VERTICAL else Camera_Fov.HORIZONTAL
        jsCamera.setProjectionFov(fovInDegrees, aspect, near, far, jsFov)
    }

    actual fun setLensProjection(
        focalLength: Double,
        aspect: Double,
        near: Double,
        far: Double
    ) {
        _near = near
        _far = far
        jsCamera.setLensProjection(focalLength, aspect, near, far)
    }

    actual fun setCustomProjection(matrix: DoubleArray, near: Double, far: Double) {
        _projectionMatrix = matrix.copyOf()
        _near = near
        _far = far
        jsCamera.setCustomProjection(matrix.toTypedArray() as Array<Number>, near, far)
    }

    actual fun setCustomProjection(
        matrix: DoubleArray,
        matrixForCulling: DoubleArray,
        near: Double,
        far: Double
    ) {
        _projectionMatrix = matrix.copyOf()
        _near = near
        _far = far
        // JS bindings don't expose a separate culling matrix setter easily, 
        // we use the main projection matrix.
        jsCamera.setCustomProjection(matrix.toTypedArray() as Array<Number>, near, far)
    }

    actual fun setCustomEyeProjection(
        projection: DoubleArray,
        count: Int,
        projectionForCulling: DoubleArray,
        near: Double,
        far: Double
    ) {
        // Multi-eye projection is specialized in C++, not directly exposed in simple JS bindings
    }

    actual fun setEyeModelMatrix(eyeId: Int, modelMatrix: DoubleArray) {
    }

    actual fun setScaling(x: Double, y: Double) {
        jsCamera.setScaling(arrayOf(x, y) as Array<Number>)
    }

    actual fun getScaling(out: DoubleArray?): DoubleArray {
        val result = out ?: DoubleArray(4)
        val jsVec = jsCamera.getScaling() as Array<Double>
        for (i in 0 until jsVec.size.coerceAtMost(result.size)) result[i] = jsVec[i]
        return result
    }

    actual fun setShift(x: Double, y: Double) {
    }

    actual fun getShift(out: DoubleArray?): DoubleArray {
        return out ?: DoubleArray(2)
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
        jsCamera.lookAt(
            arrayOf(eyeX, eyeY, eyeZ) as Array<Number>,
            arrayOf(centerX, centerY, centerZ) as Array<Number>,
            arrayOf(upX, upY, upZ) as Array<Number>
        )
    }

    actual fun setModelMatrix(modelMatrix: FloatArray) {
        jsCamera.setModelMatrix(modelMatrix.toTypedArray() as Array<Number>)
    }

    actual fun setModelMatrix(modelMatrix: DoubleArray) {
        jsCamera.setModelMatrix(modelMatrix.toTypedArray() as Array<Number>)
    }

    actual fun getProjectionMatrix(out: DoubleArray?): DoubleArray {
        val result = out ?: DoubleArray(16)
        val jsMatrix = jsCamera.getProjectionMatrix() as Array<Double>
        for (i in 0 until 16.coerceAtMost(jsMatrix.size)) result[i] = jsMatrix[i]
        return result
    }

    actual fun getCullingProjectionMatrix(out: DoubleArray?): DoubleArray {
        val result = out ?: DoubleArray(16)
        val jsMatrix = jsCamera.getCullingProjectionMatrix() as Array<Double>
        for (i in 0 until 16.coerceAtMost(jsMatrix.size)) result[i] = jsMatrix[i]
        return result
    }

    actual fun getModelMatrix(out: FloatArray?): FloatArray {
        val result = out ?: FloatArray(16)
        val jsMatrix = jsCamera.getModelMatrix() as Array<Double>
        for (i in 0 until 16.coerceAtMost(jsMatrix.size)) result[i] = jsMatrix[i].toFloat()
        return result
    }

    actual fun getModelMatrix(out: DoubleArray?): DoubleArray {
        val result = out ?: DoubleArray(16)
        val jsMatrix = jsCamera.getModelMatrix() as Array<Double>
        for (i in 0 until 16.coerceAtMost(jsMatrix.size)) result[i] = jsMatrix[i]
        return result
    }

    actual fun getViewMatrix(out: FloatArray?): FloatArray {
        val result = out ?: FloatArray(16)
        val jsMatrix = jsCamera.getViewMatrix() as Array<Double>
        for (i in 0 until 16.coerceAtMost(jsMatrix.size)) result[i] = jsMatrix[i].toFloat()
        return result
    }

    actual fun getViewMatrix(out: DoubleArray?): DoubleArray {
        val result = out ?: DoubleArray(16)
        val jsMatrix = jsCamera.getViewMatrix() as Array<Double>
        for (i in 0 until 16.coerceAtMost(jsMatrix.size)) result[i] = jsMatrix[i]
        return result
    }

    actual fun getPosition(out: FloatArray?): FloatArray {
        val result = out ?: FloatArray(3)
        val pos = jsCamera.getPosition() as Array<Double>
        for (i in 0 until 3.coerceAtMost(pos.size)) result[i] = pos[i].toFloat()
        return result
    }

    actual fun getLeftVector(out: FloatArray?): FloatArray {
        val result = out ?: FloatArray(3)
        val vec = jsCamera.getLeftVector() as Array<Double>
        for (i in 0 until 3.coerceAtMost(vec.size)) result[i] = vec[i].toFloat()
        return result
    }

    actual fun getUpVector(out: FloatArray?): FloatArray {
        val result = out ?: FloatArray(3)
        val vec = jsCamera.getUpVector() as Array<Double>
        for (i in 0 until 3.coerceAtMost(vec.size)) result[i] = vec[i].toFloat()
        return result
    }

    actual fun getForwardVector(out: FloatArray?): FloatArray {
        val result = out ?: FloatArray(3)
        val vec = jsCamera.getForwardVector() as Array<Double>
        for (i in 0 until 3.coerceAtMost(vec.size)) result[i] = vec[i].toFloat()
        return result
    }

    actual val near: Float
        get() = _near.toFloat()

    actual val cullingFar: Float
        get() = jsCamera.getCullingFar().toFloat()

    actual fun setExposure(aperture: Float, shutterSpeed: Float, sensitivity: Float) {
        jsCamera.setExposure(aperture, shutterSpeed, sensitivity)
    }

    actual fun setExposure(exposure: Float) {
        jsCamera.setExposureDirect(exposure)
    }

    actual val aperture: Float
        get() = jsCamera.getAperture().toFloat()

    actual val shutterSpeed: Float
        get() = jsCamera.getShutterSpeed().toFloat()

    actual val sensitivity: Float
        get() = jsCamera.getSensitivity().toFloat()

    actual val focalLength: Double
        get() = jsCamera.getFocalLength().toDouble()

    actual var focusDistance: Float
        get() = jsCamera.getFocusDistance().toFloat()
        set(value) { jsCamera.setFocusDistance(value.toDouble()) }

    actual fun getFieldOfViewInDegrees(direction: Fov): Double {
        return 0.0 // No direct FOV getter in JS without reverse projection
    }

    actual val entity: Entity
        get() = jsCamera.unsafeCast<io.github.erkko68.filament.js.Entity>().getId().toInt()

    actual enum class Projection { PERSPECTIVE, ORTHO }
    actual enum class Fov { VERTICAL, HORIZONTAL }

}
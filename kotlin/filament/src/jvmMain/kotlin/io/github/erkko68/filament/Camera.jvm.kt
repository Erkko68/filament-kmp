package io.github.erkko68.filament

import io.github.erkko68.filament.jni.Camera as JniCamera

actual class Camera(val nativeCamera: JniCamera) {
    actual enum class Projection { PERSPECTIVE, ORTHO }
    actual enum class Fov { VERTICAL, HORIZONTAL }

    actual fun setProjection(projection: Projection, left: Double, right: Double, bottom: Double, top: Double, near: Double, far: Double) =
        nativeCamera.setProjection(JniCamera.Projection.values()[projection.ordinal], left, right, bottom, top, near, far)
    
    actual fun setProjection(fovInDegrees: Double, aspect: Double, near: Double, far: Double, direction: Fov) =
        nativeCamera.setProjection(fovInDegrees, aspect, near, far, JniCamera.Fov.values()[direction.ordinal])

    actual fun setLensProjection(focalLength: Double, aspect: Double, near: Double, far: Double) =
        nativeCamera.setLensProjection(focalLength, aspect, near, far)

    actual fun setCustomProjection(matrix: DoubleArray, near: Double, far: Double) =
        nativeCamera.setCustomProjection(matrix, near, far)

    actual fun setCustomProjection(matrix: DoubleArray, matrixForCulling: DoubleArray, near: Double, far: Double) =
        nativeCamera.setCustomProjection(matrix, matrixForCulling, near, far)

    actual fun setCustomEyeProjection(projection: DoubleArray, count: Int, projectionForCulling: DoubleArray, near: Double, far: Double) {
        nativeCamera.setCustomEyeProjection(projection, count, projectionForCulling, near, far)
    }

    actual fun setEyeModelMatrix(eyeId: Int, modelMatrix: DoubleArray) {
        // JNI setEyeModelMatrix takes float[]. We might need to convert.
        val floatModel = FloatArray(modelMatrix.size)
        for (i in modelMatrix.indices) floatModel[i] = modelMatrix[i].toFloat()
        nativeCamera.setEyeModelMatrix(eyeId, floatModel)
    }

    actual fun setScaling(x: Double, y: Double) = nativeCamera.setScaling(x, y)
    
    actual fun getScaling(out: DoubleArray?): DoubleArray {
        val result = out ?: DoubleArray(4)
        nativeCamera.getScaling(result)
        return result
    }

    actual fun setShift(x: Double, y: Double) = nativeCamera.setShift(x, y)

    actual fun getShift(out: DoubleArray?): DoubleArray {
        val result = out ?: DoubleArray(2)
        nativeCamera.getShift(result)
        return result
    }

    actual fun lookAt(eyeX: Double, eyeY: Double, eyeZ: Double, centerX: Double, centerY: Double, centerZ: Double, upX: Double, upY: Double, upZ: Double) {
        nativeCamera.lookAt(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ)
    }

    actual fun setModelMatrix(modelMatrix: FloatArray) = nativeCamera.setModelMatrix(modelMatrix)
    actual fun setModelMatrix(modelMatrix: DoubleArray) = nativeCamera.setModelMatrix(modelMatrix)

    actual fun getProjectionMatrix(out: DoubleArray?): DoubleArray {
        val result = out ?: DoubleArray(16)
        nativeCamera.getProjectionMatrix(result)
        return result
    }

    actual fun getCullingProjectionMatrix(out: DoubleArray?): DoubleArray {
        val result = out ?: DoubleArray(16)
        nativeCamera.getCullingProjectionMatrix(result)
        return result
    }

    actual fun getModelMatrix(out: FloatArray?): FloatArray {
        val result = out ?: FloatArray(16)
        nativeCamera.getModelMatrix(result)
        return result
    }

    actual fun getModelMatrix(out: DoubleArray?): DoubleArray {
        val result = out ?: DoubleArray(16)
        nativeCamera.getModelMatrix(result)
        return result
    }

    actual fun getViewMatrix(out: FloatArray?): FloatArray {
        val result = out ?: FloatArray(16)
        nativeCamera.getViewMatrix(result)
        return result
    }

    actual fun getViewMatrix(out: DoubleArray?): DoubleArray {
        val result = out ?: DoubleArray(16)
        nativeCamera.getViewMatrix(result)
        return result
    }

    actual fun getPosition(out: FloatArray?): FloatArray {
        val result = out ?: FloatArray(3)
        nativeCamera.getPosition(result)
        return result
    }

    actual fun getLeftVector(out: FloatArray?): FloatArray {
        val result = out ?: FloatArray(3)
        nativeCamera.getLeftVector(result)
        return result
    }

    actual fun getUpVector(out: FloatArray?): FloatArray {
        val result = out ?: FloatArray(3)
        nativeCamera.getUpVector(result)
        return result
    }

    actual fun getForwardVector(out: FloatArray?): FloatArray {
        val result = out ?: FloatArray(3)
        nativeCamera.getForwardVector(result)
        return result
    }

    actual fun getNear(): Float = nativeCamera.near
    actual fun getCullingFar(): Float = nativeCamera.cullingFar
    
    actual fun setExposure(aperture: Float, shutterSpeed: Float, sensitivity: Float) =
        nativeCamera.setExposure(aperture, shutterSpeed, sensitivity)

    actual fun setExposure(exposure: Float) = nativeCamera.setExposure(exposure)

    actual fun getAperture(): Float = nativeCamera.aperture
    actual fun getShutterSpeed(): Float = nativeCamera.shutterSpeed
    actual fun getSensitivity(): Float = nativeCamera.sensitivity
    actual fun getFocalLength(): Double = nativeCamera.focalLength

    actual fun setFocusDistance(distance: Float) = nativeCamera.setFocusDistance(distance)
    actual fun getFocusDistance(): Float = nativeCamera.focusDistance

    actual fun getFieldOfViewInDegrees(direction: Fov): Double =
        nativeCamera.getFieldOfViewInDegrees(JniCamera.Fov.values()[direction.ordinal])

    actual fun getEntity(): Int = nativeCamera.entity
}

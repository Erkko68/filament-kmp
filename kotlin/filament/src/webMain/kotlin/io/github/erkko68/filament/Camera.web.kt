package io.github.erkko68.filament

import io.github.erkko68.filament.wasm.*

actual class Camera @InternalFilamentApi constructor(
    internal var nativeHandle: Int,
    actual val entity: Entity
) {
    actual enum class Projection { PERSPECTIVE, ORTHO }
    actual enum class Fov { VERTICAL, HORIZONTAL }
 
    actual fun setProjection(projection: Projection, left: Double, right: Double, bottom: Double, top: Double, near: Double, far: Double) {
        FilaCamera_setProjection(nativeHandle, projection.ordinal, left, right, bottom, top, near, far)
    }
    actual fun setProjection(fovInDegrees: Double, aspect: Double, near: Double, far: Double, direction: Fov) {
        FilaCamera_setProjectionFov(nativeHandle, fovInDegrees, aspect, near, far, direction.ordinal)
    }
    actual fun setLensProjection(focalLength: Double, aspect: Double, near: Double, far: Double) {
        FilaCamera_setLensProjection(nativeHandle, focalLength, aspect, near, far)
    }
    actual fun setCustomProjection(matrix: DoubleArray, near: Double, far: Double) {
        matrix.usePinned { pinned ->
            FilaCamera_setCustomProjection(nativeHandle, pinned, pinned, near, far)
        }
    }
    actual fun setCustomProjection(matrix: DoubleArray, matrixForCulling: DoubleArray, near: Double, far: Double) {
        matrix.usePinned { pinned ->
            matrixForCulling.usePinned { pinnedCulling ->
                FilaCamera_setCustomProjection(nativeHandle, pinned, pinnedCulling, near, far)
            }
        }
    }
    
    actual fun setCustomEyeProjection(projection: DoubleArray, count: Int, projectionForCulling: DoubleArray, near: Double, far: Double) {
        projection.usePinned { pinned ->
            projectionForCulling.usePinned { pinnedCulling ->
                FilaCamera_setCustomEyeProjection(nativeHandle, pinned, count, pinnedCulling, near, far)
            }
        }
    }

    actual fun setEyeModelMatrix(eyeId: Int, modelMatrix: DoubleArray) {
        modelMatrix.usePinned { pinned ->
            FilaCamera_setEyeModelMatrix(nativeHandle, eyeId, pinned)
        }
    }

    actual fun setScaling(x: Double, y: Double) {
        FilaCamera_setScaling(nativeHandle, x, y)
    }
    actual fun getScaling(out: DoubleArray?): DoubleArray {
        val result = out ?: DoubleArray(4)
        result.usePinned { pinned ->
            FilaCamera_getScaling(nativeHandle, pinned)
        }
        return result
    }
    actual fun setShift(x: Double, y: Double) {
        FilaCamera_setShift(nativeHandle, x, y)
    }
    actual fun getShift(out: DoubleArray?): DoubleArray {
        val result = out ?: DoubleArray(2)
        result.usePinned { pinned ->
            FilaCamera_getShift(nativeHandle, pinned)
        }
        return result
    }
    
    actual fun lookAt(eyeX: Double, eyeY: Double, eyeZ: Double, centerX: Double, centerY: Double, centerZ: Double, upX: Double, upY: Double, upZ: Double) {
        FilaCamera_lookAt(nativeHandle, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ)
    }
    
    actual fun setModelMatrix(modelMatrix: FloatArray) {
        modelMatrix.usePinned { pinned ->
            FilaCamera_setModelMatrix(nativeHandle, pinned)
        }
    }
    actual fun setModelMatrix(modelMatrix: DoubleArray) {
        modelMatrix.usePinned { pinned ->
            FilaCamera_setModelMatrixFp64(nativeHandle, pinned)
        }
    }
    
    actual fun getProjectionMatrix(out: DoubleArray?): DoubleArray {
        val result = out ?: DoubleArray(16)
        result.usePinned { pinned ->
            FilaCamera_getProjectionMatrix(nativeHandle, pinned)
        }
        return result
    }
    actual fun getCullingProjectionMatrix(out: DoubleArray?): DoubleArray {
        val result = out ?: DoubleArray(16)
        result.usePinned { pinned ->
            FilaCamera_getCullingProjectionMatrix(nativeHandle, pinned)
        }
        return result
    }
    
    actual fun getModelMatrix(out: FloatArray?): FloatArray {
        val result = out ?: FloatArray(16)
        result.usePinned { pinned ->
            FilaCamera_getModelMatrix(nativeHandle, pinned)
        }
        return result
    }
    actual fun getModelMatrix(out: DoubleArray?): DoubleArray {
        val result = out ?: DoubleArray(16)
        result.usePinned { pinned ->
            FilaCamera_getModelMatrixFp64(nativeHandle, pinned)
        }
        return result
    }
    
    actual fun getViewMatrix(out: FloatArray?): FloatArray {
        val result = out ?: FloatArray(16)
        result.usePinned { pinned ->
            FilaCamera_getViewMatrix(nativeHandle, pinned)
        }
        return result
    }
    actual fun getViewMatrix(out: DoubleArray?): DoubleArray {
        val result = out ?: DoubleArray(16)
        result.usePinned { pinned ->
            FilaCamera_getViewMatrixFp64(nativeHandle, pinned)
        }
        return result
    }
    
    actual fun getPosition(out: FloatArray?): FloatArray {
        val result = out ?: FloatArray(3)
        result.usePinned { pinned ->
            FilaCamera_getPosition(nativeHandle, pinned)
        }
        return result
    }
    
    actual fun getLeftVector(out: FloatArray?): FloatArray {
        val result = out ?: FloatArray(3)
        result.usePinned { pinned ->
            FilaCamera_getLeftVector(nativeHandle, pinned)
        }
        return result
    }
    actual fun getUpVector(out: FloatArray?): FloatArray {
        val result = out ?: FloatArray(3)
        result.usePinned { pinned ->
            FilaCamera_getUpVector(nativeHandle, pinned)
        }
        return result
    }
    actual fun getForwardVector(out: FloatArray?): FloatArray {
        val result = out ?: FloatArray(3)
        result.usePinned { pinned ->
            FilaCamera_getForwardVector(nativeHandle, pinned)
        }
        return result
    }
    
    actual val near: Float get() = FilaCamera_getNear(nativeHandle).toFloat()
    actual val cullingFar: Float get() = FilaCamera_getCullingFar(nativeHandle).toFloat()
    
    actual fun setExposure(aperture: Float, shutterSpeed: Float, sensitivity: Float) {
        FilaCamera_setExposure(nativeHandle, aperture, shutterSpeed, sensitivity)
    }
    actual fun setExposure(exposure: Float) {
        setExposure(1.0f, 1.2f, 100.0f * (1.0f / exposure))
    }
    actual val aperture: Float get() = FilaCamera_getAperture(nativeHandle)
    actual val shutterSpeed: Float get() = FilaCamera_getShutterSpeed(nativeHandle)
    actual val sensitivity: Float get() = FilaCamera_getSensitivity(nativeHandle)
    actual val focalLength: Double get() = FilaCamera_getFocalLength(nativeHandle)
    
    actual var focusDistance: Float
        get() = FilaCamera_getFocusDistance(nativeHandle)
        set(value) { FilaCamera_setFocusDistance(nativeHandle, value) }
    
    actual fun getFieldOfViewInDegrees(direction: Fov): Double = FilaCamera_getFieldOfViewInDegrees(nativeHandle, direction.ordinal)


}

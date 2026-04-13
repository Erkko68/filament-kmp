@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package io.github.erkko68

import kotlinx.cinterop.*
import io.github.erkko68.cinterop.*
import cnames.structs.FilaCamera

actual class Camera internal constructor(
    internal var nativeHandle: CPointer<FilaCamera>?,
    private val entity: Int
) {
    actual enum class Projection { PERSPECTIVE, ORTHO }
    actual enum class Fov { VERTICAL, HORIZONTAL }
 
    actual fun setProjection(projection: Projection, left: Double, right: Double, bottom: Double, top: Double, near: Double, far: Double) {
        FilaCamera_setProjection(nativeHandle, projection.ordinal.toUInt(), left, right, bottom, top, near, far)
    }
    actual fun setProjection(fovInDegrees: Double, aspect: Double, near: Double, far: Double, direction: Fov) {
        FilaCamera_setProjectionFov(nativeHandle, fovInDegrees, aspect, near, far, direction.ordinal.toUInt())
    }
    actual fun setLensProjection(focalLength: Double, aspect: Double, near: Double, far: Double) {
        FilaCamera_setLensProjection(nativeHandle, focalLength, aspect, near, far)
    }
    actual fun setCustomProjection(matrix: DoubleArray, near: Double, far: Double) {
        matrix.usePinned { pinned ->
            FilaCamera_setCustomProjection(nativeHandle, pinned.addressOf(0), pinned.addressOf(0), near, far)
        }
    }
    actual fun setCustomProjection(matrix: DoubleArray, matrixForCulling: DoubleArray, near: Double, far: Double) {
        matrix.usePinned { pinned ->
            matrixForCulling.usePinned { pinnedCulling ->
                FilaCamera_setCustomProjection(nativeHandle, pinned.addressOf(0), pinnedCulling.addressOf(0), near, far)
            }
        }
    }
    
    actual fun setCustomEyeProjection(projection: DoubleArray, count: Int, projectionForCulling: DoubleArray, near: Double, far: Double) {
        projection.usePinned { pinned ->
            projectionForCulling.usePinned { pinnedCulling ->
                FilaCamera_setCustomEyeProjection(nativeHandle, pinned.addressOf(0), count.toULong(), pinnedCulling.addressOf(0), near, far)
            }
        }
    }

    actual fun setEyeModelMatrix(eyeId: Int, modelMatrix: DoubleArray) {
        modelMatrix.usePinned { pinned ->
            FilaCamera_setEyeModelMatrix(nativeHandle, eyeId, pinned.addressOf(0))
        }
    }

    actual fun setScaling(x: Double, y: Double) {
        FilaCamera_setScaling(nativeHandle, x, y)
    }
    actual fun getScaling(out: DoubleArray?): DoubleArray {
        val result = out ?: DoubleArray(4)
        result.usePinned { pinned ->
            FilaCamera_getScaling(nativeHandle, pinned.addressOf(0))
        }
        return result
    }
    actual fun setShift(x: Double, y: Double) {
        FilaCamera_setShift(nativeHandle, x, y)
    }
    actual fun getShift(out: DoubleArray?): DoubleArray {
        val result = out ?: DoubleArray(2)
        result.usePinned { pinned ->
            FilaCamera_getShift(nativeHandle, pinned.addressOf(0))
        }
        return result
    }
    
    actual fun lookAt(eyeX: Double, eyeY: Double, eyeZ: Double, centerX: Double, centerY: Double, centerZ: Double, upX: Double, upY: Double, upZ: Double) {
        FilaCamera_lookAt(nativeHandle, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ)
    }
    
    actual fun setModelMatrix(modelMatrix: FloatArray) {
        modelMatrix.usePinned { pinned ->
            FilaCamera_setModelMatrix(nativeHandle, pinned.addressOf(0))
        }
    }
    actual fun setModelMatrix(modelMatrix: DoubleArray) {
        modelMatrix.usePinned { pinned ->
            FilaCamera_setModelMatrixFp64(nativeHandle, pinned.addressOf(0))
        }
    }
    
    actual fun getProjectionMatrix(out: DoubleArray?): DoubleArray {
        val result = out ?: DoubleArray(16)
        result.usePinned { pinned ->
            FilaCamera_getProjectionMatrix(nativeHandle, pinned.addressOf(0))
        }
        return result
    }
    actual fun getCullingProjectionMatrix(out: DoubleArray?): DoubleArray {
        val result = out ?: DoubleArray(16)
        result.usePinned { pinned ->
            FilaCamera_getCullingProjectionMatrix(nativeHandle, pinned.addressOf(0))
        }
        return result
    }
    
    actual fun getModelMatrix(out: FloatArray?): FloatArray {
        val result = out ?: FloatArray(16)
        result.usePinned { pinned ->
            FilaCamera_getModelMatrix(nativeHandle, pinned.addressOf(0))
        }
        return result
    }
    actual fun getModelMatrix(out: DoubleArray?): DoubleArray {
        val result = out ?: DoubleArray(16)
        result.usePinned { pinned ->
            FilaCamera_getModelMatrixFp64(nativeHandle, pinned.addressOf(0))
        }
        return result
    }
    
    actual fun getViewMatrix(out: FloatArray?): FloatArray {
        val result = out ?: FloatArray(16)
        result.usePinned { pinned ->
            FilaCamera_getViewMatrix(nativeHandle, pinned.addressOf(0))
        }
        return result
    }
    actual fun getViewMatrix(out: DoubleArray?): DoubleArray {
        val result = out ?: DoubleArray(16)
        result.usePinned { pinned ->
            FilaCamera_getViewMatrixFp64(nativeHandle, pinned.addressOf(0))
        }
        return result
    }
    
    actual fun getPosition(out: FloatArray?): FloatArray {
        val result = out ?: FloatArray(3)
        result.usePinned { pinned ->
            FilaCamera_getPosition(nativeHandle, pinned.addressOf(0))
        }
        return result
    }
    
    actual fun getLeftVector(out: FloatArray?): FloatArray {
        val result = out ?: FloatArray(3)
        result.usePinned { pinned ->
            FilaCamera_getLeftVector(nativeHandle, pinned.addressOf(0))
        }
        return result
    }
    actual fun getUpVector(out: FloatArray?): FloatArray {
        val result = out ?: FloatArray(3)
        result.usePinned { pinned ->
            FilaCamera_getUpVector(nativeHandle, pinned.addressOf(0))
        }
        return result
    }
    actual fun getForwardVector(out: FloatArray?): FloatArray {
        val result = out ?: FloatArray(3)
        result.usePinned { pinned ->
            FilaCamera_getForwardVector(nativeHandle, pinned.addressOf(0))
        }
        return result
    }
    
    actual fun getNear(): Float = FilaCamera_getNear(nativeHandle).toFloat()
    actual fun getCullingFar(): Float = FilaCamera_getCullingFar(nativeHandle).toFloat()
    
    actual fun setExposure(aperture: Float, shutterSpeed: Float, sensitivity: Float) {
        FilaCamera_setExposure(nativeHandle, aperture, shutterSpeed, sensitivity)
    }
    actual fun setExposure(exposure: Float) {
        setExposure(1.0f, 1.2f, 100.0f * (1.0f / exposure))
    }
    actual fun getAperture(): Float = FilaCamera_getAperture(nativeHandle)
    actual fun getShutterSpeed(): Float = FilaCamera_getShutterSpeed(nativeHandle)
    actual fun getSensitivity(): Float = FilaCamera_getSensitivity(nativeHandle)
    actual fun getFocalLength(): Double = FilaCamera_getFocalLength(nativeHandle)
    
    actual fun setFocusDistance(distance: Float) {
        FilaCamera_setFocusDistance(nativeHandle, distance)
    }
    actual fun getFocusDistance(): Float = FilaCamera_getFocusDistance(nativeHandle)
    
    actual fun getFieldOfViewInDegrees(direction: Fov): Double = FilaCamera_getFieldOfViewInDegrees(nativeHandle, direction.ordinal.toUInt())

    actual fun getEntity(): Int = entity
}

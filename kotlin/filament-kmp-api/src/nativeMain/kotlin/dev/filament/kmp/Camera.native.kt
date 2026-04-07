@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package dev.filament.kmp

import kotlinx.cinterop.*
import dev.filament.kmp.cinterop.*
import cnames.structs.FilaCamera

actual class Camera internal constructor(
    internal var nativeHandle: CPointer<FilaCamera>?,
    private val entity: Int
) {
    actual enum class Projection {
        PERSPECTIVE, ORTHO;
        internal fun toNative(): UInt = ordinal.toUInt()
    }
    actual enum class Fov {
        VERTICAL, HORIZONTAL;
        internal fun toNative(): UInt = ordinal.toUInt()
    }
 
    actual fun setProjection(projection: Projection, left: Double, right: Double, bottom: Double, top: Double, near: Double, far: Double) {
        FilaCamera_setProjection(nativeHandle, projection.toNative(), left, right, bottom, top, near, far)
    }
    actual fun setProjection(fovInDegrees: Double, aspect: Double, near: Double, far: Double, direction: Fov) {
        FilaCamera_setProjectionFov(nativeHandle, fovInDegrees, aspect, near, far, direction.toNative())
    }
    actual fun setLensProjection(focalLength: Double, aspect: Double, near: Double, far: Double) {
        FilaCamera_setLensProjection(nativeHandle, focalLength, aspect, near, far)
    }
    actual fun setCustomProjection(matrix: DoubleArray, near: Double, far: Double) {
        memScoped {
            FilaCamera_setCustomProjection(nativeHandle, matrix.toCValues().ptr, null, near, far)
        }
    }
    actual fun setCustomProjection(matrix: DoubleArray, matrixForCulling: DoubleArray, near: Double, far: Double) {
        memScoped {
            FilaCamera_setCustomProjection(nativeHandle, matrix.toCValues().ptr, matrixForCulling.toCValues().ptr, near, far)
        }
    }
    
    actual fun setScaling(x: Double, y: Double) {
        FilaCamera_setScaling(nativeHandle, x, y)
    }
    actual fun getScaling(out: DoubleArray?): DoubleArray {
        val result = out ?: DoubleArray(4)
        memScoped {
            val ptr = allocArray<DoubleVar>(4)
            FilaCamera_getScaling(nativeHandle, ptr)
            for (i in 0 until 4) result[i] = ptr[i]
        }
        return result
    }
    actual fun setShift(x: Double, y: Double) {
        FilaCamera_setShift(nativeHandle, x, y)
    }
    actual fun getShift(out: DoubleArray?): DoubleArray {
        val result = out ?: DoubleArray(2)
        memScoped {
            val ptr = allocArray<DoubleVar>(2)
            FilaCamera_getShift(nativeHandle, ptr)
            for (i in 0 until 2) result[i] = ptr[i]
        }
        return result
    }
    
    actual fun lookAt(eyeX: Double, eyeY: Double, eyeZ: Double, centerX: Double, centerY: Double, centerZ: Double, upX: Double, upY: Double, upZ: Double) {
        FilaCamera_lookAt(nativeHandle, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ)
    }
    
    actual fun setModelMatrix(modelMatrix: FloatArray) {
        memScoped {
            FilaCamera_setModelMatrix(nativeHandle, modelMatrix.toCValues().ptr)
        }
    }
    actual fun setModelMatrix(modelMatrix: DoubleArray) {
        memScoped {
            FilaCamera_setModelMatrixFp64(nativeHandle, modelMatrix.toCValues().ptr)
        }
    }
    
    actual fun getProjectionMatrix(out: DoubleArray?): DoubleArray {
        val result = out ?: DoubleArray(16)
        memScoped {
            val ptr = allocArray<DoubleVar>(16)
            FilaCamera_getProjectionMatrix(nativeHandle, ptr)
            for (i in 0 until 16) result[i] = ptr[i]
        }
        return result
    }
    actual fun getCullingProjectionMatrix(out: DoubleArray?): DoubleArray {
        val result = out ?: DoubleArray(16)
        memScoped {
            val ptr = allocArray<DoubleVar>(16)
            FilaCamera_getCullingProjectionMatrix(nativeHandle, ptr)
            for (i in 0 until 16) result[i] = ptr[i]
        }
        return result
    }
    
    actual fun getModelMatrix(out: FloatArray?): FloatArray {
        val result = out ?: FloatArray(16)
        memScoped {
            val ptr = allocArray<FloatVar>(16)
            FilaCamera_getModelMatrix(nativeHandle, ptr)
            for (i in 0 until 16) result[i] = ptr[i]
        }
        return result
    }
    actual fun getModelMatrix(out: DoubleArray?): DoubleArray {
        val result = out ?: DoubleArray(16)
        memScoped {
            val ptr = allocArray<DoubleVar>(16)
            FilaCamera_getModelMatrixFp64(nativeHandle, ptr)
            for (i in 0 until 16) result[i] = ptr[i]
        }
        return result
    }
    
    actual fun getViewMatrix(out: FloatArray?): FloatArray {
        val result = out ?: FloatArray(16)
        memScoped {
            val ptr = allocArray<FloatVar>(16)
            FilaCamera_getViewMatrix(nativeHandle, ptr)
            for (i in 0 until 16) result[i] = ptr[i]
        }
        return result
    }
    actual fun getViewMatrix(out: DoubleArray?): DoubleArray {
        val result = out ?: DoubleArray(16)
        memScoped {
            val ptr = allocArray<DoubleVar>(16)
            FilaCamera_getViewMatrixFp64(nativeHandle, ptr)
            for (i in 0 until 16) result[i] = ptr[i]
        }
        return result
    }
    
    actual fun getPosition(out: FloatArray?): FloatArray {
        val result = out ?: FloatArray(3)
        memScoped {
            val ptr = allocArray<FloatVar>(3)
            FilaCamera_getPosition(nativeHandle, ptr)
            for (i in 0 until 3) result[i] = ptr[i]
        }
        return result
    }
    
    actual fun getLeftVector(out: FloatArray?): FloatArray {
        val result = out ?: FloatArray(3)
        memScoped {
            val ptr = allocArray<FloatVar>(3)
            FilaCamera_getLeftVector(nativeHandle, ptr)
            for (i in 0 until 3) result[i] = ptr[i]
        }
        return result
    }
    actual fun getUpVector(out: FloatArray?): FloatArray {
        val result = out ?: FloatArray(3)
        memScoped {
            val ptr = allocArray<FloatVar>(3)
            FilaCamera_getUpVector(nativeHandle, ptr)
            for (i in 0 until 3) result[i] = ptr[i]
        }
        return result
    }
    actual fun getForwardVector(out: FloatArray?): FloatArray {
        val result = out ?: FloatArray(3)
        memScoped {
            val ptr = allocArray<FloatVar>(3)
            FilaCamera_getForwardVector(nativeHandle, ptr)
            for (i in 0 until 3) result[i] = ptr[i]
        }
        return result
    }
    
    actual fun getNear(): Float = FilaCamera_getNear(nativeHandle).toFloat()
    actual fun getCullingFar(): Float = FilaCamera_getCullingFar(nativeHandle).toFloat()
    
    actual fun setExposure(aperture: Float, shutterSpeed: Float, sensitivity: Float) {
        FilaCamera_setExposure(nativeHandle, aperture, shutterSpeed, sensitivity)
    }
    actual fun getAperture(): Float = FilaCamera_getAperture(nativeHandle)
    actual fun getShutterSpeed(): Float = FilaCamera_getShutterSpeed(nativeHandle)
    actual fun getSensitivity(): Float = FilaCamera_getSensitivity(nativeHandle)
    
    actual fun setFocusDistance(distance: Float) {
        FilaCamera_setFocusDistance(nativeHandle, distance)
    }
    actual fun getFocusDistance(): Float = FilaCamera_getFocusDistance(nativeHandle)
    
    actual fun getEntity(): Int = entity
}

@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package dev.filament.kmp

import kotlinx.cinterop.*
import dev.filament.kmp.cinterop.*
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
    actual fun lookAt(eyeX: Double, eyeY: Double, eyeZ: Double, centerX: Double, centerY: Double, centerZ: Double, upX: Double, upY: Double, upZ: Double) {
        FilaCamera_lookAt(nativeHandle, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ)
    }
    actual fun setModelMatrix(modelMatrix: FloatArray) {
        memScoped {
            val cMatrix = allocArray<FloatVar>(16)
            for (i in 0 until 16) cMatrix[i] = modelMatrix[i]
            FilaCamera_setModelMatrix(nativeHandle, cMatrix)
        }
    }
    actual fun setModelMatrix(modelMatrix: DoubleArray) {
        memScoped {
            val cMatrix = allocArray<DoubleVar>(16)
            for (i in 0 until 16) cMatrix[i] = modelMatrix[i]
            FilaCamera_setModelMatrixFp64(nativeHandle, cMatrix)
        }
    }
    actual fun getProjectionMatrix(out: DoubleArray?): DoubleArray {
        val result = out ?: DoubleArray(16)
        memScoped {
            val cMatrix = allocArray<DoubleVar>(16)
            FilaCamera_getProjectionMatrix(nativeHandle, cMatrix)
            for (i in 0 until 16) result[i] = cMatrix[i]
        }
        return result
    }
    actual fun getViewMatrix(out: FloatArray?): FloatArray {
        val result = out ?: FloatArray(16)
        memScoped {
            val cMatrix = allocArray<FloatVar>(16)
            FilaCamera_getViewMatrix(nativeHandle, cMatrix)
            for (i in 0 until 16) result[i] = cMatrix[i]
        }
        return result
    }
    actual fun getViewMatrix(out: DoubleArray?): DoubleArray {
        val result = out ?: DoubleArray(16)
        memScoped {
            val cMatrix = allocArray<DoubleVar>(16)
            FilaCamera_getViewMatrixFp64(nativeHandle, cMatrix)
            for (i in 0 until 16) result[i] = cMatrix[i]
        }
        return result
    }
    actual fun getPosition(out: FloatArray?): FloatArray {
        val result = out ?: FloatArray(3)
        memScoped {
            val cPos = allocArray<FloatVar>(3)
            FilaCamera_getPosition(nativeHandle, cPos)
            for (i in 0 until 3) result[i] = cPos[i]
        }
        return result
    }
    actual fun getEntity(): Int = entity
}

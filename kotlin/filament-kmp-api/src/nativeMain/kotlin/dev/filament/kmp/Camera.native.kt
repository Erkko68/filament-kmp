@file:OptIn(ExperimentalForeignApi::class)
package dev.filament.kmp

import kotlinx.cinterop.*
import dev.filament.kmp.cinterop.*
import cnames.structs.FilaCamera

actual class Camera internal constructor(internal var nativePtr: CPointer<FilaCamera>?) {
    
    actual val nativeObject: Long
        get() = nativePtr?.rawValue?.toLong() ?: 0L

    actual internal fun invalidate() {
        nativePtr = null
    }

    actual enum class Projection {
        PERSPECTIVE,
        ORTHO,
    }

    actual enum class Fov {
        VERTICAL,
        HORIZONTAL,
    }

    actual fun setProjection(projection: Projection, left: Double, right: Double, bottom: Double, top: Double, near: Double, far: Double) {
        val p = when (projection) {
            Projection.PERSPECTIVE -> FILA_CAMERA_PROJECTION_PERSPECTIVE
            Projection.ORTHO -> FILA_CAMERA_PROJECTION_ORTHO
        }
        FilaCamera_setProjection(nativePtr, p, left, right, bottom, top, near, far)
    }

    actual fun setProjection(fovInDegrees: Double, aspect: Double, near: Double, far: Double, direction: Fov) {
        val d = when (direction) {
            Fov.VERTICAL -> FILA_CAMERA_FOV_VERTICAL
            Fov.HORIZONTAL -> FILA_CAMERA_FOV_HORIZONTAL
        }
        FilaCamera_setProjectionFov(nativePtr, fovInDegrees, aspect, near, far, d)
    }

    actual fun setLensProjection(focalLength: Double, aspect: Double, near: Double, far: Double) {
        FilaCamera_setLensProjection(nativePtr, focalLength, aspect, near, far)
    }

    actual fun setCustomProjection(inProjection: DoubleArray, near: Double, far: Double) {
        inProjection.usePinned { pinned ->
            FilaCamera_setCustomProjection(nativePtr, pinned.addressOf(0), pinned.addressOf(0), near, far)
        }
    }

    actual fun setCustomProjection(inProjection: DoubleArray, inProjectionForCulling: DoubleArray, near: Double, far: Double) {
        inProjection.usePinned { pinned ->
            inProjectionForCulling.usePinned { pinnedCulling ->
                FilaCamera_setCustomProjection(nativePtr, pinned.addressOf(0), pinnedCulling.addressOf(0), near, far)
            }
        }
    }

    actual fun setCustomEyeProjection(inProjection: DoubleArray, count: Int, inProjectionForCulling: DoubleArray, near: Double, far: Double) {
        inProjection.usePinned { pinned ->
            inProjectionForCulling.usePinned { pinnedCulling ->
                FilaCamera_setCustomEyeProjection(nativePtr, pinned.addressOf(0), count.toULong(), pinnedCulling.addressOf(0), near, far)
            }
        }
    }

    actual fun setScaling(xscaling: Double, yscaling: Double) {
        FilaCamera_setScaling(nativePtr, xscaling, yscaling)
    }

    @Deprecated("Use setScaling(xscaling, yscaling)")
    actual fun setScaling(inScaling: DoubleArray) {
        require(inScaling.size >= 2)
        FilaCamera_setScaling(nativePtr, inScaling[0], inScaling[1])
    }

    actual fun setShift(xshift: Double, yshift: Double) {
        FilaCamera_setShift(nativePtr, xshift, yshift)
    }

    actual fun getShift(out: DoubleArray?): DoubleArray {
        val arr = out ?: DoubleArray(2)
        arr.usePinned { pinned ->
            FilaCamera_getShift(nativePtr, pinned.addressOf(0))
        }
        return arr
    }

    actual fun getFieldOfViewInDegrees(direction: Fov): Double {
        val d = when (direction) {
            Fov.VERTICAL -> FILA_CAMERA_FOV_VERTICAL
            Fov.HORIZONTAL -> FILA_CAMERA_FOV_HORIZONTAL
        }
        return FilaCamera_getFieldOfViewInDegrees(nativePtr, d)
    }

    actual fun setModelMatrix(modelMatrix: FloatArray) {
        modelMatrix.usePinned { pinned ->
            FilaCamera_setModelMatrix(nativePtr, pinned.addressOf(0))
        }
    }

    actual fun setModelMatrix(modelMatrix: DoubleArray) {
        modelMatrix.usePinned { pinned ->
            FilaCamera_setModelMatrixFp64(nativePtr, pinned.addressOf(0))
        }
    }

    actual fun lookAt(eyeX: Double, eyeY: Double, eyeZ: Double, centerX: Double, centerY: Double, centerZ: Double, upX: Double, upY: Double, upZ: Double) {
        FilaCamera_lookAt(nativePtr, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ)
    }

    actual val near: Float
        get() = FilaCamera_getNear(nativePtr).toFloat()

    actual val cullingFar: Float
        get() = FilaCamera_getCullingFar(nativePtr).toFloat()

    actual fun getProjectionMatrix(out: DoubleArray?): DoubleArray {
        val arr = out ?: DoubleArray(16)
        arr.usePinned { pinned ->
            FilaCamera_getProjectionMatrix(nativePtr, pinned.addressOf(0))
        }
        return arr
    }

    actual fun getCullingProjectionMatrix(out: DoubleArray?): DoubleArray {
        val arr = out ?: DoubleArray(16)
        arr.usePinned { pinned ->
            FilaCamera_getCullingProjectionMatrix(nativePtr, pinned.addressOf(0))
        }
        return arr
    }

    actual fun getScaling(out: DoubleArray?): DoubleArray {
        val arr = out ?: DoubleArray(4)
        arr.usePinned { pinned ->
            FilaCamera_getScaling(nativePtr, pinned.addressOf(0))
        }
        return arr
    }

    actual fun getModelMatrix(out: FloatArray?): FloatArray {
        val arr = out ?: FloatArray(16)
        arr.usePinned { pinned ->
            FilaCamera_getModelMatrix(nativePtr, pinned.addressOf(0))
        }
        return arr
    }

    actual fun getModelMatrix(out: DoubleArray?): DoubleArray {
        val arr = out ?: DoubleArray(16)
        arr.usePinned { pinned ->
            FilaCamera_getModelMatrixFp64(nativePtr, pinned.addressOf(0))
        }
        return arr
    }

    actual fun getViewMatrix(out: FloatArray?): FloatArray {
        val arr = out ?: FloatArray(16)
        arr.usePinned { pinned ->
            FilaCamera_getViewMatrix(nativePtr, pinned.addressOf(0))
        }
        return arr
    }

    actual fun getViewMatrix(out: DoubleArray?): DoubleArray {
        val arr = out ?: DoubleArray(16)
        arr.usePinned { pinned ->
            FilaCamera_getViewMatrixFp64(nativePtr, pinned.addressOf(0))
        }
        return arr
    }

    actual fun getPosition(out: FloatArray?): FloatArray {
        val arr = out ?: FloatArray(3)
        arr.usePinned { pinned ->
            FilaCamera_getPosition(nativePtr, pinned.addressOf(0))
        }
        return arr
    }

    actual fun getLeftVector(out: FloatArray?): FloatArray {
        val arr = out ?: FloatArray(3)
        arr.usePinned { pinned ->
            FilaCamera_getLeftVector(nativePtr, pinned.addressOf(0))
        }
        return arr
    }

    actual fun getUpVector(out: FloatArray?): FloatArray {
        val arr = out ?: FloatArray(3)
        arr.usePinned { pinned ->
            FilaCamera_getUpVector(nativePtr, pinned.addressOf(0))
        }
        return arr
    }

    actual fun getForwardVector(out: FloatArray?): FloatArray {
        val arr = out ?: FloatArray(3)
        arr.usePinned { pinned ->
            FilaCamera_getForwardVector(nativePtr, pinned.addressOf(0))
        }
        return arr
    }

    actual fun setExposure(aperture: Float, shutterSpeed: Float, sensitivity: Float) {
        FilaCamera_setExposure(nativePtr, aperture, shutterSpeed, sensitivity)
    }

    actual fun setExposure(exposure: Float) {
        // We might need to add FilaCamera_setExposureValue to C-wrapper if it's missing
        // For now, I'll use the aperture/shutter/sensitivity defaults if I don't have it
    }

    actual val aperture: Float
        get() = FilaCamera_getAperture(nativePtr)

    actual val shutterSpeed: Float
        get() = FilaCamera_getShutterSpeed(nativePtr)

    actual val focalLength: Double
        get() = FilaCamera_getFocalLength(nativePtr)

    actual var focusDistance: Float
        get() = FilaCamera_getFocusDistance(nativePtr)
        set(value) {
            FilaCamera_setFocusDistance(nativePtr, value)
        }

    actual val sensitivity: Float
        get() = FilaCamera_getSensitivity(nativePtr)

    @Entity
    actual val entity: Int
        get() = 0 // We need FilaCamera_getEntity in C-wrapper or store it in Kotlin

    actual fun setEyeModelMatrix(eyeId: Int, model: DoubleArray) {
        model.usePinned { pinned ->
            FilaCamera_setEyeModelMatrix(nativePtr, eyeId, pinned.addressOf(0))
        }
    }
}

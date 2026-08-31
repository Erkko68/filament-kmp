package io.github.erkko68.filament

import io.github.erkko68.filament.web.interop.readNumbersInto

import io.github.erkko68.filament.web.interop.jsNumbers
import io.github.erkko68.filament.web.interop.toJsArray
import io.github.erkko68.filament.web.interop.toJsNumbers

import io.github.erkko68.filament.web.Camera as JSCamera
import io.github.erkko68.filament.web.Camera_Projection
import io.github.erkko68.filament.web.Camera_Fov

// backend::CONFIG_MAX_STEREOSCOPIC_EYES
private const val MAX_STEREOSCOPIC_EYES = 4

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
actual class Camera @InternalFilamentApi constructor(internal val jsCamera: JSCamera) {

    actual fun setProjection(
        projection: Projection,
        left: Double,
        right: Double,
        bottom: Double,
        top: Double,
        near: Double,
        far: Double
    ) {
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
        val jsFov = if (direction == Fov.VERTICAL) Camera_Fov.VERTICAL else Camera_Fov.HORIZONTAL
        jsCamera.setProjectionFov(fovInDegrees, aspect, near, far, jsFov)
    }

    actual fun setLensProjection(
        focalLength: Double,
        aspect: Double,
        near: Double,
        far: Double
    ) {
        jsCamera.setLensProjection(focalLength, aspect, near, far)
    }

    actual fun setCustomProjection(matrix: DoubleArray, near: Double, far: Double) {
        jsCamera.setCustomProjection(matrix.toJsNumbers(), near, far)
    }

    actual fun setCustomProjection(
        matrix: DoubleArray,
        matrixForCulling: DoubleArray,
        near: Double,
        far: Double
    ) {
        // No 2-matrix setCustomProjection in JS; the per-eye form writes the same state, so feed
        // it the one projection repeated for every eye (extra entries past the engine's
        // stereoscopicEyeCount are ignored, and the count must be >= it).
        val projections = List(MAX_STEREOSCOPIC_EYES) { matrix.toJsNumbers() }.toJsArray()
        jsCamera.setCustomEyeProjection(projections, matrixForCulling.toJsNumbers(), near, far)
    }

    actual fun setCustomEyeProjection(
        projection: DoubleArray,
        count: Int,
        projectionForCulling: DoubleArray,
        near: Double,
        far: Double
    ) {
        // `projection` is count 4x4 matrices packed end to end.
        val projections = List(count) { eye ->
            projection.copyOfRange(eye * 16, eye * 16 + 16).toJsNumbers()
        }.toJsArray()
        jsCamera.setCustomEyeProjection(projections, projectionForCulling.toJsNumbers(), near, far)
    }

    actual fun setEyeModelMatrix(eyeId: Int, modelMatrix: DoubleArray) {
        jsCamera.setEyeModelMatrix(eyeId.toDouble(), modelMatrix.toJsNumbers())
    }

    actual fun setScaling(x: Double, y: Double) {
        jsCamera.setScaling(jsNumbers(x, y))
    }

    actual fun getScaling(out: DoubleArray?): DoubleArray {
        val result = out ?: DoubleArray(4)
        (jsCamera.getScaling())?.readNumbersInto(result)
        return result
    }

    actual fun setShift(x: Double, y: Double) {
        jsCamera.setShift(jsNumbers(x, y))
    }

    actual fun getShift(out: DoubleArray?): DoubleArray {
        val result = out ?: DoubleArray(2)
        (jsCamera.getShift())?.readNumbersInto(result)
        return result
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
            jsNumbers(eyeX, eyeY, eyeZ),
            jsNumbers(centerX, centerY, centerZ),
            jsNumbers(upX, upY, upZ)
        )
    }

    actual fun setModelMatrix(modelMatrix: FloatArray) {
        jsCamera.setModelMatrix(modelMatrix.toJsNumbers())
    }

    actual fun setModelMatrix(modelMatrix: DoubleArray) {
        jsCamera.setModelMatrix(modelMatrix.toJsNumbers())
    }

    actual fun getProjectionMatrix(out: DoubleArray?): DoubleArray {
        val result = out ?: DoubleArray(16)
        (jsCamera.getProjectionMatrix())?.readNumbersInto(result)
        return result
    }

    actual fun getCullingProjectionMatrix(out: DoubleArray?): DoubleArray {
        val result = out ?: DoubleArray(16)
        (jsCamera.getCullingProjectionMatrix())?.readNumbersInto(result)
        return result
    }

    actual fun getModelMatrix(out: FloatArray?): FloatArray {
        val result = out ?: FloatArray(16)
        (jsCamera.getModelMatrix())?.readNumbersInto(result)
        return result
    }

    actual fun getModelMatrix(out: DoubleArray?): DoubleArray {
        val result = out ?: DoubleArray(16)
        (jsCamera.getModelMatrix())?.readNumbersInto(result)
        return result
    }

    actual fun getViewMatrix(out: FloatArray?): FloatArray {
        val result = out ?: FloatArray(16)
        (jsCamera.getViewMatrix())?.readNumbersInto(result)
        return result
    }

    actual fun getViewMatrix(out: DoubleArray?): DoubleArray {
        val result = out ?: DoubleArray(16)
        (jsCamera.getViewMatrix())?.readNumbersInto(result)
        return result
    }

    actual fun getPosition(out: FloatArray?): FloatArray {
        val result = out ?: FloatArray(3)
        (jsCamera.getPosition())?.readNumbersInto(result)
        return result
    }

    actual fun getLeftVector(out: FloatArray?): FloatArray {
        val result = out ?: FloatArray(3)
        (jsCamera.getLeftVector())?.readNumbersInto(result)
        return result
    }

    actual fun getUpVector(out: FloatArray?): FloatArray {
        val result = out ?: FloatArray(3)
        (jsCamera.getUpVector())?.readNumbersInto(result)
        return result
    }

    actual fun getForwardVector(out: FloatArray?): FloatArray {
        val result = out ?: FloatArray(3)
        (jsCamera.getForwardVector())?.readNumbersInto(result)
        return result
    }

    actual val near: Float
        get() = jsCamera.getNear().toFloat()

    actual val cullingFar: Float
        get() = jsCamera.getCullingFar().toFloat()

    actual fun setExposure(aperture: Float, shutterSpeed: Float, sensitivity: Float) {
        jsCamera.setExposure(aperture.toDouble(), shutterSpeed.toDouble(), sensitivity.toDouble())
    }

    actual fun setExposure(exposure: Float) {
        jsCamera.setExposureDirect(exposure.toDouble())
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
        val jsFov = when (direction) {
            Fov.VERTICAL -> Camera_Fov.VERTICAL
            Fov.HORIZONTAL -> Camera_Fov.HORIZONTAL
        }
        return jsCamera.getFieldOfViewInDegrees(jsFov).toDouble()
    }

    actual val entity: Entity
        get() = jsCamera.getEntity().getId().toInt()

    actual enum class Projection { PERSPECTIVE, ORTHO }
    actual enum class Fov { VERTICAL, HORIZONTAL }

}
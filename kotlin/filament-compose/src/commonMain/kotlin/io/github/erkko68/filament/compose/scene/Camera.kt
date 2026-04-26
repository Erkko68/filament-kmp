package io.github.erkko68.filament.compose.scene

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import io.github.erkko68.filament.Camera as FilamentCamera
import io.github.erkko68.filament.compose.LocalCameraConfig
import io.github.erkko68.filament.compose.LocalCameraConfigController
import io.github.erkko68.filament.utils.Float2

// ── Public: projection type ───────────────────────────────────────────────────

/**
 * Camera projection. Pass an instance as the [Camera.projection] argument.
 *
 * - [Perspective] — FOV-based (most common; suitable for 3-D scenes).
 * - [Orthographic] — parallel projection (useful for UI, 2-D, CAD).
 * - [Lens] — physical lens simulation (focal-length / sensor size).
 */
sealed class Projection {
    data class Perspective(
        val fovDegrees: Double = 45.0,
        val near: Double = 0.1,
        val far: Double = 100.0,
        val fovDirection: FilamentCamera.Fov = FilamentCamera.Fov.VERTICAL,
    ) : Projection()

    data class Orthographic(
        val left: Double = -1.0,
        val right: Double = 1.0,
        val bottom: Double = -1.0,
        val top: Double = 1.0,
        val near: Double = 0.0,
        val far: Double = 1.0,
    ) : Projection()

    data class Lens(
        val focalLength: Double = 28.0,
        val near: Double = 0.1,
        val far: Double = 100.0,
    ) : Projection()
}

// ── Public: photographic exposure ─────────────────────────────────────────────

/**
 * Photographic exposure (aperture / shutter-speed / ISO triangle).
 *
 * Default values correspond to a typical outdoor daylight exposure (EV ≈ 15).
 */
data class Exposure(
    val aperture: Float = 16f,
    val shutterSpeed: Float = 1f / 125f,
    val sensitivity: Float = 100f,
)

// ── Internal: change-detection key ───────────────────────────────────────────

internal data class CameraConfig(
    val eye: Position,
    val target: Position,
    val up: Direction,
    val projection: Projection,
    val exposure: Exposure,
    val shift: Float2,
    val scaling: Float2,
) {
    fun applyTo(camera: FilamentCamera, aspect: Double) {
        camera.lookAt(
            eye.x.toDouble(), eye.y.toDouble(), eye.z.toDouble(),
            target.x.toDouble(), target.y.toDouble(), target.z.toDouble(),
            up.x.toDouble(), up.y.toDouble(), up.z.toDouble(),
        )
        when (val p = projection) {
            is Projection.Perspective  -> camera.setProjection(p.fovDegrees, aspect, p.near, p.far, p.fovDirection)
            is Projection.Orthographic -> camera.setProjection(FilamentCamera.Projection.ORTHO, p.left, p.right, p.bottom, p.top, p.near, p.far)
            is Projection.Lens         -> camera.setLensProjection(p.focalLength, aspect, p.near, p.far)
        }
        camera.setExposure(exposure.aperture, exposure.shutterSpeed, exposure.sensitivity)
        camera.setShift(shift.x.toDouble(), shift.y.toDouble())
        camera.setScaling(scaling.x.toDouble(), scaling.y.toDouble())
    }
}

// ── Composable ────────────────────────────────────────────────────────────────

/**
 * Configures the scene camera.
 *
 * [eye] and [target] are required — a camera that isn't positioned is not useful.
 * All other parameters have sensible defaults and can be overridden as needed.
 *
 * Example:
 * ```kotlin
 * Camera(
 *     eye        = Position(0f, 5f, 10f),
 *     target     = Position(0f, 0f, 0f),
 *     projection = Projection.Perspective(fovDegrees = 60.0),
 *     exposure   = Exposure(aperture = 8f, shutterSpeed = 1f / 60f),
 * )
 * ```
 */
@Composable
fun Camera(
    eye: Position,
    target: Position,
    up: Direction = Direction(0f, 1f, 0f),
    projection: Projection = Projection.Perspective(),
    exposure: Exposure = Exposure(),
    shift: Float2 = Float2(0f, 0f),
    scaling: Float2 = Float2(1f, 1f),
) {
    val currentConfig = LocalCameraConfig.current
    val controller = LocalCameraConfigController.current
    val config = CameraConfig(eye, target, up, projection, exposure, shift, scaling)
    SideEffect {
        if (config != currentConfig) controller(config)
    }
}

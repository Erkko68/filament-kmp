package io.github.erkko68.filament.compose.scene

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import io.github.erkko68.filament.compose.LocalFilamentCamera
import io.github.erkko68.filament.utils.Float3

/**
 * Camera exposure settings (photographic model).
 */
data class CameraExposure(
    val aperture: Float = 16f,
    val shutterSpeed: Float = 1f / 125f,
    val sensitivity: Float = 100f,
)

/**
 * Configures the scene camera with a perspective projection.
 *
 * FOV aspect ratio is managed by [FilamentSurface] on viewport changes.
 * This composable controls eye position, look-at target, and exposure.
 */
@Composable
fun PerspectiveCamera(
    eye: Float3 = Float3(0f, 1f, 10f),
    target: Float3 = Float3(0f, 0f, 0f),
    up: Float3 = Float3(0f, 1f, 0f),
    fovDegrees: Double = 45.0,
    near: Double = 0.1,
    far: Double = 100.0,
    exposure: CameraExposure = CameraExposure(),
) {
    val camera = LocalFilamentCamera.current

    SideEffect {
        camera.lookAt(
            eye.x.toDouble(), eye.y.toDouble(), eye.z.toDouble(),
            target.x.toDouble(), target.y.toDouble(), target.z.toDouble(),
            up.x.toDouble(), up.y.toDouble(), up.z.toDouble(),
        )
        camera.setExposure(exposure.aperture, exposure.shutterSpeed, exposure.sensitivity)
    }
}

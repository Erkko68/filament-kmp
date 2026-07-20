package io.github.erkko68.filament.compose.scene

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.erkko68.filament.Camera as FilamentCamera
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

// ── Hoisted state ─────────────────────────────────────────────────────────────

/**
 * Hoisted, observable camera state. Create with [rememberCameraState] and pass to
 * [io.github.erkko68.filament.compose.FilamentView] for full control over the camera.
 *
 * All fields are observable [androidx.compose.runtime.MutableState] — read them inside a
 * composable to subscribe to changes, or write them from anywhere on the main thread to
 * drive the camera imperatively.
 *
 * Read-only matrices ([viewMatrix], [projectionMatrix]) reflect the underlying Filament
 * camera and are valid only while the state is attached to a [FilamentView].
 *
 * ```kotlin
 * val cameraState = rememberCameraState(initialEye = Position(0f, 2f, 5f))
 * FilamentView(scene = scene, cameraState = cameraState)
 *
 * // Read the view matrix from anywhere
 * LaunchedEffect(cameraState.eye) {
 *     val v = cameraState.viewMatrix  // may be null until attached
 * }
 * ```
 */
class CameraState internal constructor(
    initialEye: Position,
    initialTarget: Position,
    initialUp: Direction,
    initialProjection: Projection,
    initialExposure: Exposure,
    initialFocusDistance: Float,
    initialShift: Float2,
    initialScaling: Float2,
) {
    var eye:        Position   by mutableStateOf(initialEye)
    var target:     Position   by mutableStateOf(initialTarget)
    var up:         Direction  by mutableStateOf(initialUp)
    var projection: Projection by mutableStateOf(initialProjection)
    var exposure:   Exposure   by mutableStateOf(initialExposure)

    /**
     * Distance from the camera to the plane of focus in world units, used by
     * `PostProcessing(depthOfField = …)` to place the focal plane. Ignored without DoF.
     */
    var focusDistance: Float   by mutableStateOf(initialFocusDistance)
    var shift:      Float2     by mutableStateOf(initialShift)
    var scaling:    Float2     by mutableStateOf(initialScaling)

    internal var attachedCamera: FilamentCamera? = null
    internal var aspect: Double = 1.0

    internal fun attach(camera: FilamentCamera) {
        check(attachedCamera == null || attachedCamera == camera) {
            "This CameraState is already attached to another FilamentView/rememberRenderTargetTexture. " +
                "Each view needs its own CameraState — sharing one would leave aspect ratio and " +
                "matrix reads racing between views."
        }
        attachedCamera = camera
    }

    internal fun detach(camera: FilamentCamera) {
        if (attachedCamera == camera) attachedCamera = null
    }

    /**
     * 4×4 column-major view matrix (world→view) computed by Filament. Null until this
     * state is attached to a [io.github.erkko68.filament.compose.FilamentView].
     */
    val viewMatrix: FloatArray?
        get() = attachedCamera?.getViewMatrix(null as FloatArray?)

    /**
     * 4×4 column-major projection matrix (view→clip) computed by Filament. Null until
     * this state is attached to a [io.github.erkko68.filament.compose.FilamentView].
     */
    val projectionMatrix: DoubleArray?
        get() = attachedCamera?.getProjectionMatrix(null as DoubleArray?)

    internal fun snapshot(): CameraSnapshot =
        CameraSnapshot(eye, target, up, projection, exposure, focusDistance, shift, scaling)
}

/**
 * Immutable snapshot of [CameraState] used to push values to the underlying Filament
 * camera without holding a reference to the state object.
 */
internal data class CameraSnapshot(
    val eye: Position,
    val target: Position,
    val up: Direction,
    val projection: Projection,
    val exposure: Exposure,
    val focusDistance: Float,
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
        camera.focusDistance = focusDistance
        camera.setShift(shift.x.toDouble(), shift.y.toDouble())
        camera.setScaling(scaling.x.toDouble(), scaling.y.toDouble())
    }
}

/**
 * Creates and remembers a [CameraState].
 *
 * The `initial*` values seed the state on first composition only — like every Compose
 * `remember*State` (`rememberPagerState(initialPage = …)`, …), later changes to these
 * arguments are ignored. To drive the camera from other state, mutate the returned object:
 *
 * ```kotlin
 * val cameraState = rememberCameraState(initialEye = Position(0f, 2f, 5f))
 * LaunchedEffect(animatedEye) { cameraState.eye = animatedEye }
 * ```
 */
@Composable
fun rememberCameraState(
    initialEye: Position = Position(0f, 1f, 10f),
    initialTarget: Position = Position(0f, 0f, 0f),
    initialUp: Direction = Direction(0f, 1f, 0f),
    initialProjection: Projection = Projection.Perspective(),
    initialExposure: Exposure = Exposure(),
    initialFocusDistance: Float = 10f,
    initialShift: Float2 = Float2(0f, 0f),
    initialScaling: Float2 = Float2(1f, 1f),
): CameraState = remember {
    CameraState(initialEye, initialTarget, initialUp, initialProjection, initialExposure,
        initialFocusDistance, initialShift, initialScaling)
}


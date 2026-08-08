package io.github.erkko68.filament.compose.scene

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.erkko68.filament.Camera as FilamentCamera
import io.github.erkko68.filament.utils.Float2
import io.github.erkko68.filament.utils.Float4
import io.github.erkko68.filament.utils.Mat4

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
// @Stable: every property composition can observe is snapshot-backed; `attachedCamera` and
// `aspect` are internal bookkeeping written by the view, never read during composition.
@Stable
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
     * View matrix (world→view) computed by Filament. Null until this state is attached to a
     * [io.github.erkko68.filament.compose.FilamentView].
     *
     * Each read queries the live camera and builds a new [Mat4] — hold the result rather than
     * reading it repeatedly in a hot loop.
     */
    val viewMatrix: Mat4?
        get() = attachedCamera?.getViewMatrix(null as FloatArray?)?.toMat4()

    /**
     * Projection matrix (view→clip) computed by Filament. Null until this state is attached to a
     * [io.github.erkko68.filament.compose.FilamentView].
     *
     * Filament computes this in double precision; the components are narrowed to `Float` here to
     * match [Mat4]. For the full-precision array, go through the camera directly
     * (`viewState.view?.camera?.getProjectionMatrix()`).
     */
    val projectionMatrix: Mat4?
        get() = attachedCamera?.getProjectionMatrix(null as DoubleArray?)?.toMat4()

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


// Filament hands back column-major arrays, and Mat4's primary constructor takes columns — so the
// groups of four map straight across. (Mat4.of() is *not* the right tool: it reads row-major.)
private fun FloatArray.toMat4() = Mat4(
    Float4(this[0], this[1], this[2], this[3]),
    Float4(this[4], this[5], this[6], this[7]),
    Float4(this[8], this[9], this[10], this[11]),
    Float4(this[12], this[13], this[14], this[15]),
)

private fun DoubleArray.toMat4() = Mat4(
    Float4(this[0].toFloat(), this[1].toFloat(), this[2].toFloat(), this[3].toFloat()),
    Float4(this[4].toFloat(), this[5].toFloat(), this[6].toFloat(), this[7].toFloat()),
    Float4(this[8].toFloat(), this[9].toFloat(), this[10].toFloat(), this[11].toFloat()),
    Float4(this[12].toFloat(), this[13].toFloat(), this[14].toFloat(), this[15].toFloat()),
)

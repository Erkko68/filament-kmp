package io.github.erkko68.filament.compose.scene

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.IntSize
import kotlin.math.roundToInt
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
@Immutable
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
@Immutable
data class Exposure(
    val aperture: Float = 16f,
    val shutterSpeed: Float = 1f / 125f,
    val sensitivity: Float = 100f,
)

// ── Public: lens shift and scaling ───────────────────────────────────────────

/**
 * Lens shift — moves the projection off-axis without rotating the camera, as a fraction of the
 * viewport (`1` shifts the image by its full width/height; for pixels, divide by the viewport size).
 * Used for tilt-shift looks, and for keeping verticals parallel in architectural shots.
 *
 * Distinct from [LensScaling] for the same reason [Position] is distinct from [Direction]: both
 * are float pairs on the projection and trivially swapped by accident.
 */
@Immutable
data class LensShift(val x: Float, val y: Float) {
    /** From a filament-utils [Float2]. */
    constructor(v: Float2) : this(v.x, v.y)

    fun toFloat2() = Float2(x, y)

    companion object {
        /** No shift — the projection stays centred. */
        val None = LensShift(0f, 0f)
    }
}

/**
 * Projection scaling — multiplies the projection along each axis. `2` on one axis zooms that axis
 * in; unequal values give an anamorphic squeeze.
 *
 * Not to be confused with [Scale], which sizes an object in the scene; this reshapes the frustum.
 */
@Immutable
data class LensScaling(val x: Float, val y: Float) {
    /** Uniform on both axes. */
    constructor(v: Float) : this(v, v)
    /** From a filament-utils [Float2]. */
    constructor(v: Float2) : this(v.x, v.y)

    fun toFloat2() = Float2(x, y)

    companion object {
        /** Unscaled — the projection Filament computes from the [Projection] alone. */
        val Identity = LensScaling(1f, 1f)
    }
}

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
// @Stable: every property composition can observe is snapshot-backed; `attachedCamera` is internal
// bookkeeping written by the view, never read during composition.
@Stable
class CameraState internal constructor(
    initialEye: Position,
    initialTarget: Position,
    initialUp: Direction,
    initialProjection: Projection,
    initialExposure: Exposure,
    initialFocusDistance: Float,
    initialShift: LensShift,
    initialScaling: LensScaling,
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
    /** Off-axis lens shift; [LensShift.None] leaves the projection centred. */
    var shift:      LensShift  by mutableStateOf(initialShift)

    /** Per-axis projection scaling; [LensScaling.Identity] leaves the frustum as computed. */
    var scaling:    LensScaling by mutableStateOf(initialScaling)

    internal var attachedCamera: FilamentCamera? = null

    // Snapshot state, so worldToScreen's callers follow a resize. Written by the view, outside composition.
    internal var aspect by mutableDoubleStateOf(1.0)
    internal var layoutSize by mutableStateOf(IntSize.Zero)

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

    // Scratch buffers for the matrix getters — one CameraState is attached to one view (see
    // attach), so reads are single-threaded. Saves the array alloc, not the Mat4 alloc.
    private val tmpViewArray = DoubleArray(16)
    private val tmpProjArray = DoubleArray(16)

    /**
     * View matrix (world→view) computed by Filament. Null until this state is attached to a
     * [io.github.erkko68.filament.compose.FilamentView].
     *
     * Each read queries the live camera and builds a new [Mat4] — hold the result rather than
     * reading it repeatedly in a hot loop.
     */
    val viewMatrix: Mat4?
        get() = attachedCamera?.getViewMatrix(tmpViewArray)?.toMat4()

    /**
     * Projection matrix (view→clip) computed by Filament. Null until this state is attached to a
     * [io.github.erkko68.filament.compose.FilamentView].
     *
     * Filament computes this in double precision; the components are narrowed to `Float` here to
     * match [Mat4]. For the full-precision array, go through the camera directly
     * (`viewState.view?.camera?.getProjectionMatrix()`).
     */
    val projectionMatrix: Mat4?
        get() = attachedCamera?.getProjectionMatrix(out = tmpProjArray)?.toMat4()

    /**
     * Where [position] (world space) appears in the attached view, in pixels from its top-left corner, or null when
     * it is behind the camera or this state isn't attached to a view yet. Reads the camera's state, so a caller in
     * composition, layout or drawing re-runs when the camera moves or the view resizes. To pin an element there,
     * use [anchorTo].
     */
    fun worldToScreen(position: Position): Offset? {
        val lens = snapshot()
        val size = layoutSize
        val camera = attachedCamera ?: return null
        if (size.width <= 0 || size.height <= 0 || aspect <= 0.0) return null
        val v = camera.getViewMatrix(tmpViewArray)
        val p = camera.getProjectionMatrix(out = tmpProjArray)
        val x = position.x.toDouble(); val y = position.y.toDouble(); val z = position.z.toDouble()
        // Column-major: view space, then clip space.
        val ex = v[0] * x + v[4] * y + v[8] * z + v[12]
        val ey = v[1] * x + v[5] * y + v[9] * z + v[13]
        val ez = v[2] * x + v[6] * y + v[10] * z + v[14]
        val cx = p[0] * ex + p[4] * ey + p[8] * ez + p[12]
        val cy = p[1] * ex + p[5] * ey + p[9] * ez + p[13]
        val cw = p[3] * ex + p[7] * ey + p[11] * ez + p[15]
        if (cw <= 0.0) return null
        // getProjectionMatrix is the user projection: Filament adds the lens scaling and shift (2 × shift in NDC) at
        // render time (FCamera::getProjectionMatrix), so they are applied here.
        val nx = lens.scaling.x * cx / cw + 2.0 * lens.shift.x
        val ny = lens.scaling.y * cy / cw + 2.0 * lens.shift.y
        // NDC to pixels; Compose's y runs down.
        return Offset(((nx + 1.0) * 0.5 * size.width).toFloat(), ((1.0 - ny) * 0.5 * size.height).toFloat())
    }

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
    val shift: LensShift,
    val scaling: LensScaling,
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
    initialShift: LensShift = LensShift.None,
    initialScaling: LensScaling = LensScaling.Identity,
): CameraState = remember {
    CameraState(initialEye, initialTarget, initialUp, initialProjection, initialExposure,
        initialFocusDistance, initialShift, initialScaling)
}


// Filament hands back column-major arrays, and Mat4's primary constructor takes columns — so the
// groups of four map straight across. (Mat4.of() is *not* the right tool: it reads row-major.)
private fun DoubleArray.toMat4() = Mat4(
    Float4(this[0].toFloat(), this[1].toFloat(), this[2].toFloat(), this[3].toFloat()),
    Float4(this[4].toFloat(), this[5].toFloat(), this[6].toFloat(), this[7].toFloat()),
    Float4(this[8].toFloat(), this[9].toFloat(), this[10].toFloat(), this[11].toFloat()),
    Float4(this[12].toFloat(), this[13].toFloat(), this[14].toFloat(), this[15].toFloat()),
)

/**
 * Centres this element on [position] (world space) as seen through [cameraState]'s view, and hides it while the
 * point is behind the camera: labels, hotspots and annotations on a model. Place the element in the same `Box` as the
 * `FilamentView`, filling it, and put `anchorTo` first in its modifier chain (what comes before it doesn't move).
 * Camera motion re-places it without recomposing.
 *
 * ```kotlin
 * Box(Modifier.fillMaxSize()) {
 *     FilamentView(scene, Modifier.fillMaxSize(), cameraState = cam)
 *     Text("Engine", Modifier.anchorTo(cam, Position(0f, 1.2f, 0f)))
 * }
 * ```
 */
fun Modifier.anchorTo(cameraState: CameraState, position: Position): Modifier = layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    layout(placeable.width, placeable.height) {
        // Read in placement, so a camera change re-places without measuring again.
        val point = cameraState.worldToScreen(position) ?: return@layout
        placeable.place((point.x - placeable.width / 2f).roundToInt(), (point.y - placeable.height / 2f).roundToInt())
    }
}

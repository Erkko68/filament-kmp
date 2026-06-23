package io.github.erkko68.filament.compose.scene

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import io.github.erkko68.filament.compose.FilamentSceneScope
import io.github.erkko68.filament.compose.LocalFilamentEngine
import io.github.erkko68.filament.compose.OnFrame

/**
 * Attaches a [CameraState] to the surrounding [Group] so the camera follows that group's
 * world transform every frame — a declarative chase / first-person / mounted camera.
 *
 * Place it *inside* a [Group]; the group's transform becomes the camera's reference frame.
 * [eyeOffset] and [targetOffset] are expressed in that local space, so a third-person rig is
 * just an eye behind-and-above the origin looking at the origin:
 *
 * ```kotlin
 * val cam = rememberCameraState()
 * val scene = rememberFilamentScene {
 *     Group(position = carPosition, rotation = carRotation) {
 *         GltfInstance(car)
 *         CameraNode(cam, eyeOffset = Position(0f, 2f, -6f), targetOffset = Position(0f, 1f, 0f))
 *     }
 * }
 * FilamentView(scene, cameraState = cam)
 * ```
 *
 * The camera itself still lives on the [FilamentView] this [cameraState] is passed to; this node
 * only drives the state's `eye`/`target`/`up`. It has no effect until the group has a transform
 * component (always true for [Group]) and the state is attached to a view.
 *
 * @param cameraState The hoisted camera state to drive. Pass the same instance to a [FilamentView].
 * @param eyeOffset Camera position in the group's local space.
 * @param targetOffset Look-at point in the group's local space.
 * @param up Up direction in the group's local space.
 */
@Composable
fun FilamentSceneScope.CameraNode(
    cameraState: CameraState,
    eyeOffset: Position = Position(0f),
    targetOffset: Position = Position(0f, 0f, -1f),
    up: Direction = Direction(0f, 1f, 0f),
) {
    val engine = LocalFilamentEngine.current
    val parent = LocalParentEntity.current
    val world = remember { FloatArray(16) }

    OnFrame {
        if (parent == null) return@OnFrame
        val tm = engine.getTransformManager()
        if (tm.hasComponent(parent)) {
            tm.getWorldTransform(tm.getInstance(parent), world)
            cameraState.eye = world.transformPoint(eyeOffset)
            cameraState.target = world.transformPoint(targetOffset)
            cameraState.up = world.transformDirection(up).normalized()
        }
    }
}

/** Multiplies the column-major 4×4 [this] by the point `(p, 1)` and returns the xyz result. */
private fun FloatArray.transformPoint(p: Position): Position = Position(
    this[0] * p.x + this[4] * p.y + this[8] * p.z + this[12],
    this[1] * p.x + this[5] * p.y + this[9] * p.z + this[13],
    this[2] * p.x + this[6] * p.y + this[10] * p.z + this[14],
)

/** Multiplies the column-major 4×4 [this] by the direction `(d, 0)` — ignores translation. */
private fun FloatArray.transformDirection(d: Direction): Direction = Direction(
    this[0] * d.x + this[4] * d.y + this[8] * d.z,
    this[1] * d.x + this[5] * d.y + this[9] * d.z,
    this[2] * d.x + this[6] * d.y + this[10] * d.z,
)

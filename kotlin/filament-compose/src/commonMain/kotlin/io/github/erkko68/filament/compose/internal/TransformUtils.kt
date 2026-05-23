package io.github.erkko68.filament.compose.internal

import io.github.erkko68.filament.compose.scene.Position
import io.github.erkko68.filament.compose.scene.Scale
import io.github.erkko68.filament.utils.Quaternion

/**
 * Builds a column-major 4×4 transform matrix as `FloatArray` for `TransformManager.setTransform`.
 *
 * The composition is `T(position) * R(rotation) * S(scale) * T(-pivot)`, so rotation and scale
 * happen *about the pivot point* (in mesh space) before the model is placed at `position`. When
 * `pivot` is `(0,0,0)` this collapses to the standard TRS transform — the default behaviour.
 */
internal fun transformMatrix(
    position: Position,
    rotation: Quaternion,
    scale: Scale,
    pivot: Position = Position(0f),
): FloatArray {
    val x = rotation.x; val y = rotation.y; val z = rotation.z; val w = rotation.w

    // Pre-compute the (R·S) basis columns once — used twice (for the matrix entries and to
    // shift the translation column so the pivot ends up where `position` points).
    val m00 = (1f - 2f * (y * y + z * z)) * scale.x
    val m01 = (2f * (x * y + w * z)) * scale.x
    val m02 = (2f * (x * z - w * y)) * scale.x

    val m10 = (2f * (x * y - w * z)) * scale.y
    val m11 = (1f - 2f * (x * x + z * z)) * scale.y
    val m12 = (2f * (y * z + w * x)) * scale.y

    val m20 = (2f * (x * z + w * y)) * scale.z
    val m21 = (2f * (y * z - w * x)) * scale.z
    val m22 = (1f - 2f * (x * x + y * y)) * scale.z

    // Translation column: position − (R·S)·pivot. Cancels exactly to `position` when pivot=0.
    val tx = position.x - (m00 * pivot.x + m10 * pivot.y + m20 * pivot.z)
    val ty = position.y - (m01 * pivot.x + m11 * pivot.y + m21 * pivot.z)
    val tz = position.z - (m02 * pivot.x + m12 * pivot.y + m22 * pivot.z)

    return floatArrayOf(
        m00, m01, m02, 0f,   // column 0
        m10, m11, m12, 0f,   // column 1
        m20, m21, m22, 0f,   // column 2
        tx,  ty,  tz,  1f,   // column 3 (translation, pivot-adjusted)
    )
}

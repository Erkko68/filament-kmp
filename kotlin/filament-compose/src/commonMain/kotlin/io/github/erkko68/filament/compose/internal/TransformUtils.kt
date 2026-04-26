package io.github.erkko68.filament.compose.internal

import io.github.erkko68.filament.utils.Float3
import io.github.erkko68.filament.utils.Quaternion

/**
 * Builds a column-major 4x4 TRS matrix as FloatArray for TransformManager.setTransform.
 */
internal fun transformMatrix(position: Float3, rotation: Quaternion, scale: Float3): FloatArray {
    val x = rotation.x; val y = rotation.y; val z = rotation.z; val w = rotation.w
    return floatArrayOf(
        // column 0
        (1f - 2f * (y * y + z * z)) * scale.x,
        (2f * (x * y + w * z)) * scale.x,
        (2f * (x * z - w * y)) * scale.x,
        0f,
        // column 1
        (2f * (x * y - w * z)) * scale.y,
        (1f - 2f * (x * x + z * z)) * scale.y,
        (2f * (y * z + w * x)) * scale.y,
        0f,
        // column 2
        (2f * (x * z + w * y)) * scale.z,
        (2f * (y * z - w * x)) * scale.z,
        (1f - 2f * (x * x + y * y)) * scale.z,
        0f,
        // column 3 (translation)
        position.x, position.y, position.z, 1f,
    )
}

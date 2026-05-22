package io.github.erkko68.filament.compose.scene.primitives

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import io.github.erkko68.filament.Box
import io.github.erkko68.filament.MaterialInstance
import io.github.erkko68.filament.compose.scene.Position
import io.github.erkko68.filament.compose.scene.Scale
import io.github.erkko68.filament.utils.Quaternion

/**
 * A flat quad lying in the XZ plane (normal = +Y) centered on the origin. Use [rotation] to
 * orient it as a wall or ceiling.
 *
 * @param material  Material applied to the front face. The plane is single-sided unless the
 *   material is built with `doubleSided(true)`.
 * @param position  World-space center.
 * @param rotation  World-space rotation.
 * @param scale     Per-axis scale.
 * @param width     Size along the X axis in mesh space.
 * @param depth     Size along the Z axis in mesh space.
 */
@Composable
fun Plane(
    material: MaterialInstance,
    position: Position = Position(0f),
    rotation: Quaternion = Quaternion(),
    scale: Scale = Scale(1f),
    width: Float = 1f,
    depth: Float = 1f,
) {
    val mesh = remember(width, depth) { planeMesh(width, depth) }
    Mesh(mesh, material, position, rotation, scale)
}

private fun planeMesh(width: Float, depth: Float): MeshData {
    val w = width * 0.5f
    val d = depth * 0.5f

    val positions = floatArrayOf(
        -w, 0f,  d,
         w, 0f,  d,
         w, 0f, -d,
        -w, 0f, -d,
    )
    val normals = floatArrayOf(
        0f, 1f, 0f,
        0f, 1f, 0f,
        0f, 1f, 0f,
        0f, 1f, 0f,
    )
    val uvs = floatArrayOf(
        0f, 0f,
        1f, 0f,
        1f, 1f,
        0f, 1f,
    )
    val indices = intArrayOf(0, 1, 2,  0, 2, 3)

    return MeshData(
        positions   = positions,
        normals     = normals,
        uvs         = uvs,
        indices     = indices,
        boundingBox = Box(0f, 0f, 0f, w, 0.001f, d),
    )
}

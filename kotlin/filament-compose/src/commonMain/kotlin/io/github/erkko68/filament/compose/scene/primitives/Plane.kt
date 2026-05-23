package io.github.erkko68.filament.compose.scene.primitives

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import io.github.erkko68.filament.Box
import io.github.erkko68.filament.MaterialInstance
import io.github.erkko68.filament.compose.scene.Position
import io.github.erkko68.filament.compose.scene.Scale
import io.github.erkko68.filament.utils.Quaternion

/**
 * A flat quad lying in the XZ plane centered on the origin. The mesh is two-sided by default:
 * we emit a full set of vertices/triangles for both the +Y face and the -Y face with correctly
 * oriented normals, so lighting works from above *and* below without disabling face culling or
 * requiring a `doubleSided` material.
 *
 * @param material  Material applied to both faces.
 * @param position  World-space position of the [pivot] point.
 * @param rotation  World-space rotation. Use this to make a wall or a ceiling.
 * @param scale     Per-axis scale.
 * @param pivot     Point in mesh space that rotation/scale revolve around. Defaults to the plane centre.
 * @param width     Size along the X axis in mesh space.
 * @param depth     Size along the Z axis in mesh space.
 * @param doubleSided  When true (default) the mesh has both faces. Set false to omit the back
 *   side when you know nothing will ever look at it from below.
 * @param onCreate  Receives the renderable entity ID once the plane is added to the scene.
 */
@Composable
fun Plane(
    material: MaterialInstance,
    position: Position = Position(0f),
    rotation: Quaternion = Quaternion(),
    scale: Scale = Scale(1f),
    pivot: Position = Position(0f),
    width: Float = 1f,
    depth: Float = 1f,
    doubleSided: Boolean = true,
    onCreate: (entity: Int) -> Unit = {},
) {
    val mesh = remember(width, depth, doubleSided) { planeMesh(width, depth, doubleSided) }
    Mesh(mesh, material, position, rotation, scale, pivot, onCreate)
}

private fun planeMesh(width: Float, depth: Float, doubleSided: Boolean): MeshData {
    val w = width * 0.5f
    val d = depth * 0.5f

    // Top face — normal +Y, CCW when viewed from above.
    val topPositions = floatArrayOf(
        -w, 0f,  d,
         w, 0f,  d,
         w, 0f, -d,
        -w, 0f, -d,
    )
    val topNormals = floatArrayOf(
        0f, 1f, 0f,  0f, 1f, 0f,  0f, 1f, 0f,  0f, 1f, 0f,
    )
    val topUvs = floatArrayOf(
        0f, 0f,  1f, 0f,  1f, 1f,  0f, 1f,
    )
    val topIndices = intArrayOf(0, 1, 2,  0, 2, 3)

    if (!doubleSided) {
        return MeshData(
            positions = topPositions, normals = topNormals, uvs = topUvs, indices = topIndices,
            boundingBox = Box(0f, 0f, 0f, w, 0.001f, d),
        )
    }

    // Bottom face — same vertices duplicated with normal -Y. Winding reversed so it's CCW
    // when viewed from below.
    val positions = topPositions + topPositions
    val normals   = topNormals + floatArrayOf(
        0f, -1f, 0f,  0f, -1f, 0f,  0f, -1f, 0f,  0f, -1f, 0f,
    )
    val uvs       = topUvs + topUvs
    val indices   = topIndices + intArrayOf(4, 6, 5,  4, 7, 6)

    return MeshData(
        positions   = positions,
        normals     = normals,
        uvs         = uvs,
        indices     = indices,
        boundingBox = Box(0f, 0f, 0f, w, 0.001f, d),
    )
}

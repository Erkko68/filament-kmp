package io.github.erkko68.filament.compose.scene.primitives

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import io.github.erkko68.filament.Box
import io.github.erkko68.filament.MaterialInstance
import io.github.erkko68.filament.compose.FilamentSceneScope
import io.github.erkko68.filament.compose.scene.Position
import io.github.erkko68.filament.compose.scene.Scale
import io.github.erkko68.filament.utils.Quaternion

/**
 * A unit cube centered on the origin (in mesh space). Six faces, 24 vertices (4 per face so
 * normals and UVs are not shared across edges), 12 triangles.
 *
 * Each face's UV maps the full 0..1 square. The cube is sized via [size] (full edge length).
 *
 * @param material  The material to apply to every face. Use [rememberMaterial] +
 *   [rememberMaterialInstance] to construct one.
 * @param position  World-space position of the [pivot] point.
 * @param rotation  World-space rotation as a quaternion.
 * @param scale     Per-axis scale applied after [size].
 * @param pivot     Point in mesh space that rotation/scale revolve around and that ends up at
 *   [position]. Defaults to the cube centre.
 * @param size      Edge length in mesh space. The mesh is rebuilt when this changes.
 * @param onCreate  Receives the renderable entity ID once the cube is added to the scene.
 *   Use it to register the cube with `view.pick` callbacks or other entity-keyed maps.
 */
@Composable
fun FilamentSceneScope.Cube(
    material: MaterialInstance,
    position: Position = Position(0f),
    rotation: Quaternion = Quaternion(),
    scale: Scale = Scale(1f),
    pivot: Position = Position(0f),
    size: Float = 1f,
    onCreate: (entity: Int) -> Unit = {},
) {
    val mesh = remember(size) { cubeMesh(size) }
    Mesh(mesh, material, position, rotation, scale, pivot, onCreate)
}

private fun cubeMesh(size: Float): MeshData {
    val h = size * 0.5f

    // Per-face: 4 corners CCW when viewed from outside, normal pointing outward, UV (u,v) ∈ [0,1].
    // Order: +X, -X, +Y, -Y, +Z, -Z.
    val positions = floatArrayOf(
        // +X
         h, -h,  h,    h, -h, -h,    h,  h, -h,    h,  h,  h,
        // -X
        -h, -h, -h,   -h, -h,  h,   -h,  h,  h,   -h,  h, -h,
        // +Y
        -h,  h,  h,    h,  h,  h,    h,  h, -h,   -h,  h, -h,
        // -Y
        -h, -h, -h,    h, -h, -h,    h, -h,  h,   -h, -h,  h,
        // +Z
        -h, -h,  h,    h, -h,  h,    h,  h,  h,   -h,  h,  h,
        // -Z
         h, -h, -h,   -h, -h, -h,   -h,  h, -h,    h,  h, -h,
    )

    val normals = FloatArray(24 * 3)
    val faceNormals = floatArrayOf(
         1f, 0f, 0f,   -1f,  0f,  0f,
         0f, 1f, 0f,    0f, -1f,  0f,
         0f, 0f, 1f,    0f,  0f, -1f,
    )
    for (face in 0 until 6) {
        for (v in 0 until 4) {
            val base = (face * 4 + v) * 3
            normals[base]     = faceNormals[face * 3]
            normals[base + 1] = faceNormals[face * 3 + 1]
            normals[base + 2] = faceNormals[face * 3 + 2]
        }
    }

    val uvs = FloatArray(24 * 2)
    val uvPattern = floatArrayOf(0f, 0f,  1f, 0f,  1f, 1f,  0f, 1f)
    for (face in 0 until 6) {
        for (v in 0 until 4) {
            uvs[(face * 4 + v) * 2]     = uvPattern[v * 2]
            uvs[(face * 4 + v) * 2 + 1] = uvPattern[v * 2 + 1]
        }
    }

    // Two triangles per face (0,1,2) (0,2,3) with CCW winding.
    val indices = IntArray(36)
    for (face in 0 until 6) {
        val base = face * 4
        val out = face * 6
        indices[out]     = base
        indices[out + 1] = base + 1
        indices[out + 2] = base + 2
        indices[out + 3] = base
        indices[out + 4] = base + 2
        indices[out + 5] = base + 3
    }

    return MeshData(
        positions   = positions,
        normals     = normals,
        uvs         = uvs,
        indices     = indices,
        boundingBox = Box(0f, 0f, 0f, h, h, h),
    )
}

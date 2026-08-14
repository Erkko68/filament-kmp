package io.github.erkko68.filament.compose.scene.primitives

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import io.github.erkko68.filament.Box
import io.github.erkko68.filament.compose.EntityScope
import io.github.erkko68.filament.MaterialInstance
import io.github.erkko68.filament.compose.FilamentSceneScope
import io.github.erkko68.filament.compose.scene.Position
import io.github.erkko68.filament.compose.scene.Rotation
import io.github.erkko68.filament.compose.scene.Scale

/**
 * Renders a custom triangle mesh from raw vertex data — the escape hatch for geometry the
 * built-in primitives ([Cube], [Sphere], [Plane], [Cylinder]) don't cover (procedural terrain,
 * generated meshes, imported non-glTF geometry).
 *
 * Vertices are non-interleaved: [positions] (xyz triples), [normals] (xyz triples), and [uvs]
 * (uv pairs) are parallel arrays addressed by [indices] (a flat list of triangle corner indices,
 * 3 per triangle). The tangent frame Filament's LIT shaders need is derived automatically from
 * the normals, UVs, and topology — supply correct normals and UVs.
 *
 * Geometry arrays are compared by **content**: re-creating identical arrays on recomposition is
 * safe (no GPU re-upload); changing any element re-uploads the buffers and rebuilds the
 * renderable. Still prefer `remember`-ing large arrays to skip the comparison cost.
 *
 * ```kotlin
 * Mesh(
 *     material  = myMaterial,
 *     positions = floatArrayOf(/* x,y,z, … */),
 *     normals   = floatArrayOf(/* x,y,z, … */),
 *     uvs       = floatArrayOf(/* u,v, … */),
 *     indices   = intArrayOf(0, 1, 2, /* … */),
 * )
 * ```
 *
 * @param material    Material applied to the whole mesh, or null while it is still loading.
 * @param positions   Vertex positions as xyz triples. Length must be a multiple of 3.
 * @param normals     Per-vertex normals as xyz triples. Must match [positions] in length.
 * @param uvs         Per-vertex UVs as uv pairs. Length must be `2 * vertexCount`.
 * @param indices     Triangle indices (32-bit). Length must be a multiple of 3.
 * @param position    World-space position of the [pivot] point.
 * @param rotation    World-space rotation. Build one with [Rotation.axisAngle]/[Rotation.euler].
 * @param scale       Per-axis scale.
 * @param pivot       Mesh-space point that rotation/scale revolve around and that ends up at
 *   [position]. Defaults to the mesh origin.
 * @param boundingBox AABB used for frustum culling. Defaults to one computed from [positions].
 *   Provide an explicit box if you skip culling or animate vertices beyond the static bounds.
 * @param visible   Whether this renderable is in the scene. False removes it from the
 *   scene (cheaply, keeping the entity alive) — a show/hide toggle without losing state.
 * @param castShadows    Whether the mesh casts shadows onto other renderables. On by default.
 * @param receiveShadows Whether the mesh receives shadows cast by others. On by default.
 * @param onCreate    Runs once when the mesh enters the scene, with the renderable entity and
 *   engine in scope ([EntityScope]) — use it to register the mesh with `view.pick` callbacks or
 *   other entity-keyed maps.
 */
@Composable
fun FilamentSceneScope.Mesh(
    material: MaterialInstance?,
    positions: FloatArray,
    normals: FloatArray,
    uvs: FloatArray,
    indices: IntArray,
    position: Position = Position(0f),
    rotation: Rotation = Rotation.Identity,
    scale: Scale = Scale(1f),
    pivot: Position = Position(0f),
    boundingBox: Box? = null,
    visible: Boolean = true,
    castShadows: Boolean = true,
    receiveShadows: Boolean = true,
    onCreate: EntityScope.() -> Unit = {},
) {
    require(positions.isNotEmpty() && positions.size % 3 == 0) {
        "positions must be a non-empty multiple of 3 (xyz triples), was ${positions.size}"
    }
    val vertexCount = positions.size / 3
    require(normals.size == positions.size) {
        "normals length (${normals.size}) must equal positions length (${positions.size})"
    }
    require(uvs.size == vertexCount * 2) {
        "uvs length (${uvs.size}) must be 2 * vertexCount (${vertexCount * 2})"
    }
    require(indices.isNotEmpty() && indices.size % 3 == 0) {
        "indices must be a non-empty multiple of 3 (triangles), was ${indices.size}"
    }

    val mesh = remember(positions, normals, uvs, indices, boundingBox) {
        MeshData(positions, normals, uvs, indices, boundingBox ?: positions.toBoundingBox())
    }
    Mesh(mesh, material, position, rotation, scale, pivot, visible, castShadows, receiveShadows, onCreate)
}

/** Axis-aligned bounding box (center + half-extent) enclosing every xyz triple in this array. */
private fun FloatArray.toBoundingBox(): Box {
    var minX = this[0]; var minY = this[1]; var minZ = this[2]
    var maxX = minX; var maxY = minY; var maxZ = minZ
    var i = 3
    while (i < size) {
        val x = this[i]; val y = this[i + 1]; val z = this[i + 2]
        if (x < minX) minX = x else if (x > maxX) maxX = x
        if (y < minY) minY = y else if (y > maxY) maxY = y
        if (z < minZ) minZ = z else if (z > maxZ) maxZ = z
        i += 3
    }
    return Box(
        (minX + maxX) * 0.5f, (minY + maxY) * 0.5f, (minZ + maxZ) * 0.5f,
        (maxX - minX) * 0.5f, (maxY - minY) * 0.5f, (maxZ - minZ) * 0.5f,
    )
}

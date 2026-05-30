package io.github.erkko68.filament.compose.scene.primitives

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import io.github.erkko68.filament.Box
import io.github.erkko68.filament.MaterialInstance
import io.github.erkko68.filament.compose.FilamentSceneScope
import io.github.erkko68.filament.compose.scene.Position
import io.github.erkko68.filament.compose.scene.Scale
import io.github.erkko68.filament.utils.Quaternion
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * A right circular cylinder aligned with the Y axis, centered on the origin (top at +height/2,
 * bottom at -height/2). Side wall + two end caps. [segments] controls the radial subdivision.
 *
 * The side wall and the caps use separate vertices (different normals), so this mesh has
 * `4 * (segments + 1)` vertices and `4 * segments` triangles.
 *
 * @param material  Material applied to side and caps.
 * @param position  World-space position of the [pivot] point.
 * @param rotation  World-space rotation.
 * @param scale     Per-axis scale.
 * @param pivot     Point in mesh space that rotation/scale revolve around. Defaults to the cylinder centre.
 * @param radius    Cylinder radius in mesh space.
 * @param height    Full height along Y in mesh space.
 * @param segments  Number of radial subdivisions. Minimum 3.
 * @param onCreate  Receives the renderable entity ID once the cylinder is added to the scene.
 */
@Composable
fun FilamentSceneScope.Cylinder(
    material: MaterialInstance,
    position: Position = Position(0f),
    rotation: Quaternion = Quaternion(),
    scale: Scale = Scale(1f),
    pivot: Position = Position(0f),
    radius: Float = 0.5f,
    height: Float = 1f,
    segments: Int = 32,
    onCreate: (entity: Int) -> Unit = {},
) {
    val mesh = remember(radius, height, segments) { cylinderMesh(radius, height, segments) }
    Mesh(mesh, material, position, rotation, scale, pivot, onCreate)
}

private fun cylinderMesh(radius: Float, height: Float, segments: Int): MeshData {
    require(segments >= 3) { "segments must be >= 3 (got $segments)" }

    val h = height * 0.5f
    val ringSize  = segments + 1
    // Side: 2 rings of (segments+1) vertices.
    // Caps: top and bottom each have (segments+1) ring vertices + 1 center = (segments+2).
    val sideVertexCount = ringSize * 2
    val capVertexCount  = (ringSize + 1) * 2
    val totalVertices   = sideVertexCount + capVertexCount

    val positions = FloatArray(totalVertices * 3)
    val normals   = FloatArray(totalVertices * 3)
    val uvs       = FloatArray(totalVertices * 2)

    var p = 0; var n = 0; var u = 0

    // --- Side wall: bottom ring then top ring, one vertex per segment + 1 wrap-around.
    for (ring in 0..1) {
        val y = if (ring == 0) -h else h
        val v = ring.toFloat()
        for (s in 0..segments) {
            val theta = (s.toFloat() / segments) * 2f * PI.toFloat()
            val cx = cos(theta); val cz = sin(theta)
            positions[p++] = cx * radius; positions[p++] = y; positions[p++] = cz * radius
            normals[n++]   = cx;           normals[n++]   = 0f; normals[n++]   = cz
            uvs[u++] = s.toFloat() / segments; uvs[u++] = v
        }
    }

    // --- Top cap: ring vertices then center.
    val topRingStart = sideVertexCount
    for (s in 0..segments) {
        val theta = (s.toFloat() / segments) * 2f * PI.toFloat()
        val cx = cos(theta); val cz = sin(theta)
        positions[p++] = cx * radius; positions[p++] = h; positions[p++] = cz * radius
        normals[n++]   = 0f;           normals[n++]   = 1f; normals[n++]   = 0f
        uvs[u++] = 0.5f + 0.5f * cx; uvs[u++] = 0.5f + 0.5f * cz
    }
    val topCenter = topRingStart + ringSize
    positions[p++] = 0f; positions[p++] = h; positions[p++] = 0f
    normals[n++]   = 0f; normals[n++]   = 1f; normals[n++]   = 0f
    uvs[u++] = 0.5f; uvs[u++] = 0.5f

    // --- Bottom cap: ring vertices then center.
    val bottomRingStart = topCenter + 1
    for (s in 0..segments) {
        val theta = (s.toFloat() / segments) * 2f * PI.toFloat()
        val cx = cos(theta); val cz = sin(theta)
        positions[p++] = cx * radius; positions[p++] = -h; positions[p++] = cz * radius
        normals[n++]   = 0f;           normals[n++]   = -1f; normals[n++]   = 0f
        uvs[u++] = 0.5f + 0.5f * cx; uvs[u++] = 0.5f - 0.5f * cz
    }
    val bottomCenter = bottomRingStart + ringSize
    positions[p++] = 0f; positions[p++] = -h; positions[p++] = 0f
    normals[n++]   = 0f; normals[n++]   = -1f; normals[n++]   = 0f
    uvs[u++] = 0.5f; uvs[u++] = 0.5f

    // Side: 2 triangles per quad, CCW from outside.
    // Top cap: triangles fan around topCenter, CCW seen from +Y.
    // Bottom cap: triangles fan around bottomCenter, CCW seen from -Y (reversed winding).
    val sideTriangles   = segments * 2
    val capTriangles    = segments * 2
    val indices = IntArray((sideTriangles + capTriangles) * 3)
    var i = 0
    for (s in 0 until segments) {
        val a = s
        val b = s + 1
        val c = s + ringSize
        val d = s + ringSize + 1
        indices[i++] = a; indices[i++] = c; indices[i++] = b
        indices[i++] = b; indices[i++] = c; indices[i++] = d
    }
    // Ring vertices trace a clockwise path when viewed from +Y (positions are x=cosθ, z=sinθ
    // with θ increasing). For CCW (front-facing) winding the top cap goes (center, s+1, s),
    // and the bottom cap (viewed from -Y, which inverts the projection) goes (center, s, s+1).
    for (s in 0 until segments) {
        indices[i++] = topCenter
        indices[i++] = topRingStart + s + 1
        indices[i++] = topRingStart + s
    }
    for (s in 0 until segments) {
        indices[i++] = bottomCenter
        indices[i++] = bottomRingStart + s
        indices[i++] = bottomRingStart + s + 1
    }

    return MeshData(
        positions   = positions,
        normals     = normals,
        uvs         = uvs,
        indices     = indices,
        boundingBox = Box(0f, 0f, 0f, radius, h, radius),
    )
}

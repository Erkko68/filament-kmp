package io.github.erkko68.filament.compose.scene.primitives

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import io.github.erkko68.filament.Box
import io.github.erkko68.filament.MaterialInstance
import io.github.erkko68.filament.compose.scene.Position
import io.github.erkko68.filament.compose.scene.Scale
import io.github.erkko68.filament.utils.Quaternion
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * A UV sphere centered on the origin. [rings] is the latitudinal count (from pole to pole),
 * [segments] is the longitudinal count. Higher counts → smoother sphere, more triangles
 * (`rings * segments * 2` triangles total).
 *
 * @param material  The material applied to the whole sphere.
 * @param position  World-space position of the [pivot] point.
 * @param rotation  World-space rotation.
 * @param scale     Per-axis scale.
 * @param pivot     Point in mesh space that rotation/scale revolve around. Defaults to the sphere centre.
 * @param radius    Sphere radius in mesh space.
 * @param rings     Latitude subdivisions. Minimum 2.
 * @param segments  Longitude subdivisions. Minimum 3.
 * @param onCreate  Receives the renderable entity ID once the sphere is added to the scene.
 */
@Composable
fun Sphere(
    material: MaterialInstance,
    position: Position = Position(0f),
    rotation: Quaternion = Quaternion(),
    scale: Scale = Scale(1f),
    pivot: Position = Position(0f),
    radius: Float = 0.5f,
    rings: Int = 16,
    segments: Int = 32,
    onCreate: (entity: Int) -> Unit = {},
) {
    val mesh = remember(radius, rings, segments) { sphereMesh(radius, rings, segments) }
    Mesh(mesh, material, position, rotation, scale, pivot, onCreate)
}

private fun sphereMesh(radius: Float, rings: Int, segments: Int): MeshData {
    require(rings >= 2)    { "rings must be >= 2 (got $rings)" }
    require(segments >= 3) { "segments must be >= 3 (got $segments)" }

    val vertexCount = (rings + 1) * (segments + 1)
    val positions = FloatArray(vertexCount * 3)
    val normals   = FloatArray(vertexCount * 3)
    val uvs       = FloatArray(vertexCount * 2)

    var p = 0
    var n = 0
    var u = 0
    for (r in 0..rings) {
        val v = r.toFloat() / rings
        val phi = v * PI.toFloat()           // 0..π   (north pole to south pole)
        val sinPhi = sin(phi); val cosPhi = cos(phi)
        for (s in 0..segments) {
            val uu = s.toFloat() / segments
            val theta = uu * 2f * PI.toFloat() // 0..2π
            val sinTheta = sin(theta); val cosTheta = cos(theta)

            val nx = sinPhi * cosTheta
            val ny = cosPhi
            val nz = sinPhi * sinTheta

            positions[p++] = nx * radius
            positions[p++] = ny * radius
            positions[p++] = nz * radius

            normals[n++] = nx
            normals[n++] = ny
            normals[n++] = nz

            uvs[u++] = uu
            uvs[u++] = 1f - v
        }
    }

    // Indices: each quad (r,s)→(r+1,s+1) split into two CCW triangles when viewed from outside.
    // Vertex layout: a=(r,s), b=(r,s+1), c=(r+1,s), d=(r+1,s+1). The CCW order from outside is
    // a→b→c (and b→d→c) — checked via cross-product at the equator: (b−a)×(c−a) points outward.
    val indices = IntArray(rings * segments * 6)
    var i = 0
    val stride = segments + 1
    for (r in 0 until rings) {
        for (s in 0 until segments) {
            val a = r * stride + s
            val b = a + 1
            val c = a + stride
            val d = c + 1
            indices[i++] = a; indices[i++] = b; indices[i++] = c
            indices[i++] = b; indices[i++] = d; indices[i++] = c
        }
    }

    return MeshData(
        positions   = positions,
        normals     = normals,
        uvs         = uvs,
        indices     = indices,
        boundingBox = Box(0f, 0f, 0f, radius, radius, radius),
    )
}

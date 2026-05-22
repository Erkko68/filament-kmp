package io.github.erkko68.filament.compose.scene.primitives

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import io.github.erkko68.filament.Box
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.IndexBuffer
import io.github.erkko68.filament.MaterialInstance
import io.github.erkko68.filament.RenderableManager
import io.github.erkko68.filament.SurfaceOrientation
import io.github.erkko68.filament.VertexBuffer
import io.github.erkko68.filament.VertexBuffer.AttributeType
import io.github.erkko68.filament.VertexBuffer.VertexAttribute
import io.github.erkko68.filament.compose.LocalFilamentEngine
import io.github.erkko68.filament.compose.LocalFilamentScene
import io.github.erkko68.filament.compose.internal.transformMatrix
import io.github.erkko68.filament.compose.scene.Position
import io.github.erkko68.filament.compose.scene.Scale
import io.github.erkko68.filament.toBytes
import io.github.erkko68.filament.utils.Quaternion

/**
 * CPU-side geometry buffers. [indices] uses unsigned 32-bit indices. [boundingBox] is used by
 * Filament for frustum culling — provide an accurate AABB or culling will be wrong.
 *
 * Vertices are non-interleaved: positions/normals/uvs each live in their own buffer.
 */
internal data class MeshData(
    val positions: FloatArray,
    val normals: FloatArray,
    val uvs: FloatArray,
    val indices: IntArray,
    val boundingBox: Box,
)

internal class MeshHandles(val vertexBuffer: VertexBuffer, val indexBuffer: IndexBuffer)

private fun MeshData.upload(engine: Engine): MeshHandles {
    val vertexCount = positions.size / 3
    val triangleCount = indices.size / 3

    // Compute the packed tangent-frame quaternion (TANGENTS attribute) from the per-vertex
    // normal + UV + triangle topology. Filament's LIT shaders expect this exact encoding.
    val tangents = FloatArray(vertexCount * 4)
    val orientation = SurfaceOrientation.Builder()
        .vertexCount(vertexCount)
        .positions(positions)
        .normals(normals)
        .uvs(uvs)
        .triangleCount(triangleCount)
        .triangles32(indices)
        .build()
    orientation.getQuatsAsFloat(tangents, vertexCount)
    orientation.destroy()

    val vb = VertexBuffer.Builder()
        .vertexCount(vertexCount)
        .bufferCount(3)
        .attribute(VertexAttribute.POSITION, 0, AttributeType.FLOAT3)
        .attribute(VertexAttribute.TANGENTS, 1, AttributeType.FLOAT4)
        .attribute(VertexAttribute.UV0, 2, AttributeType.FLOAT2)
        .build(engine)
    vb.setBufferAt(engine, 0, positions.toBytes())
    vb.setBufferAt(engine, 1, tangents.toBytes())
    vb.setBufferAt(engine, 2, uvs.toBytes())

    val ib = IndexBuffer.Builder()
        .indexCount(indices.size)
        .bufferType(IndexBuffer.Builder.IndexType.UINT)
        .build(engine)
    ib.setBuffer(engine, indices.toBytes())

    return MeshHandles(vb, ib)
}

/**
 * Builds and manages a single-primitive renderable entity from a [MeshData] and a
 * [MaterialInstance]. Recreates the entity when [mesh] or [material] changes; updates the
 * transform in place when only [position]/[rotation]/[scale] change.
 *
 * Internal — all public primitive composables (Cube, Sphere, Plane, Cylinder) call this.
 */
@Composable
internal fun Mesh(
    mesh: MeshData,
    material: MaterialInstance,
    position: Position,
    rotation: Quaternion,
    scale: Scale,
) {
    val engine = LocalFilamentEngine.current
    val scene  = LocalFilamentScene.current

    val handles = remember(mesh) { mesh.upload(engine) }
    DisposableEffect(handles) {
        onDispose {
            engine.destroyVertexBuffer(handles.vertexBuffer)
            engine.destroyIndexBuffer(handles.indexBuffer)
        }
    }

    val entity = remember(handles, material) {
        engine.getEntityManager().create().also { e ->
            RenderableManager.Builder(1)
                .geometry(0, RenderableManager.PrimitiveType.TRIANGLES, handles.vertexBuffer, handles.indexBuffer)
                .material(0, material)
                .boundingBox(mesh.boundingBox)
                .build(engine, e)
        }
    }

    DisposableEffect(entity) {
        scene.addEntity(entity)
        onDispose {
            scene.removeEntity(entity)
            engine.getRenderableManager().destroy(entity)
            engine.getEntityManager().destroy(entity)
        }
    }

    DisposableEffect(entity, position, rotation, scale) {
        val tm = engine.getTransformManager()
        tm.setTransform(tm.getInstance(entity), transformMatrix(position, rotation, scale))
        onDispose { }
    }
}

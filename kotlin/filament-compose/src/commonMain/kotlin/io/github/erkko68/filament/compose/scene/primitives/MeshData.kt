package io.github.erkko68.filament.compose.scene.primitives

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import io.github.erkko68.filament.Box
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Entity
import io.github.erkko68.filament.IndexBuffer
import io.github.erkko68.filament.MaterialInstance
import io.github.erkko68.filament.RenderableManager
import io.github.erkko68.filament.SurfaceOrientation
import io.github.erkko68.filament.VertexBuffer
import io.github.erkko68.filament.VertexBuffer.AttributeType
import io.github.erkko68.filament.VertexBuffer.VertexAttribute
import io.github.erkko68.filament.compose.EntityScope
import io.github.erkko68.filament.compose.EntityScopeImpl
import io.github.erkko68.filament.compose.LocalFilamentEngine
import io.github.erkko68.filament.compose.noFilamentEngine
import io.github.erkko68.filament.compose.LocalFilamentScene
import io.github.erkko68.filament.compose.noFilamentScene
import io.github.erkko68.filament.compose.internal.transformMatrix
import io.github.erkko68.filament.compose.scene.LocalGroupVisible
import io.github.erkko68.filament.compose.scene.LocalParentEntity
import io.github.erkko68.filament.compose.scene.Position
import io.github.erkko68.filament.compose.scene.Rotation
import io.github.erkko68.filament.compose.scene.Scale
import io.github.erkko68.filament.toBytes

/**
 * CPU-side geometry buffers. [indices] uses unsigned 32-bit indices. [boundingBox] is used by
 * Filament for frustum culling — provide an accurate AABB or culling will be wrong.
 *
 * Vertices are non-interleaved: positions/normals/uvs each live in their own buffer. Equality is
 * by **content** (a data class would compare the arrays by reference), so a caller re-creating
 * identical arrays each recomposition doesn't re-upload the GPU buffers.
 */
@Immutable
internal class MeshData(
    val positions: FloatArray,
    val normals: FloatArray,
    val uvs: FloatArray,
    val indices: IntArray,
    val boundingBox: Box,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MeshData) return false
        return positions.contentEquals(other.positions) &&
            normals.contentEquals(other.normals) &&
            uvs.contentEquals(other.uvs) &&
            indices.contentEquals(other.indices) &&
            boundingBox.center.contentEquals(other.boundingBox.center) &&
            boundingBox.halfExtent.contentEquals(other.boundingBox.halfExtent)
    }

    override fun hashCode(): Int {
        var result = positions.contentHashCode()
        result = 31 * result + indices.contentHashCode()
        return result
    }
}

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
 * transform in place when only [position]/[rotation]/[scale]/[pivot] change.
 *
 * A null [material] renders nothing — it means the material is still loading (the
 * [rememberMaterial]/[rememberMaterialInstance] chain returns null until ready).
 *
 * [onCreate] fires once when the renderable entity is added to the scene — pass the entity
 * to e.g. an `entityToIndex` map so `view.pick` callbacks can identify the primitive.
 *
 * Internal — all public primitive composables (Cube, Sphere, Plane, Cylinder) call this.
 */
@Composable
internal fun Mesh(
    mesh: MeshData,
    material: MaterialInstance?,
    position: Position,
    rotation: Rotation,
    scale: Scale,
    pivot: Position,
    visible: Boolean,
    castShadows: Boolean,
    receiveShadows: Boolean,
    onCreate: EntityScope.() -> Unit,
) {
    if (material == null) return

    val engine = LocalFilamentEngine.current ?: noFilamentEngine()
    val scene  = LocalFilamentScene.current ?: noFilamentScene()
    val parent = LocalParentEntity.current
    // A hidden enclosing Group hides its whole subtree.
    val effectiveVisible = visible && LocalGroupVisible.current

    val handles = remember(mesh) { mesh.upload(engine) }
    DisposableEffect(handles) {
        onDispose {
            engine.destroyVertexBuffer(handles.vertexBuffer)
            engine.destroyIndexBuffer(handles.indexBuffer)
        }
    }

    val entity = remember(handles, material, castShadows, receiveShadows) {
        engine.getEntityManager().create().also { e ->
            RenderableManager.Builder(1)
                .geometry(0, RenderableManager.PrimitiveType.TRIANGLES, handles.vertexBuffer, handles.indexBuffer)
                .material(0, material)
                .boundingBox(mesh.boundingBox)
                .castShadows(castShadows)
                .receiveShadows(receiveShadows)
                .build(engine, e)
        }
    }

    // Registered before the membership effect so it disposes *after* it — the entity is
    // removed from the scene before its components are destroyed.
    DisposableEffect(entity) {
        EntityScopeImpl(entity, engine).onCreate()
        onDispose {
            engine.getRenderableManager().destroy(entity)
            engine.getEntityManager().destroy(entity)
        }
    }

    // Scene membership tracks `visible` — hiding removes the entity from the scene without
    // destroying it, so toggling visibility is cheap and keeps entity identity stable.
    DisposableEffect(entity, effectiveVisible) {
        if (effectiveVisible) scene.addEntity(entity)
        onDispose { if (effectiveVisible) scene.removeEntity(entity) }
    }

    DisposableEffect(entity, position, rotation, scale, pivot) {
        val tm = engine.getTransformManager()
        // Ensure the renderable entity has a transform component before we touch it. The
        // RenderableManager.Builder.build() above doesn't add one; parenting and setTransform
        // both need it.
        if (!tm.hasComponent(entity)) tm.create(entity)
        tm.setTransform(tm.getInstance(entity), transformMatrix(position, rotation, scale, pivot))
        onDispose { }
    }

    // Reparent to the surrounding Group, if any. Re-runs when the parent identity changes
    // (e.g. the user moves this composable into/out of a Group at runtime).
    DisposableEffect(entity, parent) {
        if (parent != null) {
            val tm = engine.getTransformManager()
            if (!tm.hasComponent(entity)) tm.create(entity)
            tm.setParent(tm.getInstance(entity), tm.getInstance(parent))
        }
        onDispose { }
    }
}

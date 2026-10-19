package io.github.erkko68.filament.filamat

import io.github.erkko68.filament.Box
import io.github.erkko68.filament.IndexBuffer
import io.github.erkko68.filament.MaterialInstance
import io.github.erkko68.filament.RenderableManager
import io.github.erkko68.filament.VertexBuffer
import io.github.erkko68.filament.Viewport
import io.github.erkko68.filament.filamat.testutils.FilamatTestFixture
import io.github.erkko68.filament.toBytes
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

/** The runtime-material loop: compile, load, set a parameter, draw it through frames, tear down. */
class CompiledMaterialFrameTest : FilamatTestFixture() {
    @Test
    fun aCompiledMaterialDrawsWithItsParameter() {
        val material = MaterialBuilder()
            .shading(MaterialBuilder.Shading.UNLIT)
            .parameter("tint", MaterialBuilder.UniformType.FLOAT3)
            .material("void material(inout MaterialInputs m) { prepareMaterial(m); m.baseColor = vec4(materialParams.tint, 1.0); }")
            .load()
        val instance = material.createInstance().apply { setParameter("tint", 1f, 0.5f, 0.25f) }
        val tint = instance.getParameter("tint", MaterialInstance.FloatElement.FLOAT3)

        val vb = VertexBuffer.Builder().vertexCount(3).bufferCount(1)
            .attribute(VertexBuffer.VertexAttribute.POSITION, 0, VertexBuffer.AttributeType.FLOAT3, 0, 12)
            .build(engine)
        vb.setBufferAt(engine, 0, floatArrayOf(0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f, 0f).toBytes())
        val ib = IndexBuffer.Builder().indexCount(3).bufferType(IndexBuffer.IndexType.USHORT).build(engine)
        ib.setBuffer(engine, shortArrayOf(0, 1, 2).toBytes())
        val triangle = engine.entityManager.create()
        RenderableManager.Builder(1)
            .geometry(0, RenderableManager.PrimitiveType.TRIANGLES, vb, ib)
            .material(0, instance)
            .boundingBox(Box(floatArrayOf(0.5f, 0.5f, 0f), floatArrayOf(0.5f, 0.5f, 0.01f)))
            .build(engine, triangle)

        val scene = engine.createScene().apply { addEntity(triangle) }
        val cameraEntity = engine.entityManager.create()
        val camera = engine.createCamera(cameraEntity).apply {
            lookAt(0.5, 0.5, 3.0, 0.5, 0.5, 0.0)
            setProjection(45.0, 1.0, 0.1, 10.0)
        }
        val view = engine.createView().also {
            it.scene = scene
            it.camera = camera
            it.viewport = Viewport(0, 0, 32, 32)
        }
        val renderer = engine.createRenderer()
        val swapChain = engine.createSwapChain(32, 32)
        val visible = mutableListOf<Int>()
        repeat(4) {
            if (renderer.beginFrame(swapChain, 0L)) {
                renderer.render(view)
                renderer.endFrame()
                visible += view.visibleRenderableCount
            }
        }

        engine.destroy(renderer)
        engine.destroy(swapChain)
        engine.destroy(view)
        engine.destroyCameraComponent(cameraEntity)
        engine.destroy(scene)
        engine.destroy(triangle)
        listOf(cameraEntity, triangle).forEach(engine.entityManager::destroy)
        engine.destroy(vb)
        engine.destroy(ib)
        engine.destroy(instance)

        assertContentEquals(floatArrayOf(1f, 0.5f, 0.25f), tint)
        assertEquals(1, visible.lastOrNull(), "frames rendered: ${visible.size}")
    }
}

package io.github.erkko68.filament

import kotlin.test.Test
import kotlin.test.assertNotNull
import io.github.erkko68.filament.toBytes

/**
 * A common test that sets up a basic Filament scene with a rotating cube.
 * This test uses a headless swapchain to verify the resource creation and 
 * rendering pipeline without requiring a physical display surface.
 */
class RotatingCubeTest {

    @Test
    fun testSetupRotatingCube() {
        val engine = Engine.create()
        assertNotNull(engine, "Engine should be created")

        val scene = engine.createScene()
        val camera = engine.createCamera()
        val view = engine.createView()
        val renderer = engine.createRenderer()

        // 1. Setup geometry (Unit Cube)
        val x = 0.5f
        val y = 0.5f
        val z = 0.5f

        val positions = floatArrayOf(
            -x, -y,  z,  x, -y,  z,  x,  y,  z, -x,  y,  z, // Front
            -x, -y, -z, -x,  y, -z,  x,  y, -z,  x, -y, -z, // Back
            -x, -y, -z, -x, -y,  z, -x,  y,  z, -x,  y, -z, // Left
             x, -y, -z,  x,  y, -z,  x,  y,  z,  x, -y,  z, // Right
            -x,  y, -z, -x,  y,  z,  x,  y,  z,  x,  y, -z, // Top
            -x, -y, -z,  x, -y, -z,  x, -y,  z, -x, -y,  z  // Bottom
        )

        val normals = floatArrayOf(
            0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f,
            0f, 0f, -1f, 0f, 0f, -1f, 0f, 0f, -1f, 0f, 0f, -1f,
            -1f, 0f, 0f, -1f, 0f, 0f, -1f, 0f, 0f, -1f, 0f, 0f,
            1f, 0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f,
            0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f, 0f,
            0f, -1f, 0f, 0f, -1f, 0f, 0f, -1f, 0f, 0f, -1f, 0f
        )

        val indices = shortArrayOf(
            0, 1, 2, 2, 3, 0,
            4, 5, 6, 6, 7, 4,
            8, 9, 10, 10, 11, 8,
            12, 13, 14, 14, 15, 12,
            16, 17, 18, 18, 19, 16,
            20, 21, 22, 22, 23, 20
        )

        val vb = VertexBuffer.Builder()
            .vertexCount(24)
            .bufferCount(2)
            .attribute(VertexBuffer.VertexAttribute.POSITION, 0, VertexBuffer.AttributeType.FLOAT3, 0, 12)
            .attribute(VertexBuffer.VertexAttribute.NORMAL, 1, VertexBuffer.AttributeType.FLOAT3, 0, 12)
            .build(engine)

        vb.setBufferAt(engine, 0, positions.toBytes())
        vb.setBufferAt(engine, 1, normals.toBytes())

        val ib = IndexBuffer.Builder()
            .indexCount(36)
            .bufferType(IndexBuffer.Builder.IndexType.USHORT)
            .build(engine)
        ib.setBuffer(engine, indices.toBytes())

        // 2. Setup Renderable
        val entity = EntityManager.get().create()
        RenderableManager.Builder(1)
            .boundingBox(Box(0f, 0f, 0f, x, y, z))
            .geometry(0, RenderableManager.PrimitiveType.TRIANGLES, vb, ib)
            .build(engine, entity)

        scene.addEntity(entity)

        // 3. Setup Light
        val light = EntityManager.get().create()
        LightManager.Builder(LightManager.Type.DIRECTIONAL)
            .color(1f, 1f, 1f)
            .intensity(100000f)
            .direction(0f, -1f, -1f)
            .castShadows(true)
            .build(engine, light)
        scene.addEntity(light)

        // 4. Setup View
        view.setScene(scene)
        view.setCamera(camera)
        view.setViewport(Viewport(0, 0, 1024, 1024))

        // 5. Headless Rendering
        val swapChain = engine.createSwapChain(1024, 1024, 0L)
        
        // Simulating the render loop (one frame)
        if (renderer.beginFrame(swapChain, 0L)) {
            renderer.render(view)
            renderer.endFrame()
        }

        // 6. Cleanup
        engine.destroyEntity(entity)
        engine.destroyEntity(light)
        engine.destroyRenderer(renderer)
        engine.destroyView(view)
        engine.destroyCamera(camera)
        engine.destroyScene(scene)
        engine.destroyIndexBuffer(ib)
        engine.destroyVertexBuffer(vb)
        engine.destroySwapChain(swapChain)
        engine.destroy()
    }
}

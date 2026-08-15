package io.github.erkko68.filament.testutils

import io.github.erkko68.filament.Box
import io.github.erkko68.filament.Camera
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Entity
import io.github.erkko68.filament.EntityManager
import io.github.erkko68.filament.IndexBuffer
import io.github.erkko68.filament.Material
import io.github.erkko68.filament.MaterialInstance
import io.github.erkko68.filament.RenderableManager
import io.github.erkko68.filament.Renderer
import io.github.erkko68.filament.Texture
import io.github.erkko68.filament.VertexBuffer
import io.github.erkko68.filament.Viewport
import io.github.erkko68.filament.toBytes

/**
 * Renders a scene into a readable headless swapchain and reads the pixels back,
 * for Tier C semantic frame assertions: property-based checks on *relations
 * between image regions* (shadow darker than open floor, centre not black, …)
 * that hold on every rasterizer — never golden-image comparisons.
 *
 * Owns the render plumbing (swapchain/renderer/view/camera/scene) and every
 * resource created through it; [destroy] tears it all down in dependency order
 * so the fixture's `engine.destroy()` doesn't panic on live resources.
 */
class FrameProbe(private val engine: Engine, val width: Int = 64, val height: Int = 64) {
    val scene = engine.createScene()
    val camera = engine.createCamera().apply {
        setProjection(45.0, width.toDouble() / height, 0.1, 100.0, Camera.Fov.VERTICAL)
    }
    val view = engine.createView().apply {
        this.scene = this@FrameProbe.scene
        this.camera = this@FrameProbe.camera
        this.viewport = Viewport(0, 0, width, height)
    }
    private val swapChain = engine.createSwapChain(width, height, SWAP_CHAIN_CONFIG_READABLE)
    private val renderer = engine.createRenderer().apply {
        clearOptions = Renderer.ClearOptions().apply {
            clearColor = doubleArrayOf(0.0, 0.0, 0.0, 1.0)
            clear = true
        }
    }

    private val materials = mutableListOf<Material>()
    private val instances = mutableListOf<MaterialInstance>()
    private val vertexBuffers = mutableListOf<VertexBuffer>()
    private val indexBuffers = mutableListOf<IndexBuffer>()
    private val entities = mutableListOf<Entity>()

    /** Builds a material from [payload] and tracks it for [destroy]. */
    fun material(payload: ByteArray): Material =
        Material.Builder().payload(payload).build(engine).also { materials += it }

    /** Creates a tracked instance of a tracked material. */
    fun instance(material: Material): MaterialInstance =
        material.createInstance().also { instances += it }

    /** Tracks an entity created outside the probe (e.g. a light) for [destroy]. */
    fun track(entity: Entity) {
        entities += entity
    }

    /**
     * Renders [frames] frames (a few, so program compilation and shadow-map passes
     * settle) and reads the last one back as RGBA8. Row order is backend-dependent:
     * Metal delivers rows top-down, OpenGL bottom-up (GL convention) — pinned by
     * FrameSemanticsTest.readPixelsRowOrderMatchesBackendConvention.
     * Returns null only if the backend never delivered the async readback.
     */
    fun renderAndRead(frames: Int = 3): ByteArray? {
        repeat(frames - 1) {
            if (renderer.beginFrame(swapChain, 0L)) {
                renderer.render(view)
                renderer.endFrame()
            }
            engine.flushAndWait()
        }
        val pixels = ByteArray(width * height * 4)
        val readbackDone = ReadbackFlag()
        val pbd = Texture.PixelBufferDescriptor(pixels, pixels.size, Texture.Format.RGBA, Texture.Type.UBYTE) {
            readbackDone.done = true
        }
        // A skipped beginFrame never issues the readPixels, so retry it — otherwise a
        // frame the backend declined comes back as "the readback never landed".
        var began = false
        var frameTries = 0
        while (!began && frameTries++ < 10) {
            began = renderer.beginFrame(swapChain, 0L)
            if (began) {
                renderer.render(view)
                renderer.readPixels(0, 0, width, height, pbd)
                renderer.endFrame()
            }
            engine.flushAndWait()
        }
        var tries = 0
        while (!readbackDone.done && tries++ < 20) engine.flushAndWait()
        return if (readbackDone.done) pixels else null
    }

    /**
     * Adds a horizontal (+Y facing) quad centred at ([cx], [cy], [cz]) with the
     * given [halfExtent], casting and receiving shadows. Returns the entity.
     */
    fun addHorizontalQuad(
        material: MaterialInstance,
        cx: Float, cy: Float, cz: Float,
        halfExtent: Float,
    ): Entity {
        // TANGENTS frame with the normal on +Y: rotate +Z to +Y about X by -90°.
        val quat = floatArrayOf(-0.70710678f, 0f, 0f, 0.70710678f)
        val positions = floatArrayOf(
            cx - halfExtent, cy, cz - halfExtent,
            cx + halfExtent, cy, cz - halfExtent,
            cx + halfExtent, cy, cz + halfExtent,
            cx - halfExtent, cy, cz + halfExtent,
        )
        val tangents = FloatArray(4 * 4) { quat[it % 4] }

        val vb = VertexBuffer.Builder()
            .vertexCount(4)
            .bufferCount(2)
            .attribute(VertexBuffer.VertexAttribute.POSITION, 0, VertexBuffer.AttributeType.FLOAT3, 0, 12)
            .attribute(VertexBuffer.VertexAttribute.TANGENTS, 1, VertexBuffer.AttributeType.FLOAT4, 0, 16)
            .build(engine)
        vb.setBufferAt(engine, 0, positions.toBytes())
        vb.setBufferAt(engine, 1, tangents.toBytes())
        vertexBuffers += vb

        val ib = IndexBuffer.Builder()
            .indexCount(6)
            .bufferType(IndexBuffer.Builder.IndexType.USHORT)
            .build(engine)
        // Two CCW triangles as seen from +Y.
        ib.setBuffer(engine, shortArrayOf(0, 2, 1, 0, 3, 2).toBytes())
        indexBuffers += ib

        val entity = EntityManager.get().create()
        RenderableManager.Builder(1)
            .geometry(0, RenderableManager.PrimitiveType.TRIANGLES, vb, ib)
            .material(0, material)
            .boundingBox(Box(cx, cy, cz, halfExtent, 0.01f, halfExtent))
            .castShadows(true)
            .receiveShadows(true)
            .build(engine, entity)
        scene.addEntity(entity)
        entities += entity
        return entity
    }

    fun destroy() {
        val em = EntityManager.get()
        entities.forEach {
            scene.removeEntity(it)
            engine.destroyEntity(it)
            em.destroy(it)
        }
        vertexBuffers.forEach { engine.destroyVertexBuffer(it) }
        indexBuffers.forEach { engine.destroyIndexBuffer(it) }
        instances.forEach { engine.destroyMaterialInstance(it) }
        materials.forEach { engine.destroyMaterial(it) }
        engine.destroyView(view)
        engine.destroyCamera(camera)
        engine.destroyScene(scene)
        engine.destroyRenderer(renderer)
        engine.destroySwapChain(swapChain)
    }

    companion object {
        private const val SWAP_CHAIN_CONFIG_READABLE = 0x2L
    }
}

/** Cross-thread completion flag: readback callbacks may fire on the backend thread. */
class ReadbackFlag {
    @kotlin.concurrent.Volatile
    var done = false
}

/** Mean channel/luminance statistics over a pixel region of an RGBA8 readback. */
class RegionStats(val meanR: Double, val meanG: Double, val meanB: Double) {
    /** Rec. 709 luma of the channel means, 0..255. */
    val meanLuma: Double get() = 0.2126 * meanR + 0.7152 * meanG + 0.0722 * meanB
    override fun toString() = "RegionStats(r=$meanR, g=$meanG, b=$meanB, luma=$meanLuma)"
}

/**
 * Statistics over the axis-aligned region given in *fractions* of the image
 * (e.g. x 0.4..0.6, y 0.4..0.6 is the centre patch). RGBA8; y fractions are measured
 * from the buffer's first row — whether that is the screen top or bottom depends on
 * the backend (see renderAndRead), which centred regions are insensitive to.
 */
fun ByteArray.regionStats(
    imageWidth: Int,
    imageHeight: Int,
    xFrom: Double, xTo: Double,
    yFrom: Double, yTo: Double,
): RegionStats {
    val x0 = (imageWidth * xFrom).toInt().coerceIn(0, imageWidth - 1)
    val x1 = (imageWidth * xTo).toInt().coerceIn(x0 + 1, imageWidth)
    val y0 = (imageHeight * yFrom).toInt().coerceIn(0, imageHeight - 1)
    val y1 = (imageHeight * yTo).toInt().coerceIn(y0 + 1, imageHeight)
    var r = 0.0; var g = 0.0; var b = 0.0; var n = 0
    for (y in y0 until y1) {
        for (x in x0 until x1) {
            val i = (y * imageWidth + x) * 4
            r += this[i].toInt() and 0xFF
            g += this[i + 1].toInt() and 0xFF
            b += this[i + 2].toInt() and 0xFF
            n++
        }
    }
    return RegionStats(r / n, g / n, b / n)
}

/** Mean absolute per-byte difference between two same-size RGBA readbacks, 0..255. */
fun meanAbsoluteDifference(a: ByteArray, b: ByteArray): Double {
    require(a.size == b.size)
    var sum = 0.0
    for (i in a.indices) {
        sum += kotlin.math.abs((a[i].toInt() and 0xFF) - (b[i].toInt() and 0xFF))
    }
    return sum / a.size
}

package eric.bitria.samples

import dev.filament.kmp.*
import dev.filament.kmp.filamat.*

class FilamentRenderer : FilamentViewRenderer {
    var engine: Engine? = null
        private set
    var renderer: Renderer? = null
        private set
    var scene: Scene? = null
        private set
    var view: View? = null
        private set
    var camera: Camera? = null
        private set
    var swapChain: SwapChain? = null
        private set
    var skybox: Skybox? = null
        private set

    private var cubeEntity: Entity? = null
    private var vertexBuffer: VertexBuffer? = null
    private var indexBuffer: IndexBuffer? = null
    private var material: Material? = null
    private var materialInstance: MaterialInstance? = null

    private var pendingSurface: NativeSurface? = null
    private var pendingWidth: Int = 0
    private var pendingHeight: Int = 0

    fun initialize() {
        println("FilamentRenderer: Initializing engine and MaterialBuilder...")
        MaterialBuilder.init()
        engine = Engine.create()
        renderer = engine!!.createRenderer()
        scene = engine!!.createScene()
        view = engine!!.createView()
        camera = engine!!.createCamera()

        view!!.setScene(scene)
        view!!.setCamera(camera)

        // Simple skybox
        skybox = Skybox.Builder()
            .color(0.1f, 0.12f, 0.15f, 1.0f) // Darker blueish background
            .build(engine!!)
        scene!!.setSkybox(skybox)
        
        // Setup initial camera
        camera!!.setProjection(45.0, 1.0, 0.1, 100.0, Camera.Fov.VERTICAL)
        camera!!.lookAt(0.0, 0.0, 6.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0)

        setupCube()
        println("FilamentRenderer: Cube setup complete.")

        // Handle surface if it became available before engine was ready
        pendingSurface?.let {
            println("FilamentRenderer: Applying pending surface...")
            onSurfaceAvailable(it, pendingWidth, pendingHeight)
            pendingSurface = null
        }
    }

    private fun setupCube() {
        val engine = engine!!
        
        val materialPackage = MaterialBuilder()
            .name("BakedColor")
            .platform(MaterialBuilder.Platform.MOBILE)
            .targetApi(MaterialBuilder.TargetApi.ALL)
            .shading(MaterialBuilder.Shading.UNLIT)
            .doubleSided(true)
            .culling(MaterialBuilder.CullingMode.NONE)
            .require(VertexBuffer.VertexAttribute.COLOR)
            .material("void material(inout MaterialInputs material) { prepareMaterial(material); material.baseColor = getColor(); }")
            .build()
        
        if (!materialPackage.isValid()) {
            println("FilamentRenderer: FAILED to compile material at runtime!")
            return
        }

        material = Material.Builder()
            .payload(materialPackage.getBuffer())
            .build(engine)
        
        materialInstance = material!!.createInstance()

        val side = 1.0f
        val vertices = floatArrayOf(
            // Position (X, Y, Z), Color (R, G, B, A)
            -side, -side,  side, 1.0f, 0.0f, 0.0f, 1.0f, // 0: Red
             side, -side,  side, 0.0f, 1.0f, 0.0f, 1.0f, // 1: Green
             side,  side,  side, 0.0f, 0.0f, 1.0f, 1.0f, // 2: Blue
            -side,  side,  side, 1.0f, 1.0f, 0.0f, 1.0f, // 3: Yellow
            -side, -side, -side, 0.0f, 1.0f, 1.0f, 1.0f, // 4: Cyan
             side, -side, -side, 1.0f, 0.0f, 1.0f, 1.0f, // 5: Magenta
             side,  side, -side, 1.0f, 1.0f, 1.0f, 1.0f, // 6: White
            -side,  side, -side, 0.0f, 0.0f, 0.0f, 1.0f  // 7: Black
        )

        val indices = intArrayOf(
            // Front
            0, 1, 2, 2, 3, 0,
            // Back
            4, 7, 6, 6, 5, 4,
            // Left
            0, 3, 7, 7, 4, 0,
            // Right
            1, 5, 6, 6, 2, 1,
            // Top
            3, 2, 6, 6, 7, 3,
            // Bottom
            0, 4, 5, 5, 1, 0
        )

        vertexBuffer = VertexBuffer.Builder()
            .vertexCount(8)
            .bufferCount(1)
            // Position is 3 floats, Color is 4 floats. Total stride = (3+4)*4 = 28 bytes
            .attribute(VertexBuffer.VertexAttribute.POSITION, 0, VertexBuffer.AttributeType.FLOAT3, 0, 28)
            .attribute(VertexBuffer.VertexAttribute.COLOR, 0, VertexBuffer.AttributeType.FLOAT4, 12, 28)
            .build(engine)

        vertexBuffer!!.setBufferAt(engine, 0, vertices.toBytes())

        indexBuffer = IndexBuffer.Builder()
            .indexCount(indices.size)
            .bufferType(IndexBuffer.Builder.IndexType.UINT)
            .build(engine)

        indexBuffer!!.setBuffer(engine, indices.toBytes())

        val entityManager = engine.getEntityManager()
        cubeEntity = entityManager.create()

        RenderableManager.Builder(1)
            .boundingBox(Box(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f))
            .geometry(0, RenderableManager.PrimitiveType.TRIANGLES, vertexBuffer!!, indexBuffer!!)
            .material(0, materialInstance!!)
            .build(engine, cubeEntity!!)

        scene!!.addEntity(cubeEntity!!)
    }

    override fun onSurfaceAvailable(surface: NativeSurface, width: Int, height: Int) {
        val engine = engine
        if (engine == null) {
            println("FilamentRenderer: onSurfaceAvailable called but engine is null, saving as pending")
            pendingSurface = surface
            pendingWidth = width
            pendingHeight = height
            return
        }

        println("FilamentRenderer: Creating SwapChain ($width x $height)...")
        if (swapChain != null) {
            engine.destroySwapChain(swapChain!!)
        }
        swapChain = engine.createSwapChain(surface)
        onSurfaceResized(width, height)
    }

    override fun onSurfaceResized(width: Int, height: Int) {
        if (width <= 0 || height <= 0) return

        view?.setViewport(Viewport(0, 0, width, height))
        val aspect = width.toDouble() / height.toDouble()
        camera?.setProjection(45.0, aspect, 0.1, 100.0, Camera.Fov.VERTICAL)
    }

    override fun onSurfaceDetached() {
        engine?.let {
            if (swapChain != null) {
                it.destroySwapChain(swapChain!!)
                swapChain = null
            }
        }
    }

    private var startTime: Long = -1

    override fun render(frameTimeNanos: Long) {
        val renderer = renderer ?: return
        val swapChain = swapChain ?: return
        val view = view ?: return

        if (startTime < 0) startTime = frameTimeNanos
        val elapsed = (frameTimeNanos - startTime).toDouble() / 1_000_000_000.0

        updateRotation(elapsed)

        if (renderer.beginFrame(swapChain, frameTimeNanos)) {
            renderer.render(view)
            renderer.endFrame()
        }
    }

    private fun updateRotation(time: Double) {
        val engine = engine ?: return
        val entity = cubeEntity ?: return
        val tm = engine.getTransformManager()
        val instance = tm.getInstance(entity)

        val angle = time * 0.5 // Radians per second
        val rotationMatrix = rotationY(angle)
        multiplyMatrices(rotationMatrix, rotationX(angle * 0.7), rotationMatrix)
        
        tm.setTransform(instance, rotationMatrix)
    }

    private fun rotationX(angle: Double): DoubleArray {
        val c = kotlin.math.cos(angle)
        val s = kotlin.math.sin(angle)
        return doubleArrayOf(
            1.0, 0.0, 0.0, 0.0,
            0.0,   c,   s, 0.0,
            0.0,  -s,   c, 0.0,
            0.0, 0.0, 0.0, 1.0
        )
    }

    private fun rotationY(angle: Double): DoubleArray {
        val c = kotlin.math.cos(angle)
        val s = kotlin.math.sin(angle)
        return doubleArrayOf(
              c, 0.0,  -s, 0.0,
            0.0, 1.0, 0.0, 0.0,
              s, 0.0,   c, 0.0,
            0.0, 0.0, 0.0, 1.0
        )
    }

    private fun multiplyMatrices(a: DoubleArray, b: DoubleArray, result: DoubleArray) {
        val tmp = DoubleArray(16)
        for (i in 0..3) {
            for (j in 0..3) {
                var sum = 0.0
                for (k in 0..3) {
                    sum += a[i + k * 4] * b[k + j * 4]
                }
                tmp[i + j * 4] = sum
            }
        }
        for (i in 0..15) result[i] = tmp[i]
    }

    fun destroy() {
        engine?.let {
            if (cubeEntity != null) {
                it.getRenderableManager().destroy(cubeEntity!!)
                it.getTransformManager().destroy(cubeEntity!!)
                it.getEntityManager().destroy(cubeEntity!!)
            }
            if (materialInstance != null) it.destroyMaterialInstance(materialInstance!!)
            if (material != null) it.destroyMaterial(material!!)
            if (vertexBuffer != null) it.destroyVertexBuffer(vertexBuffer!!)
            if (indexBuffer != null) it.destroyIndexBuffer(indexBuffer!!)
            if (skybox != null) it.destroySkybox(skybox!!)
            if (camera != null) it.destroyCamera(camera!!)
            if (view != null) it.destroyView(view!!)
            if (scene != null) it.destroyScene(scene!!)
            if (renderer != null) it.destroyRenderer(renderer!!)
            if (swapChain != null) it.destroySwapChain(swapChain!!)
            it.destroy()
        }
        engine = null
        renderer = null
        scene = null
        view = null
        camera = null
        swapChain = null
        skybox = null
        cubeEntity = null
        vertexBuffer = null
        indexBuffer = null
        material = null
        materialInstance = null
    }
}

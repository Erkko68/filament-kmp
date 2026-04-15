package eric.bitria.samples

import io.github.erkko68.filament.*
import io.github.erkko68.filament.filamat.*
import io.github.erkko68.filament.gltfio.*

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
    private var sunLightEntity: Entity? = null

    // Object 1: Runtime compiled material
    private var runtimeCubeEntity: Entity? = null
    private var runtimeVertexBuffer: VertexBuffer? = null
    private var runtimeIndexBuffer: IndexBuffer? = null
    private var runtimeMaterial: Material? = null
    private var runtimeMaterialInstance: MaterialInstance? = null

    // Object 2: Resource-loaded compiled material
    private var resourceCubeEntity: Entity? = null
    private var resourceVertexBuffer: VertexBuffer? = null
    private var resourceIndexBuffer: IndexBuffer? = null
    private var resourceMaterial: Material? = null
    private var resourceMaterialInstance: MaterialInstance? = null

    // Object 3: GLTFIO loaded model
    private var gltfLoader: AssetLoader? = null
    private var gltfResourceLoader: ResourceLoader? = null
    private var gltfAsset: FilamentAsset? = null
    private var gltfMaterialProvider: MaterialProvider? = null
    private var gltfBaseTransform: DoubleArray? = null

    private var pendingSurface: NativeSurface? = null
    private var pendingWidth: Int = 0
    private var pendingHeight: Int = 0
    private var pendingFilamatData: ByteArray? = null
    private var pendingGlbData: ByteArray? = null

    fun initialize() {
        println("FilamentRenderer: Initializing engine and MaterialBuilder...")
        engine = Engine.create()
        MaterialBuilder.init()
        Gltfio.init()
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

        // glTF PBR materials need some light; a simple directional light is enough for samples.
        sunLightEntity = engine!!.getEntityManager().create()
        LightManager.Builder(LightManager.Type.DIRECTIONAL)
            .color(1.0f, 1.0f, 1.0f)
            .intensity(100_000.0f)
            .direction(0.3f, -1.0f, -0.5f)
            .build(engine!!, sunLightEntity!!)
        scene!!.addEntity(sunLightEntity!!)

        // Setup initial camera
        camera!!.setProjection(45.0, 1.0, 0.1, 100.0, Camera.Fov.VERTICAL)
        camera!!.lookAt(0.0, 1.0, 10.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0)

        setupRuntimeMaterialCube()
        
        // Initialize gltfio components
        gltfMaterialProvider = UbershaderProvider(engine!!)
        gltfLoader = AssetLoader.create(engine!!, gltfMaterialProvider!!, engine!!.getEntityManager())
        gltfResourceLoader = ResourceLoader(engine!!, true)

        // Handle surface if it became available before engine was ready
        pendingSurface?.let {
            println("FilamentRenderer: Applying pending surface...")
            onSurfaceAvailable(it, pendingWidth, pendingHeight)
            pendingSurface = null
        }

        pendingFilamatData?.let {
            println("FilamentRenderer: Applying pending filamat...")
            setupResourceMaterialCube(it)
            pendingFilamatData = null
        }

        pendingGlbData?.let {
            println("FilamentRenderer: Applying pending glb...")
            setupGltfModel(it)
            pendingGlbData = null
        }
    }

    private fun setupRuntimeMaterialCube() {
        val engine = engine!!
        
        println("FilamentRenderer: Setting up Runtime Material Object...")
        val materialPackage = MaterialBuilder()
            .name("RuntimeMaterial")
            .platform(MaterialBuilder.Platform.ALL)
            .targetApi(MaterialBuilder.TargetApi.ALL)
            .shading(MaterialBuilder.Shading.UNLIT)
            .doubleSided(true)
            .require(VertexBuffer.VertexAttribute.COLOR)
            .material("void material(inout MaterialInputs material) { prepareMaterial(material); material.baseColor = getColor(); }")
            .build()
        
        if (!materialPackage.isValid()) {
            println("FilamentRenderer: FAILED to compile runtime material!")
            return
        }

        runtimeMaterial = Material.Builder()
            .payload(materialPackage.getBuffer())
            .build(engine)
        runtimeMaterialInstance = runtimeMaterial!!.createInstance()

        val side = 1.0f
        val vertices = floatArrayOf(
            -side, -side,  side, 1.0f, 0.3f, 0.3f, 1.0f,
             side, -side,  side, 1.0f, 0.3f, 0.3f, 1.0f,
             side,  side,  side, 1.0f, 0.3f, 0.3f, 1.0f,
            -side,  side,  side, 1.0f, 0.3f, 0.3f, 1.0f,
            -side, -side, -side, 1.0f, 0.3f, 0.3f, 1.0f,
             side, -side, -side, 1.0f, 0.3f, 0.3f, 1.0f,
             side,  side, -side, 1.0f, 0.3f, 0.3f, 1.0f,
            -side,  side, -side, 1.0f, 0.3f, 0.3f, 1.0f 
        )
        val indices = intArrayOf(0, 1, 2, 2, 3, 0, 4, 7, 6, 6, 5, 4, 0, 3, 7, 7, 4, 0, 1, 5, 6, 6, 2, 1, 3, 2, 6, 6, 7, 3, 0, 4, 5, 5, 1, 0)

        runtimeVertexBuffer = VertexBuffer.Builder()
            .vertexCount(8)
            .bufferCount(1)
            .attribute(VertexBuffer.VertexAttribute.POSITION, 0, VertexBuffer.AttributeType.FLOAT3, 0, 28)
            .attribute(VertexBuffer.VertexAttribute.COLOR, 0, VertexBuffer.AttributeType.FLOAT4, 12, 28)
            .build(engine)
        runtimeVertexBuffer!!.setBufferAt(engine, 0, vertices.toBytes())

        runtimeIndexBuffer = IndexBuffer.Builder()
            .indexCount(indices.size)
            .bufferType(IndexBuffer.Builder.IndexType.UINT)
            .build(engine)
        runtimeIndexBuffer!!.setBuffer(engine, indices.toBytes())

        runtimeCubeEntity = engine.getEntityManager().create()
        RenderableManager.Builder(1)
            .boundingBox(Box(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f))
            .geometry(0, RenderableManager.PrimitiveType.TRIANGLES, runtimeVertexBuffer!!, runtimeIndexBuffer!!)
            .material(0, runtimeMaterialInstance!!)
            .build(engine, runtimeCubeEntity!!)

        scene!!.addEntity(runtimeCubeEntity!!)
        
        // Position it on the left
        val tm = engine.getTransformManager()
        tm.setTransform(tm.getInstance(runtimeCubeEntity!!), translation(-2.5, 0.0, 0.0))
    }

    fun setupResourceMaterialCube(filamatData: ByteArray) {
        val engine = engine
        if (engine == null) {
            pendingFilamatData = filamatData
            println("FilamentRenderer: Engine not ready, queued filamat setup")
            return
        }

        println("FilamentRenderer: Setting up Resource Material Object...")
        resourceMaterial = Material.Builder()
            .payload(filamatData)
            .build(engine)
        resourceMaterialInstance = resourceMaterial!!.createInstance()
        resourceMaterialInstance!!.setParameter("color", 0.3f, 1.0f, 0.3f, 1.0f) // Set to green

        // Shared geometry for simplicity
        resourceCubeEntity = engine.getEntityManager().create()
        RenderableManager.Builder(1)
            .boundingBox(Box(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f))
            .geometry(0, RenderableManager.PrimitiveType.TRIANGLES, runtimeVertexBuffer!!, runtimeIndexBuffer!!)
            .material(0, resourceMaterialInstance!!)
            .build(engine, resourceCubeEntity!!)

        scene!!.addEntity(resourceCubeEntity!!)
        
        // Position it in the center
        val tm = engine.getTransformManager()
        tm.setTransform(tm.getInstance(resourceCubeEntity!!), translation(0.0, 0.0, 0.0))
    }

    fun setupGltfModel(glbData: ByteArray) {
        val engine = engine
        val loader = gltfLoader
        if (engine == null || loader == null) {
            pendingGlbData = glbData
            println("FilamentRenderer: GLTF loader not ready, queued glb setup")
            return
        }

        gltfAsset?.let { existing ->
            scene?.removeEntities(existing.getEntities())
            gltfLoader?.destroyAsset(existing)
            gltfAsset = null
        }

        println("FilamentRenderer: Setting up GLTF Model Object...")
        gltfAsset = loader.createAsset(glbData)
        if (gltfAsset != null) {
            val asset = gltfAsset!!
            val loaded = gltfResourceLoader?.loadResources(asset) ?: false
            scene!!.addEntities(asset.getEntities())
            println(
                "FilamentRenderer: GLTF loaded=$loaded entities=${asset.getEntityCount()} renderables=${asset.getRenderableEntities().size}"
            )

            // Calculate auto-scale and centering
            val box = asset.getBoundingBox()
            val center = box.center
            val halfExtent = box.halfExtent
            val maxDim = maxOf(halfExtent[0], maxOf(halfExtent[1], halfExtent[2]))
            val scale = if (maxDim > 0) 3.5 / (maxDim * 2.0) else 1.0
            
            val transform = identity()
            // Transform = Scale(s) * Translation(-center)
            multiplyMatrices(transform, scaling(scale), transform)
            multiplyMatrices(transform, translation(-center[0].toDouble(), -center[1].toDouble(), -center[2].toDouble()), transform)
            gltfBaseTransform = transform

            // Initial position on the right
            val tm = engine.getTransformManager()
            val initialTransform = translation(2.5, 0.0, 0.0)
            multiplyMatrices(initialTransform, gltfBaseTransform!!, initialTransform)
            tm.setTransform(tm.getInstance(asset.getRoot()), initialTransform)
            asset.releaseSourceData()
        } else {
            println("FilamentRenderer: FAILED to load GLB asset!")
        }
    }

    override fun onSurfaceAvailable(surface: NativeSurface, width: Int, height: Int) {
        val engine = engine
        if (engine == null) {
            pendingSurface = surface
            pendingWidth = width
            pendingHeight = height
            return
        }

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
        val tm = engine.getTransformManager()
        val angle = time * 0.5

        // Rotate runtime cube
        runtimeCubeEntity?.let {
            val transform = translation(-2.5, 0.0, 0.0)
            multiplyMatrices(transform, rotationY(angle), transform)
            tm.setTransform(tm.getInstance(it), transform)
        }

        // Rotate resource cube
        resourceCubeEntity?.let {
            val transform = translation(0.0, 0.0, 0.0)
            multiplyMatrices(transform, rotationY(angle * 1.5), transform)
            tm.setTransform(tm.getInstance(it), transform)
        }

        // Rotate gltf model root
        gltfAsset?.let { asset ->
            gltfBaseTransform?.let { base ->
                val transform = translation(2.5, 0.0, 0.0)
                multiplyMatrices(transform, rotationY(angle * 0.8), transform)
                multiplyMatrices(transform, base, transform)
                tm.setTransform(tm.getInstance(asset.getRoot()), transform)
            }
        }
    }

    private fun translation(x: Double, y: Double, z: Double): DoubleArray {
        return doubleArrayOf(
            1.0, 0.0, 0.0, 0.0,
            0.0, 1.0, 0.0, 0.0,
            0.0, 0.0, 1.0, 0.0,
              x,   y,   z, 1.0
        )
    }

    private fun scaling(s: Double): DoubleArray {
        return doubleArrayOf(
              s, 0.0, 0.0, 0.0,
            0.0,   s, 0.0, 0.0,
            0.0, 0.0,   s, 0.0,
            0.0, 0.0, 0.0, 1.0
        )
    }

    private fun identity(): DoubleArray {
        return doubleArrayOf(
            1.0, 0.0, 0.0, 0.0,
            0.0, 1.0, 0.0, 0.0,
            0.0, 0.0, 1.0, 0.0,
            0.0, 0.0, 0.0, 1.0
        )
    }

    private fun rotationX(angle: Double): DoubleArray {
        val c = kotlin.math.cos(angle)
        val s = kotlin.math.sin(angle)
        return doubleArrayOf(1.0, 0.0, 0.0, 0.0, 0.0, c, s, 0.0, 0.0, -s, c, 0.0, 0.0, 0.0, 0.0, 1.0)
    }

    private fun rotationY(angle: Double): DoubleArray {
        val c = kotlin.math.cos(angle)
        val s = kotlin.math.sin(angle)
        return doubleArrayOf(c, 0.0, -s, 0.0, 0.0, 1.0, 0.0, 0.0, s, 0.0, c, 0.0, 0.0, 0.0, 0.0, 1.0)
    }

    private fun multiplyMatrices(a: DoubleArray, b: DoubleArray, result: DoubleArray) {
        val tmp = DoubleArray(16)
        for (i in 0..3) {
            for (j in 0..3) {
                var sum = 0.0
                for (k in 0..3) sum += a[i + k * 4] * b[k + j * 4]
                tmp[i + j * 4] = sum
            }
        }
        for (i in 0..15) result[i] = tmp[i]
    }

    fun destroy() {
        engine?.let {
            runtimeCubeEntity?.let { e -> it.getEntityManager().destroy(e) }
            resourceCubeEntity?.let { e -> it.getEntityManager().destroy(e) }
            sunLightEntity?.let { e -> it.destroyEntity(e) }

            gltfAsset?.let { a -> gltfLoader?.destroyAsset(a) }
            gltfLoader?.let { l -> AssetLoader.destroy(l) }
            gltfResourceLoader?.destroy()
            gltfMaterialProvider?.destroy()

            runtimeMaterialInstance?.let { m -> it.destroyMaterialInstance(m) }
            runtimeMaterial?.let { m -> it.destroyMaterial(m) }
            runtimeVertexBuffer?.let { v -> it.destroyVertexBuffer(v) }
            runtimeIndexBuffer?.let { i -> it.destroyIndexBuffer(i) }

            resourceMaterialInstance?.let { m -> it.destroyMaterialInstance(m) }
            resourceMaterial?.let { m -> it.destroyMaterial(m) }

            skybox?.let { s -> it.destroySkybox(s) }
            camera?.let { c -> it.destroyCamera(c) }
            view?.let { v -> it.destroyView(v) }
            scene?.let { s -> it.destroyScene(s) }
            renderer?.let { r -> it.destroyRenderer(r) }
            swapChain?.let { s -> it.destroySwapChain(s) }
            it.destroy()
        }
        engine = null
        renderer = null
        scene = null
        view = null
        camera = null
        swapChain = null
        skybox = null
        sunLightEntity = null
        runtimeCubeEntity = null
        resourceCubeEntity = null
        gltfAsset = null
    }
}

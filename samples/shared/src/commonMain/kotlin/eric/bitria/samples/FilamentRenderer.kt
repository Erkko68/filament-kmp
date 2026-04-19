package eric.bitria.samples

import io.github.erkko68.filament.*
import io.github.erkko68.filament.filamat.*
import io.github.erkko68.filament.gltfio.*
import androidx.compose.runtime.mutableStateOf

class FilamentRenderer : FilamentViewRenderer {
    var width = 0
    var height = 0
    
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
    var renderTarget: RenderTarget? = null
        private set
    var colorTexture: Texture? = null
        private set
    var depthTexture: Texture? = null
        private set
    private var sunLightEntity: Entity? = null


    private var runtimeCubeEntity: Entity? = null
    private var runtimeVertexBuffer: VertexBuffer? = null
    private var runtimeIndexBuffer: IndexBuffer? = null
    private var runtimeMaterial: Material? = null
    private var runtimeMaterialInstance: MaterialInstance? = null

    private var resourceCubeEntity: Entity? = null
    private var resourceMaterial: Material? = null
    private var resourceMaterialInstance: MaterialInstance? = null

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
        if (engine != null) return
        engine = Engine.create(Engine.Backend.METAL)
        MaterialBuilder.init()
        Gltfio.init()
        renderer = engine!!.createRenderer()
        scene = engine!!.createScene()
        view = engine!!.createView()
        camera = engine!!.createCamera()

        view!!.setScene(scene)
        view!!.setCamera(camera)

        skybox = Skybox.Builder()
            .color(1.0f, 0.0f, 0.0f, 1.0f)
            .build(engine!!)
        scene!!.setSkybox(skybox)


        sunLightEntity = engine!!.getEntityManager().create()
        LightManager.Builder(LightManager.Type.DIRECTIONAL)
            .color(1.0f, 1.0f, 1.0f)
            .intensity(100_000.0f)
            .direction(0.3f, -1.0f, -0.5f)
            .build(engine!!, sunLightEntity!!)
        scene!!.addEntity(sunLightEntity!!)

        renderer!!.setClearOptions(Renderer.ClearOptions().apply {
            clear = true
            clearColor = floatArrayOf(0.0f, 1.0f, 1.0f, 1.0f)
        })




        // Setup initial camera

        camera!!.setProjection(45.0, 1.0, 0.1, 100.0, Camera.Fov.VERTICAL)
        camera!!.lookAt(0.0, 1.0, 10.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0)
        camera!!.setExposure(16.0f, 1.2f, 100.0f)

        view!!.setPostProcessingEnabled(false)
        
        setupRuntimeMaterialCube()

        
        // Initialize gltfio components
        gltfMaterialProvider = UbershaderProvider(engine!!)
        gltfLoader = AssetLoader.create(engine!!, gltfMaterialProvider!!, engine!!.getEntityManager())
        gltfResourceLoader = ResourceLoader(engine!!, true)

        pendingSurface?.let {
            onSurfaceAvailable(it, pendingWidth, pendingHeight)
            pendingSurface = null
        }

        pendingFilamatData?.let {
            setupResourceMaterialCube(it)
            pendingFilamatData = null
        }

        pendingGlbData?.let {
            setupGltfModel(it)
            pendingGlbData = null
        }
    }

    fun initializeOffscreen(width: Int, height: Int, textureHandle: Long) {
        if (engine == null) initialize()
        val engine = engine!!

        this.width = width
        this.height = height
        
        // 1. Import or recreate the external color texture
        colorTexture?.let { engine.destroyTexture(it) }
        colorTexture = Texture.Builder()
            .width(width)
            .height(height)
            .levels(1)
            .usage(Texture.Usage.COLOR_ATTACHMENT or Texture.Usage.SAMPLEABLE)
            .format(Texture.InternalFormat.RGBA8)
            .importTexture(textureHandle)
            .build(engine)
        
        // 2. Create/Recreate Depth RenderTarget
        // We need a depth buffer for 3D rendering to work correctly
        depthTexture?.let { engine.destroyTexture(it) }
        depthTexture = Texture.Builder()
            .width(width)
            .height(height)
            .levels(1)
            .usage(Texture.Usage.DEPTH_ATTACHMENT)
            .format(Texture.InternalFormat.DEPTH32F) // Use 32F for better compatibility on Metal
            .build(engine)
        
        renderTarget?.let { engine.destroyRenderTarget(it) }
        renderTarget = RenderTarget.Builder()
            .texture(RenderTarget.AttachmentPoint.COLOR, colorTexture!!)
            .texture(RenderTarget.AttachmentPoint.DEPTH, depthTexture!!)
            .build(engine)
            
        view?.setMultiSampleAntiAliasingOptions(View.MultiSampleAntiAliasingOptions().apply {
            enabled = false
        })
        view?.setRenderTarget(renderTarget)

        
        // 4. Create headless swapchain
        destroySwapChain(engine)
        swapChain = engine.createSwapChain(width, height, 0)
        
        onSurfaceResized(width, height)
    }


    fun createSharedTexture(devicePtr: Long, physDevicePtr: Long, width: Int, height: Int): Long =
        io.github.erkko68.filament.jni.Texture.nCreateSharedTexture(devicePtr, physDevicePtr, width, height)

    fun releaseSharedTexture(handle: Long) {
        if (handle != 0L) io.github.erkko68.filament.jni.Texture.nReleaseSharedTexture(handle)
    }

    private fun setupRuntimeMaterialCube() {
        val engine = engine!!
        
        val materialPackage = MaterialBuilder()
            .name("RuntimeMaterial")
            .platform(MaterialBuilder.Platform.ALL)
            .targetApi(MaterialBuilder.TargetApi.METAL)
            .shading(MaterialBuilder.Shading.UNLIT)
            .doubleSided(true)
            .require(VertexBuffer.VertexAttribute.COLOR)
            .material("void material(inout MaterialInputs material) { prepareMaterial(material); material.baseColor = float4(1.0, 0.5, 0.5, 1.0); }")
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
        val tm = engine.getTransformManager()
        tm.setTransform(tm.getInstance(runtimeCubeEntity!!), translation(-2.5, 0.0, 0.0))
    }

    fun setupResourceMaterialCube(filamatData: ByteArray) {
        val engine = engine
        if (engine == null) {
            pendingFilamatData = filamatData
            return
        }

        destroyResourceMaterialCube(engine)

        resourceMaterial = Material.Builder()
            .payload(filamatData)
            .build(engine)
        resourceMaterialInstance = resourceMaterial!!.createInstance()
        resourceMaterialInstance!!.setParameter("color", 0.3f, 1.0f, 0.3f, 1.0f)

        resourceCubeEntity = engine.getEntityManager().create()
        RenderableManager.Builder(1)
            .boundingBox(Box(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f))
            .geometry(0, RenderableManager.PrimitiveType.TRIANGLES, runtimeVertexBuffer!!, runtimeIndexBuffer!!)
            .material(0, resourceMaterialInstance!!)
            .build(engine, resourceCubeEntity!!)

        scene!!.addEntity(resourceCubeEntity!!)
        val tm = engine.getTransformManager()
        tm.setTransform(tm.getInstance(resourceCubeEntity!!), translation(0.0, 0.0, 0.0))
    }

    fun setupGltfModel(glbData: ByteArray) {
        val engine = engine
        val loader = gltfLoader
        if (engine == null || loader == null) {
            pendingGlbData = glbData
            return
        }

        destroyGltfAsset()

        gltfAsset = loader.createAsset(glbData)
        if (gltfAsset != null) {
            val asset = gltfAsset!!
            gltfResourceLoader?.loadResources(asset)
            scene!!.addEntities(asset.getEntities())

            val box = asset.getBoundingBox()
            val center = box.center
            val halfExtent = box.halfExtent
            val maxDim = maxOf(halfExtent[0], maxOf(halfExtent[1], halfExtent[2]))
            val scale = if (maxDim > 0) 3.5 / (maxDim * 2.0) else 1.0
            
            val transform = identity()
            multiplyMatrices(transform, scaling(scale), transform)
            multiplyMatrices(transform, translation(-center[0].toDouble(), -center[1].toDouble(), -center[2].toDouble()), transform)
            gltfBaseTransform = transform

            val tm = engine.getTransformManager()
            val initialTransform = translation(2.5, 0.0, 0.0)
            multiplyMatrices(initialTransform, gltfBaseTransform!!, initialTransform)
            tm.setTransform(tm.getInstance(asset.getRoot()), initialTransform)
            asset.releaseSourceData()
        } else {
            gltfBaseTransform = null
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

        destroySwapChain(engine)
        val createdSwapChain = try {
            engine.createSwapChain(surface)
        } catch (_: IllegalStateException) {
            pendingSurface = surface
            pendingWidth = width
            pendingHeight = height
            return
        }
        if (!engine.isValidSwapChain(createdSwapChain)) {
            // On macOS AWT the CAMetalLayer can be unavailable briefly after show().
            pendingSurface = surface
            pendingWidth = width
            pendingHeight = height
            destroySwapChain(engine)
            return
        }
        swapChain = createdSwapChain
        pendingSurface = null
        pendingWidth = 0
        pendingHeight = 0
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
            destroySwapChain(it)
        }
    }

    var frameCount = mutableStateOf(0L)
        private set

    private var startTime: Long = -1

    override fun render(frameTimeNanos: Long) {
        if (swapChain == null) {
            pendingSurface?.let { surface ->
                onSurfaceAvailable(surface, pendingWidth, pendingHeight)
            }
        }

        val renderer = renderer ?: return
        val swapChain = swapChain ?: return
        val view = view ?: return

        if (startTime < 0) startTime = frameTimeNanos
        val elapsed = (frameTimeNanos - startTime).toDouble() / 1_000_000_000.0

        updateRotation(elapsed)

        val beginSucceeded = renderer.beginFrame(swapChain, frameTimeNanos)

        if (beginSucceeded) {
            // Ensure viewport matches texture size
            view.setViewport(Viewport(0, 0, width, height))
        
            renderer.render(view)



            renderer.endFrame()
            engine!!.flushAndWait() 
            frameCount.value++
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

    private fun destroySwapChain(engine: Engine) {
        swapChain?.let { current ->
            engine.destroySwapChain(current)
            swapChain = null
        }
    }

    private fun destroyResourceMaterialCube(engine: Engine) {
        resourceCubeEntity?.let { e ->
            engine.getEntityManager().destroy(e)
            resourceCubeEntity = null
        }
        resourceMaterialInstance?.let { m ->
            engine.destroyMaterialInstance(m)
            resourceMaterialInstance = null
        }
        resourceMaterial?.let { m ->
            engine.destroyMaterial(m)
            resourceMaterial = null
        }
    }

    private fun destroyGltfAsset() {
        gltfAsset?.let { existing ->
            scene?.removeEntities(existing.getEntities())
            gltfLoader?.destroyAsset(existing)
            gltfAsset = null
            gltfBaseTransform = null
        }
    }

    fun destroy() {
        engine?.let {
            runtimeCubeEntity?.let { e -> it.getEntityManager().destroy(e) }
            destroyResourceMaterialCube(it)
            sunLightEntity?.let { e -> it.destroyEntity(e) }

            destroyGltfAsset()
            gltfLoader?.let { l -> AssetLoader.destroy(l) }
            gltfResourceLoader?.destroy()
            gltfMaterialProvider?.destroy()

            colorTexture?.let { t -> it.destroyTexture(t) }
            depthTexture?.let { t -> it.destroyTexture(t) }
            renderTarget?.let { rt -> it.destroyRenderTarget(rt) }

            runtimeMaterialInstance?.let { m -> it.destroyMaterialInstance(m) }
            runtimeMaterial?.let { m -> it.destroyMaterial(m) }
            runtimeVertexBuffer?.let { v -> it.destroyVertexBuffer(v) }
            runtimeIndexBuffer?.let { i -> it.destroyIndexBuffer(i) }

            skybox?.let { s -> it.destroySkybox(s) }
            camera?.let { c -> it.destroyCamera(c) }
            view?.let { v -> it.destroyView(v) }
            scene?.let { s -> it.destroyScene(s) }
            renderer?.let { r -> it.destroyRenderer(r) }
            destroySwapChain(it)
            it.destroy()
        }
        engine = null
        renderer = null
        scene = null
        view = null
        camera = null
        swapChain = null
        skybox = null
        renderTarget = null
        colorTexture = null
        sunLightEntity = null
        runtimeCubeEntity = null
        runtimeVertexBuffer = null
        runtimeIndexBuffer = null
        runtimeMaterial = null
        runtimeMaterialInstance = null
        resourceCubeEntity = null
        resourceMaterial = null
        resourceMaterialInstance = null
        gltfAsset = null
        gltfLoader = null
        gltfResourceLoader = null
        gltfMaterialProvider = null
        gltfBaseTransform = null
        pendingSurface = null
        pendingWidth = 0
        pendingHeight = 0
        pendingFilamatData = null
        pendingGlbData = null
        startTime = -1
    }
}

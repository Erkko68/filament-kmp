package eric.bitria.samples

import io.github.erkko68.filament.*
import io.github.erkko68.filament.filamat.MaterialBuilder
import io.github.erkko68.filament.gltfio.*

/**
 * Handles the 3D content for the sample app.
 * Separates entities, materials, and models from the engine management.
 */
class SampleScene(private val controller: FilamentController) {
    private var sunLightEntity: Entity? = null
    
    private var runtimeCubeEntity: Entity? = null
    private var runtimeVertexBuffer: VertexBuffer? = null
    private var runtimeIndexBuffer: IndexBuffer? = null
    private var runtimeMaterial: Material? = null
    private var runtimeMaterialInstance: MaterialInstance? = null

    private var resourceCubeEntity: Entity? = null
    private var resourceMaterialInstance: MaterialInstance? = null
    private var resourceMaterial: Material? = null

    private var gltfLoader: AssetLoader? = null
    private var gltfResourceLoader: ResourceLoader? = null
    private var gltfAsset: FilamentAsset? = null
    private var gltfMaterialProvider: MaterialProvider? = null
    private var gltfBaseTransform: DoubleArray? = null

    fun setup() {
        val engine = controller.engine ?: return
        val scene = controller.scene ?: return

        // 1. Setup Sun
        sunLightEntity = engine.getEntityManager().create()
        LightManager.Builder(LightManager.Type.DIRECTIONAL)
            .color(1.0f, 1.0f, 1.0f)
            .intensity(100_000.0f)
            .direction(0.3f, -1.0f, -0.5f)
            .build(engine, sunLightEntity!!)
        scene.addEntity(sunLightEntity!!)

        // 2. Setup GLTF Loaders
        gltfMaterialProvider = UbershaderProvider(engine)
        gltfLoader = AssetLoader.create(engine, gltfMaterialProvider!!, engine.getEntityManager())
        gltfResourceLoader = ResourceLoader(engine, true)

        // 3. Setup initial runtime content
        setupRuntimeCube()
    }

    private fun setupRuntimeCube() {
        val engine = controller.engine ?: return
        val scene = controller.scene ?: return

        val materialPackage = MaterialBuilder()
            .name("RuntimeMaterial")
            .platform(MaterialBuilder.Platform.ALL)
            .targetApi(MaterialBuilder.TargetApi.ALL)
            .shading(MaterialBuilder.Shading.UNLIT)
            .doubleSided(true)
            .require(VertexBuffer.VertexAttribute.COLOR)
            .material("void material(inout MaterialInputs material) { prepareMaterial(material); material.baseColor = float4(1.0, 0.5, 0.5, 1.0); }")
            .build()
        
        if (!materialPackage.isValid()) return

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

        scene.addEntity(runtimeCubeEntity!!)
        val tm = engine.getTransformManager()
        tm.setTransform(tm.getInstance(runtimeCubeEntity!!), translation(-2.5, 0.0, 0.0))
    }

    fun loadMaterialCube(filamatData: ByteArray) {
        val engine = controller.engine ?: return
        val scene = controller.scene ?: return

        destroyResourceMaterialCube()

        resourceMaterial = Material.Builder().payload(filamatData).build(engine)
        resourceMaterialInstance = resourceMaterial!!.createInstance()
        resourceMaterialInstance!!.setParameter("color", 0.3f, 1.0f, 0.3f, 1.0f)

        resourceCubeEntity = engine.getEntityManager().create()
        RenderableManager.Builder(1)
            .boundingBox(Box(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f))
            .geometry(0, RenderableManager.PrimitiveType.TRIANGLES, runtimeVertexBuffer!!, runtimeIndexBuffer!!)
            .material(0, resourceMaterialInstance!!)
            .build(engine, resourceCubeEntity!!)

        scene.addEntity(resourceCubeEntity!!)
        val tm = engine.getTransformManager()
        tm.setTransform(tm.getInstance(resourceCubeEntity!!), translation(0.0, 0.0, 0.0))
    }

    fun loadGltfModel(glbData: ByteArray) {
        val engine = controller.engine ?: return
        val scene = controller.scene ?: return
        val loader = gltfLoader ?: return

        destroyGltfAsset()

        gltfAsset = loader.createAsset(glbData)
        gltfAsset?.let { asset ->
            gltfResourceLoader?.loadResources(asset)
            scene.addEntities(asset.getEntities())

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
        }
    }

    fun updateAnimations(time: Double) {
        val engine = controller.engine ?: return
        val tm = engine.getTransformManager()
        val angle = time * 0.5

        runtimeCubeEntity?.let {
            val transform = translation(-2.5, 0.0, 0.0)
            multiplyMatrices(transform, rotationY(angle), transform)
            tm.setTransform(tm.getInstance(it), transform)
        }

        resourceCubeEntity?.let {
            val transform = translation(0.0, 0.0, 0.0)
            multiplyMatrices(transform, rotationY(angle * 1.5), transform)
            tm.setTransform(tm.getInstance(it), transform)
        }

        gltfAsset?.let { asset ->
            gltfBaseTransform?.let { base ->
                val transform = translation(2.5, 0.0, 0.0)
                multiplyMatrices(transform, rotationY(angle * 0.8), transform)
                multiplyMatrices(transform, base, transform)
                tm.setTransform(tm.getInstance(asset.getRoot()), transform)
            }
        }
    }

    private fun destroyResourceMaterialCube() {
        val engine = controller.engine ?: return
        resourceCubeEntity?.let { engine.destroyEntity(it); resourceCubeEntity = null }
        resourceMaterialInstance?.let { engine.destroyMaterialInstance(it); resourceMaterialInstance = null }
        resourceMaterial?.let { engine.destroyMaterial(it); resourceMaterial = null }
    }

    private fun destroyGltfAsset() {
        val scene = controller.scene ?: return
        val loader = gltfLoader ?: return
        gltfAsset?.let { 
            scene.removeEntities(it.getEntities())
            loader.destroyAsset(it)
            gltfAsset = null
            gltfBaseTransform = null
        }
    }

    fun destroy() {
        val engine = controller.engine ?: return
        
        destroyResourceMaterialCube()
        destroyGltfAsset()
        
        sunLightEntity?.let { engine.destroyEntity(it); sunLightEntity = null }
        runtimeCubeEntity?.let { engine.destroyEntity(it); runtimeCubeEntity = null }
        
        runtimeMaterialInstance?.let { engine.destroyMaterialInstance(it); runtimeMaterialInstance = null }
        runtimeMaterial?.let { engine.destroyMaterial(it); runtimeMaterial = null }
        runtimeVertexBuffer?.let { engine.destroyVertexBuffer(it); runtimeVertexBuffer = null }
        runtimeIndexBuffer?.let { engine.destroyIndexBuffer(it); runtimeIndexBuffer = null }
        
        gltfLoader?.let { AssetLoader.destroy(it) }
        gltfResourceLoader?.destroy()
        gltfMaterialProvider?.destroy()
    }
}

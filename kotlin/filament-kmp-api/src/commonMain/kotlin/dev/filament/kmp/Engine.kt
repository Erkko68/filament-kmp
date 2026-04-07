package dev.filament.kmp

/**
 * Engine is filament's main entry-point.
 *
 * An Engine instance main function is to keep track of all resources created by the user and
 * manage the rendering thread as well as the hardware renderer.
 */
expect class Engine {
    /**
     * Denotes a backend
     */
    enum class Backend {
        DEFAULT,
        OPENGL,
        VULKAN,
        METAL,
        WEBGPU,
        NOOP,
    }

    /**
     * Defines the backend's feature levels.
     */
    enum class FeatureLevel {
        FEATURE_LEVEL_0,
        FEATURE_LEVEL_1,
        FEATURE_LEVEL_2,
        FEATURE_LEVEL_3,
    }

    /**
     * The type of technique for stereoscopic rendering.
     */
    enum class StereoscopicType {
        NONE,
        INSTANCED,
        MULTIVIEW,
    }

    /**
     * This controls the priority level for GPU work scheduling.
     */
    enum class GpuContextPriority {
        DEFAULT,
        LOW,
        MEDIUM,
        HIGH,
        REALTIME,
    }

    /**
     * Parameters for customizing the initialization of Engine.
     */
    class Config() {
        var commandBufferSizeMB: Long
        var perRenderPassArenaSizeMB: Long
        var driverHandleArenaSizeMB: Long
        var minCommandBufferSizeMB: Long
        var perFrameCommandsSizeMB: Long
        var jobSystemThreadCount: Long
        var stereoscopicType: StereoscopicType
        var stereoscopicEyeCount: Long
        var resourceAllocatorCacheSizeMB: Long
        var resourceAllocatorCacheMaxAge: Long

        enum class ShaderLanguage {
            DEFAULT,
            MSL,
            METAL_LIBRARY,
        }
        var preferredShaderLanguage: ShaderLanguage
        var forceGLES2Context: Boolean
        var gpuContextPriority: GpuContextPriority
        var sharedUboInitialSizeInBytes: Long
    }

    /**
     * Constructs Engine objects using a builder pattern.
     */
    class Builder() {
        fun backend(backend: Backend): Builder
        fun sharedContext(sharedContext: Any): Builder
        fun config(config: Config): Builder
        fun featureLevel(featureLevel: FeatureLevel): Builder
        fun paused(paused: Boolean): Builder
        fun feature(name: String, value: Boolean): Builder
        fun build(): Engine
    }

    companion object {
        fun create(): Engine
        fun create(backend: Backend): Engine
        fun create(sharedContext: Any): Engine
        fun getSteadyClockTimeNano(): Long
    }

    fun isValid(): Boolean
    fun destroy()
    fun getBackend(): Backend
    fun getSupportedFeatureLevel(): FeatureLevel
    fun setActiveFeatureLevel(featureLevel: FeatureLevel): FeatureLevel
    fun getActiveFeatureLevel(): FeatureLevel
    fun setAutomaticInstancingEnabled(enable: Boolean)
    fun isAutomaticInstancingEnabled(): Boolean
    fun getConfig(): Config
    fun getMaxStereoscopicEyes(): Long
    
    fun isValidRenderer(renderer: Renderer): Boolean
    fun isValidView(view: View): Boolean
    fun isValidScene(scene: Scene): Boolean
    fun isValidFence(fence: Fence): Boolean
    fun isValidRenderTarget(renderTarget: RenderTarget): Boolean
    fun isValidIndexBuffer(indexBuffer: IndexBuffer): Boolean
    fun isValidVertexBuffer(vertexBuffer: VertexBuffer): Boolean
    fun isValidSkinningBuffer(skinningBuffer: SkinningBuffer): Boolean
    fun isValidMorphTargetBuffer(morphTargetBuffer: MorphTargetBuffer): Boolean
    fun isValidIndirectLight(ibl: IndirectLight): Boolean
    fun isValidMaterial(material: Material): Boolean
    fun isValidMaterialInstance(material: Material, materialInstance: MaterialInstance): Boolean
    fun isValidExpensiveMaterialInstance(materialInstance: MaterialInstance): Boolean
    fun isValidSkybox(skybox: Skybox): Boolean
    fun isValidColorGrading(colorGrading: ColorGrading): Boolean
    fun isValidTexture(texture: Texture): Boolean
    fun isValidStream(stream: Stream): Boolean
    fun isValidSwapChain(swapChain: SwapChain): Boolean

    // SwapChain
    fun createSwapChain(surface: Any): SwapChain
    fun createSwapChain(surface: Any, flags: Long): SwapChain
    fun createSwapChain(width: Int, height: Int, flags: Long): SwapChain
    fun destroySwapChain(swapChain: SwapChain)

    // View
    fun createView(): View
    fun destroyView(view: View)

    // Renderer
    fun createRenderer(): Renderer
    fun destroyRenderer(renderer: Renderer)

    // Camera
    fun createCamera(): Camera
    fun createCamera(entity: Int): Camera
    fun getCameraComponent(entity: Int): Camera?
    fun destroyCamera(camera: Camera)
    fun destroyCameraComponent(entity: Int)

    // Scene
    fun createScene(): Scene
    fun destroyScene(scene: Scene)

    // Fence
    fun createFence(): Fence
    fun destroyFence(fence: Fence)

    // Buffers and Managers
    fun destroyIndexBuffer(indexBuffer: IndexBuffer)
    fun destroyVertexBuffer(vertexBuffer: VertexBuffer)
    fun destroySkinningBuffer(skinningBuffer: SkinningBuffer)
    fun destroyMorphTargetBuffer(morphTargetBuffer: MorphTargetBuffer)
    fun destroyIndirectLight(ibl: IndirectLight)
    fun destroyMaterial(material: Material)
    fun destroyMaterialInstance(materialInstance: MaterialInstance)
    fun destroySkybox(skybox: Skybox)
    fun destroyColorGrading(colorGrading: ColorGrading)
    fun destroyTexture(texture: Texture)
    fun destroyRenderTarget(target: RenderTarget)
    fun destroyStream(stream: Stream)
    fun destroyEntity(entity: Int)

    // Managers
    fun getTransformManager(): TransformManager
    fun getLightManager(): LightManager
    fun getRenderableManager(): RenderableManager
    fun getEntityManager(): EntityManager

    // Command Queue management
    fun flushAndWait()
    fun flushAndWait(timeout: Long): Boolean
    fun flush()
    fun isPaused(): Boolean
    fun setPaused(paused: Boolean)
    fun unprotected()
    fun hasFeatureFlag(name: String): Boolean
    fun setFeatureFlag(name: String, value: Boolean): Boolean
    fun getFeatureFlag(name: String): Boolean
}

package io.github.erkko68.filament

/**
 * Engine is filament's main entry-point.
 *
 * An Engine instance main function is to keep track of all resources created by the user and
 * manage the rendering thread as well as the hardware renderer.
 */
expect class Engine {
    /** Rendering backend. AUTO selects the platform's optimal choice. NOOP for testing. */
    enum class Backend {
        DEFAULT,
        OPENGL,
        VULKAN,
        METAL,
        WEBGPU,
        NOOP,
    }

    /** Backend feature levels control available capabilities and requirements. */
    enum class FeatureLevel {
        /** Minimum; OpenGL ES 2 compatible; no post-processing. */
        FEATURE_LEVEL_0,
        /** Metal-level feature set. */
        FEATURE_LEVEL_1,
        /** Full feature set. */
        FEATURE_LEVEL_2,
        /** Advanced features. */
        FEATURE_LEVEL_3,
    }

    /** Stereoscopic rendering technique. */
    enum class StereoscopicType {
        /** No stereoscopic rendering. */
        NONE,
        /** Instanced stereo rendering (two draw calls). */
        INSTANCED,
        /** Multiview stereo rendering (single draw call, faster). */
        MULTIVIEW,
    }

    /** GPU context priority for work scheduling. */
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

    /** Builder for Engine configuration. */
    class Builder() {
        fun backend(backend: Backend): Builder
        fun sharedContext(sharedContext: Any): Builder
        fun config(config: Config): Builder
        fun featureLevel(featureLevel: FeatureLevel): Builder
        fun paused(paused: Boolean): Builder
        fun feature(name: String, value: Boolean): Builder
        fun colorGrading(colorGrading: ColorGrading.Builder): Builder
        fun build(): Engine
    }

    companion object {
        /** Create an Engine with default backend. */
        fun create(): Engine
        /** Create an Engine with specified backend. */
        fun create(backend: Backend): Engine
        /** Create an Engine sharing a GL context. */
        fun create(sharedContext: Any): Engine
        /** Get current steady clock time in nanoseconds. */
        fun getSteadyClockTimeNano(): Long
    }

    /** Check if this Engine is still valid (not destroyed). */
    fun isValid(): Boolean
    /** Destroy the Engine and all resources. Blocking operation. */
    fun destroy()
    /** Get the rendering backend used. */
    fun getBackend(): Backend
    /** Get the highest feature level supported by this backend. */
    fun getSupportedFeatureLevel(): FeatureLevel
    /** Set the active feature level (must not exceed supported level). */
    fun setActiveFeatureLevel(featureLevel: FeatureLevel): FeatureLevel
    /** Get the currently active feature level. */
    fun getActiveFeatureLevel(): FeatureLevel
    /** Enable/disable automatic GPU instancing of identical drawables. */
    fun setAutomaticInstancingEnabled(enable: Boolean)
    /** Check if automatic GPU instancing is enabled. */
    fun isAutomaticInstancingEnabled(): Boolean
    /** Get Engine configuration. */
    fun getConfig(): Config
    /** Get max stereoscopic eyes for this engine configuration. */
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
    /** Create a SwapChain from a native surface. */
    fun createSwapChain(surface: NativeSurface): SwapChain
    /** Create a SwapChain from a native surface with flags. */
    fun createSwapChain(surface: NativeSurface, flags: Long): SwapChain
    /** Create an offscreen SwapChain of specified dimensions. */
    fun createSwapChain(width: Int, height: Int, flags: Long): SwapChain
    /** Destroy a SwapChain. */
    fun destroySwapChain(swapChain: SwapChain)

    // View
    /** Create a View for rendering. */
    fun createView(): View
    /** Destroy a View. */
    fun destroyView(view: View)

    // Renderer
    /** Create a Renderer for this Engine. */
    fun createRenderer(): Renderer
    /** Destroy a Renderer. */
    fun destroyRenderer(renderer: Renderer)

    // Camera
    /** Create a Camera as a standalone component. */
    fun createCamera(): Camera
    /** Create a Camera attached to an entity. */
    fun createCamera(entity: Entity): Camera
    /** Get the Camera component attached to an entity, or null. */
    fun getCameraComponent(entity: Entity): Camera?
    /** Destroy a Camera. */
    fun destroyCamera(camera: Camera)
    /** Destroy the Camera component on an entity. */
    fun destroyCameraComponent(entity: Entity)

    // Scene
    /** Create a Scene. */
    fun createScene(): Scene
    /** Destroy a Scene. */
    fun destroyScene(scene: Scene)

    // Fence
    /** Create a Fence for GPU synchronization. */
    fun createFence(): Fence
    /** Destroy a Fence. */
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
    fun destroyEntity(entity: Entity)

    // Managers
    /** Get the TransformManager. */
    fun getTransformManager(): TransformManager
    /** Get the LightManager. */
    fun getLightManager(): LightManager
    /** Get the RenderableManager. */
    fun getRenderableManager(): RenderableManager
    /** Get the EntityManager. */
    fun getEntityManager(): EntityManager

    // Command Queue management
    /** Block until all pending GPU work completes. */
    fun flushAndWait()
    /** Block until all pending GPU work completes, with timeout in ms. Returns success. */
    fun flushAndWait(timeout: Long): Boolean
    /** Flush pending GPU commands (non-blocking). */
    fun flush()
    /** Check if rendering is paused. */
    fun isPaused(): Boolean
    /** Pause/resume rendering. */
    fun setPaused(paused: Boolean)
    /** No-op; deprecated. */
    fun unprotected()
    /** Check if a feature flag exists. */
    fun hasFeatureFlag(name: String): Boolean
    /** Set a feature flag. */
    fun setFeatureFlag(name: String, value: Boolean): Boolean
    /** Get a feature flag value. */
    fun getFeatureFlag(name: String): Boolean

    /** Enable accurate world-space translations (higher precision, more CPU cost). */
    fun enableAccurateTranslations()

    /** Material compilation priority. */
    enum class CompilerPriorityQueue { CRITICAL, HIGH, LOW }
    /** Feature state for conditional material compilation. */
    enum class FeatureState { FALSE, TRUE, INDETERMINATE }

    /** Asynchronously compile a material variant. Callback invoked on main thread when done. */
    fun compile(priority: CompilerPriorityQueue, material: Material, view: View, shadowReceiver: FeatureState, skinning: FeatureState, callback: (() -> Unit)? = null)
}

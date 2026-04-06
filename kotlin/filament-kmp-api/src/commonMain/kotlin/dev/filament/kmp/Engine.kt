package dev.filament.kmp

/**
 * Engine is filament's main entry-point.
 * <p>
 * An Engine instance main function is to keep track of all resources created by the user and
 * manage the rendering thread as well as the hardware renderer.
 * <p>
 * To use filament, an Engine instance must be created first:
 *
 * <pre>
 * import dev.filament.kmp.*
 *
 * val engine = Engine.create()
 * </pre>
 * <p>
 * Engine essentially represents (or is associated to) a hardware context
 * (e.g. an OpenGL ES context).
 * <p>
 * Rendering typically happens in an operating system's window (which can be full screen), such
 * window is managed by a [Renderer].
 * <p>
 * A typical filament render loop looks like this:
 *
 * <pre>
 * import dev.filament.kmp.*
 *
 * val engine        = Engine.create()
 * val swapChain     = engine.createSwapChain(nativeWindow)
 * val renderer      = engine.createRenderer()
 * val scene         = engine.createScene()
 * val view          = engine.createView()
 *
 * view.setScene(scene)
 *
 * do {
 *     // typically we wait for VSYNC and user input events
 *     if (renderer.beginFrame(swapChain)) {
 *         renderer.render(view)
 *         renderer.endFrame()
 *     }
 * } while (!quit)
 *
 * engine.destroyView(view)
 * engine.destroyScene(scene)
 * engine.destroyRenderer(renderer)
 * engine.destroySwapChain(swapChain)
 * engine.destroy()
 * </pre>
 *
 * <h1>Resource Tracking</h1>
 * <p>
 * Each `Engine` instance keeps track of all objects created by the user, such as vertex
 * and index buffers, lights, cameras, etc...
 * The user is expected to free those resources, however, leaked resources are freed when the
 * engine instance is destroyed and a warning is emitted in the console.
 *
 * <h1>Thread safety</h1>
 * <p>
 * An `Engine` instance is not thread-safe. The implementation makes no attempt to
 * synchronize calls to an `Engine` instance methods.
 * If multi-threading is needed, synchronization must be external.
 *
 * <h1>Multi-threading</h1>
 * <p>
 * When created, the `Engine` instance starts a render thread as well as multiple worker
 * threads, these threads have an elevated priority appropriate for rendering, based on the
 * platform's best practices. The number of worker threads depends on the platform and is
 * automatically chosen for best performance.
 * <p>
 * On platforms with asymmetric cores (e.g. ARM's Big.Little), `Engine` makes some
 * educated guesses as to which cores to use for the render thread and worker threads. For example,
 * it'll try to keep an OpenGL ES thread on a Big core.
 *
 * @see SwapChain
 * @see Renderer
 */
expect class Engine {
    /**
     * Denotes a backend
     */
    enum class Backend {
        /**
         * Automatically selects an appropriate driver for the platform.
         */
        DEFAULT,
        /**
         * Selects the OpenGL driver (which supports OpenGL ES as well).
         */
        OPENGL,
        /**
         * Selects the Vulkan driver if the platform supports it.
         */
        VULKAN,
        /**
         * Selects the Metal driver if the platform supports it.
         */
        METAL,
        /**
         * Select the WebGPU driver if platform supports it.
         */
        WEBGPU,
        /**
         * Selects the no-op driver for testing purposes.
         */
        NOOP,
    }

    /**
     * Defines the backend's feature levels.
     */
    enum class FeatureLevel {
        /** Reserved, don't use */
        FEATURE_LEVEL_0,
        /** OpenGL ES 3.0 features (default) */
        FEATURE_LEVEL_1,
        /** OpenGL ES 3.1 features + 16 textures units + cubemap arrays */
        FEATURE_LEVEL_2,
        /** OpenGL ES 3.1 features + 31 textures units + cubemap arrays */
        FEATURE_LEVEL_3,
    }

    /**
     * The type of technique for stereoscopic rendering. (Note that the materials used will need to be
     * compatible with the chosen technique.)
     */
    enum class StereoscopicType {
        /** No stereoscopic rendering. */
        NONE,
        /** Stereoscopic rendering is performed using instanced rendering technique. */
        INSTANCED,
        /** Stereoscopic rendering is performed using the multiview feature from the graphics backend. */
        MULTIVIEW,
    }

    /**
     * This controls the priority level for GPU work scheduling, which helps prioritize the
     * submitted GPU work and enables preemption.
     */
    enum class GpuContextPriority {
        /**
         * Backend default GPU context priority (typically MEDIUM)
         */
        DEFAULT,
        /**
         * For non-interactive, deferrable workloads. This should not interfere with standard
         * applications.
         */
        LOW,
        /**
         * The default priority level for standard applications.
         */
        MEDIUM,
        /**
         * For high-priority, latency-sensitive workloads that are more important than standard
         * applications.
         */
        HIGH,
        /**
         * The highest priority, intended for system-critical, real-time applications where missing
         * deadlines is unacceptable (e.g., VR/AR compositors or other system-critical tasks).
         */
        REALTIME,
    }

    /**
     * Parameters for customizing the initialization of [Engine].
     */
    class Config() {
        /**
         * Size in MiB of the low-level command buffer arena.
         */
        var commandBufferSizeMB: Long

        /**
         * Size in MiB of the per-frame data arena.
         */
        var perRenderPassArenaSizeMB: Long

        /**
         * Size in MiB of the backend's handle arena.
         */
        var driverHandleArenaSizeMB: Long

        /**
         * Minimum size in MiB of a low-level command buffer.
         */
        var minCommandBufferSizeMB: Long

        /**
         * Size in MiB of the per-frame high level command buffer.
         */
        var perFrameCommandsSizeMB: Long

        /**
         * Number of threads to use in Engine's JobSystem.
         */
        var jobSystemThreadCount: Long

        /**
         * Number of most-recently destroyed textures to track for use-after-free.
         */
        var textureUseAfterFreePoolSize: Long

        /**
         * Set to `true` to forcibly disable parallel shader compilation in the backend.
         */
        @Deprecated("use 'backend.disable_parallel_shader_compile' feature flag instead")
        var disableParallelShaderCompile: Boolean

        /**
         * The type of technique for stereoscopic rendering.
         */
        var stereoscopicType: StereoscopicType

        /**
         * The number of eyes to render when stereoscopic rendering is enabled.
         */
        var stereoscopicEyeCount: Long

        /**
         * @Deprecated This value is no longer used.
         */
        @Deprecated("This value is no longer used.")
        var resourceAllocatorCacheSizeMB: Long

        /**
         * This value determines how many frames texture entries are kept for in the cache.
         */
        var resourceAllocatorCacheMaxAge: Long

        /**
         * Disable backend handles use-after-free checks.
         */
        @Deprecated("use 'backend.disable_handle_use_after_free_check' feature flag instead")
        var disableHandleUseAfterFreeCheck: Boolean

        /**
         * Sets a preferred shader language for Filament to use.
         */
        enum class ShaderLanguage {
            DEFAULT,
            MSL,
            METAL_LIBRARY,
        }
        var preferredShaderLanguage: ShaderLanguage

        /**
         * When the OpenGL ES backend is used, setting this value to true will force a GLES2.0
         * context if supported by the Platform, or if not, will have the backend pretend
         * it's a GLES2 context. Ignored on other backends.
         */
        var forceGLES2Context: Boolean

        /**
         * Assert the native window associated to a SwapChain is valid when calling makeCurrent().
         */
        @Deprecated("use 'backend.opengl.assert_native_window_is_valid' feature flag instead")
        var assertNativeWindowIsValid: Boolean

        /**
         * GPU context priority level. Controls GPU work scheduling and preemption.
         */
        var gpuContextPriority: GpuContextPriority

        /**
         * The initial size in bytes of the shared uniform buffer used for material instance batching.
         */
        var sharedUboInitialSizeInBytes: Long
    }

    /**
     * Constructs [Engine] objects using a builder pattern.
     */
    class Builder() {
        /**
         * Sets the [Backend] for the Engine.
         */
        fun backend(backend: Backend): Builder

        /**
         * Sets a sharedContext for the Engine.
         *
         * @param sharedContext A platform-dependant OpenGL context handle.
         */
        fun sharedContext(sharedContext: Any): Builder

        /**
         * Configure the Engine with custom parameters.
         */
        fun config(config: Config): Builder

        /**
         * Sets the initial featureLevel for the Engine.
         */
        fun featureLevel(featureLevel: FeatureLevel): Builder

        /**
         * Sets the initial paused state of the rendering thread.
         * <p>Warning: This is an experimental API.
         */
        fun paused(paused: Boolean): Builder

        /**
         * Set a feature flag value. This is the only way to set constant feature flags.
         */
        fun feature(name: String, value: Boolean): Builder

        /**
         * Creates an instance of Engine
         */
        fun build(): Engine
    }

    companion object {
        /**
         * Creates an instance of Engine using the default [Backend]
         */
        fun create(): Engine

        /**
         * Creates an instance of Engine using the specified [Backend]
         */
        fun create(backend: Backend): Engine

        /**
         * Creates an instance of Engine using the specified shared context.
         */
        fun create(sharedContext: Any): Engine

        /**
         * Get the current time in nanoseconds since epoch of std::chrono::steady_clock.
         */
        fun getSteadyClockTimeNano(): Long
    }

    /**
     * @return `true` if this `Engine` is initialized properly.
     */
    fun isValid(): Boolean

    /**
     * Destroy the `Engine` instance and all associated resources.
     */
    fun destroy()

    /**
     * @return the backend used by this `Engine`
     */
    fun getBackend(): Backend

    /**
     * Helper to enable accurate translations.
     */
    fun enableAccurateTranslations()

    /**
     * Query the feature level supported by the selected backend.
     */
    fun getSupportedFeatureLevel(): FeatureLevel

    /**
     * Activate all features of a given feature level.
     */
    fun setActiveFeatureLevel(featureLevel: FeatureLevel): FeatureLevel

    /**
     * Returns the currently active feature level.
     */
    fun getActiveFeatureLevel(): FeatureLevel

    /**
     * @return active config settings.
     */
    fun getConfig(): Config

    /**
     * Returns the maximum number of stereoscopic eyes supported by Filament.
     */
    fun getMaxStereoscopicEyes(): Long

    /**
     * Enables or disables automatic instancing of render primitives.
     */
    fun setAutomaticInstancingEnabled(enable: Boolean)

    /**
     * @return true if automatic instancing is enabled, false otherwise.
     */
    fun isAutomaticInstancingEnabled(): Boolean

    // SwapChain
    fun createSwapChain(surface: Any): SwapChain
    fun createSwapChain(surface: Any, flags: Long): SwapChain
    fun createSwapChain(width: Int, height: Int, flags: Long): SwapChain
    fun createSwapChainFromNativeSurface(surface: NativeSurface, flags: Long): SwapChain
    fun destroySwapChain(swapChain: SwapChain)

    // Renderer, View, Scene, Fence, etc.
    fun createView(): View
    fun destroyView(view: View)

    fun createRenderer(): Renderer
    fun destroyRenderer(renderer: Renderer)

    fun createCamera(entity: Int): Camera
    fun getCameraComponent(entity: Int): Camera?
    fun destroyCameraComponent(entity: Int)

    fun createScene(): Scene
    fun destroyScene(scene: Scene)

    fun createFence(): Fence
    fun destroyFence(fence: Fence)

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

    // Interaction
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
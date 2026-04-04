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
 * val engine = Engine.create()
 * val swapChain = engine.createSwapChain(nativeWindow)
 * val renderer = engine.createRenderer()
 * val scene = engine.createScene()
 * val view = engine.createView()
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
 * <h1><u>Resource Tracking</u></h1>
 * <p>
 * Each <code>Engine</code> instance keeps track of all objects created by the user, such as vertex
 * and index buffers, lights, cameras, etc...
 * The user is expected to free those resources, however, leaked resources are freed when the
 * engine instance is destroyed and a warning is emitted in the console.
 *
 * <h1><u>Thread safety</u></h1>
 * <p>
 * An <code>Engine</code> instance is not thread-safe. The implementation makes no attempt to
 * synchronize calls to an <code>Engine</code> instance methods.
 * If multi-threading is needed, synchronization must be external.
 *
 * <h1><u>Multi-threading</u></h1>
 * <p>
 * When created, the <code>Engine</code> instance starts a render thread as well as multiple worker
 * threads, these threads have an elevated priority appropriate for rendering, based on the
 * platform's best practices. The number of worker threads depends on the platform and is
 * automatically chosen for best performance.
 * <p>
 * On platforms with asymmetric cores (e.g. ARM's Big.Little), <code>Engine</code> makes some
 * educated guesses as to which cores to use for the render thread and worker threads. For example,
 * it'll try to keep an OpenGL ES thread on a Big core.
 *
 * <h1><u>Swap Chains</u></h1>
 * <p>
 * A swap chain represents an Operating System's <b>native</b> renderable surface.
 * Typically it's a window or a view. Because a [SwapChain] is initialized from a native
 * object, it is given to filament as an <code>Object</code>, which must be of the proper type for
 * each platform filament is running on.
 * <p>
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
    class Config {
        /**
         * Sets a preferred shader language for Filament to use.
         */
        enum class ShaderLanguage {
            DEFAULT,
            MSL,
            METAL_LIBRARY,
        }

        /**
         * Size in MiB of the low-level command buffer arena.
         *
         * Each new command buffer is allocated from here. If this buffer is too small the program
         * might terminate or rendering errors might occur.
         *
         * This is typically set to minCommandBufferSizeMB * 3, so that up to 3 frames can be
         * batched-up at once.
         *
         * This value affects the application's memory usage.
         */
        var commandBufferSizeMB: Long

        /**
         * Size in MiB of the per-frame data arena.
         *
         * This is the main arena used for allocations when preparing a frame.
         * e.g.: Froxel data and high-level commands are allocated from this arena.
         *
         * If this size is too small, the program will abort on debug builds and have undefined
         * behavior otherwise.
         *
         * This value affects the application's memory usage.
         */
        var perRenderPassArenaSizeMB: Long

        /**
         * Size in MiB of the backend's handle arena.
         *
         * Backends will fallback to slower heap-based allocations when running out of space and
         * log this condition.
         *
         * If 0, then the default value for the given platform is used
         *
         * This value affects the application's memory usage.
         */
        var driverHandleArenaSizeMB: Long

        /**
         * Minimum size in MiB of a low-level command buffer.
         *
         * This is how much space is guaranteed to be available for low-level commands when a new
         * buffer is allocated. If this is too small, the engine might have to stall to wait for
         * more space to become available, this situation is logged.
         *
         * This value does not affect the application's memory usage.
         */
        var minCommandBufferSizeMB: Long

        /**
         * Size in MiB of the per-frame high level command buffer.
         *
         * This buffer is related to the number of draw calls achievable within a frame, if it is
         * too small, the program will abort on debug builds and have undefined behavior otherwise.
         *
         * It is allocated from the 'per-render-pass arena' above. Make sure that at least 1 MiB is
         * left in the per-render-pass arena when deciding the size of this buffer.
         *
         * This value does not affect the application's memory usage.
         */
        var perFrameCommandsSizeMB: Long

        /**
         * Number of threads to use in Engine's JobSystem.
         *
         * Engine uses a utils::JobSystem to carry out paralleization of Engine workloads. This
         * value sets the number of threads allocated for JobSystem. Configuring this value can be
         * helpful in CPU-constrained environments where too many threads can cause contention of
         * CPU and reduce performance.
         *
         * The default value is 0, which implies that the Engine will use a heuristic to determine
         * the number of threads to use.
         */
        var jobSystemThreadCount: Long

        /**
         * Number of most-recently destroyed textures to track for use-after-free.
         *
         * This will cause the backend to throw an exception when a texture is freed but still bound
         * to a SamplerGroup and used in a draw call. 0 disables completely.
         *
         * Currently only respected by the Metal backend.
         */
        var textureUseAfterFreePoolSize: Long

        /**
         * Set to `true` to forcibly disable parallel shader compilation in the backend.
         * Currently only honored by the GL backend.
         */
        @Deprecated("Use feature flag \"backend.disable_parallel_shader_compile\" instead.")
        var disableParallelShaderCompile: Boolean

        /**
         * The type of technique for stereoscopic rendering.
         *
         * This setting determines the algorithm used when stereoscopic rendering is enabled. This
         * decision applies to the entire Engine for the lifetime of the Engine. E.g., multiple
         * Views created from the Engine must use the same stereoscopic type.
         *
         * Each view can enable stereoscopic rendering via the StereoscopicOptions::enable flag.
         *
         * @see View.setStereoscopicOptions
         */
        var stereoscopicType: StereoscopicType

        /**
         * The number of eyes to render when stereoscopic rendering is enabled. Supported values are
         * between 1 and Engine#getMaxStereoscopicEyes() (inclusive).
         *
         * @see View.setStereoscopicOptions
         * @see getMaxStereoscopicEyes
         */
        var stereoscopicEyeCount: Long

        /**
         * This value is no longer used.
         */
        @Deprecated("This value is no longer used.")
        var resourceAllocatorCacheSizeMB: Long

        /**
         * This value determines how many frames texture entries are kept for in the cache. This
         * is a soft limit, meaning some texture older than this are allowed to stay in the cache.
         * Typically only one texture is evicted per frame.
         * The default is 1.
         */
        var resourceAllocatorCacheMaxAge: Long

        /**
         * Disable backend handles use-after-free checks.
         */
        @Deprecated("Use feature flag \"backend.disable_handle_use_after_free_check\" instead.")
        var disableHandleUseAfterFreeCheck: Boolean

        /**
         * Sets a preferred shader language for Filament to use.
         *
         * The Metal backend supports two shader languages: MSL (Metal Shading Language) and
         * METAL_LIBRARY (precompiled .metallib). This option controls which shader language is
         * used when materials contain both.
         *
         * By default, when preferredShaderLanguage is unset, Filament will prefer METAL_LIBRARY
         * shaders if present within a material, falling back to MSL. Setting
         * preferredShaderLanguage to ShaderLanguage::MSL will instead instruct Filament to check
         * for the presence of MSL in a material first, falling back to METAL_LIBRARY if MSL is not
         * present.
         *
         * When using a non-Metal backend, setting this has no effect.
         */
        var preferredShaderLanguage: ShaderLanguage

        /**
         * When the OpenGL ES backend is used, setting this value to true will force a GLES2.0
         * context if supported by the Platform, or if not, will have the backend pretend
         * it's a GLES2 context. Ignored on other backends.
         */
        var forceGLES2Context: Boolean

        /**
         * Assert the native window associated to a SwapChain is valid when calling makeCurrent().
         * This is only supported for:
         *      - PlatformEGLAndroid
         */
        @Deprecated("Use feature flag \"backend.opengl.assert_native_window_is_valid\" instead.")
        var assertNativeWindowIsValid: Boolean

        /**
         * GPU context priority level. Controls GPU work scheduling and preemption.
         */
        var gpuContextPriority: GpuContextPriority

        /**
         * The initial size in bytes of the shared uniform buffer used for material instance batching.
         *
         * If the buffer runs out of space during a frame, it will be automatically reallocated
         * with a larger capacity. Setting an appropriate initial size can help avoid runtime
         * reallocations, which can cause a minor performance stutter, at the cost of higher
         * initial memory usage.
         */
        var sharedUboInitialSizeInBytes: Long
    }

    /**
     * Constructs <code>Engine</code> objects using a builder pattern.
     */
    class Builder {
        /**
         * Sets the [Backend] for the Engine.
         *
         * @param backend Driver backend to use
         * @return A reference to this Builder for chaining calls.
         */
        fun backend(backend: Backend): Builder

        /**
         * Sets a sharedContext for the Engine.
         *
         * @param sharedContext  A platform-dependant OpenGL context used as a shared context
         *                       when creating filament's internal context.
         * @return A reference to this Builder for chaining calls.
         */
        fun sharedContext(sharedContext: Any): Builder

        /**
         * Configure the Engine with custom parameters.
         *
         * @param config A [Config] object
         * @return A reference to this Builder for chaining calls.
         */
        fun config(config: Config): Builder

        /**
         * Sets the initial featureLevel for the Engine.
         *
         * @param featureLevel The feature level at which initialize Filament.
         * @return A reference to this Builder for chaining calls.
         */
        fun featureLevel(featureLevel: FeatureLevel): Builder

        /**
         * Sets the initial paused state of the rendering thread.
         *
         * <p>Warning: This is an experimental API. See [Engine.setPaused] for caveats.
         *
         * @param paused Whether to start the rendering thread paused.
         * @return A reference to this Builder for chaining calls.
         */
        fun paused(paused: Boolean): Builder

        /**
         * Set a feature flag value. This is the only way to set constant feature flags.
         * @param name feature name
         * @param value true to enable, false to disable
         * @return A reference to this Builder for chaining calls.
         */
        fun feature(name: String, value: Boolean): Builder

        /**
         * Creates an instance of Engine
         *
         * @return A newly created <code>Engine</code>, or <code>null</code> if the GPU driver couldn't
         *         be initialized, for instance if it doesn't support the right version of OpenGL or
         *         OpenGL ES.
         *
         * @exception IllegalStateException can be thrown if there isn't enough memory to
         * allocate the command buffer.
         */
        fun build(): Engine
    }

    /**
     * @return <code>true</code> if this <code>Engine</code> is initialized properly.
     */
    fun isValid(): Boolean

    /**
     * Destroy the <code>Engine</code> instance and all associated resources.
     * <p>
     * This method is one of the few thread-safe methods.
     * <p>
     * [destroy] should be called last and after all other resources have been
     * destroyed, it ensures all filament resources are freed.
     * <p>
     * <code>Destroy</code> performs the following tasks:
     * <li>Destroy all internal software and hardware resources.</li>
     * <li>Free all user allocated resources that are not already destroyed and logs a warning.
     *     <p>This indicates a "leak" in the user's code.</li>
     * <li>Terminate the rendering engine's thread.</li>
     */
    fun destroy()

    /**
     * @return the backend used by this <code>Engine</code>
     */
    fun getBackend(): Backend

    /**
     * Helper to enable accurate translations.
     * If you need this Engine to handle a very large world space, one way to achieve this
     * automatically is to enable accurate translations in the TransformManager. This helper
     * provides a convenient way of doing that.
     * This is typically called once just after creating the Engine.
     */
    fun enableAccurateTranslations()

    /**
     * Query the feature level supported by the selected backend.
     *
     * A specific feature level needs to be set before the corresponding features can be used.
     *
     * @return FeatureLevel supported the selected backend.
     * @see setActiveFeatureLevel
     */
    fun getSupportedFeatureLevel(): FeatureLevel

    /**
     * Activate all features of a given feature level. If an explicit feature level is not specified
     * at Engine initialization time via [Builder.featureLevel], the default feature level is
     * [FeatureLevel.FEATURE_LEVEL_0] on devices not compatible with GLES 3.0; otherwise, the
     * default is [FeatureLevel.FEATURE_LEVEL_1]. The selected feature level must not be
     * higher than the value returned by [getActiveFeatureLevel] and it's not possible lower
     * the active feature level. Additionally, it is not possible to modify the feature level at all
     * if the Engine was initialized at [FeatureLevel.FEATURE_LEVEL_0].
     *
     * @param featureLevel the feature level to activate. If featureLevel is lower than
     * [getActiveFeatureLevel], the current (higher) feature level is kept. If
     * featureLevel is higher than [getSupportedFeatureLevel], or if the
     * engine was initialized at feature level 0, an exception is thrown, or the
     * program is terminated if exceptions are disabled.
     *
     * @return the active feature level.
     *
     * @see Builder.featureLevel
     * @see getSupportedFeatureLevel
     * @see getActiveFeatureLevel
     */
    fun setActiveFeatureLevel(featureLevel: FeatureLevel): FeatureLevel

    /**
     * Returns the currently active feature level.
     * @return currently active feature level
     * @see getSupportedFeatureLevel
     * @see setActiveFeatureLevel
     */
    fun getActiveFeatureLevel(): FeatureLevel

    /**
     * Enables or disables automatic instancing of render primitives. Instancing of render primitive
     * can greatly reduce CPU overhead but requires the instanced primitives to be identical
     * (i.e. use the same geometry) and use the same MaterialInstance. If it is known that the
     * scene doesn't contain any identical primitives, automatic instancing can have some
     * overhead and it is then best to disable it.
     *
     * Disabled by default.
     *
     * @param enable true to enable, false to disable automatic instancing.
     *
     * @see RenderableManager
     * @see MaterialInstance
     */
    fun setAutomaticInstancingEnabled(enable: Boolean)

    /**
     * @return true if automatic instancing is enabled, false otherwise.
     * @see setAutomaticInstancingEnabled
     */
    fun isAutomaticInstancingEnabled(): Boolean

    /**
     * Retrieves the configuration settings of this [Engine].
     *
     * This method returns the configuration object that was supplied to the Engine's
     * [Builder.config] method during the creation of this Engine. If the builder
     * config method was not explicitly called (or called with null), this method returns the default
     * configuration settings.
     *
     * @return a [Config] object with this Engine's configuration
     * @see Builder.config
     */
    fun getConfig(): Config

    /**
     * Returns the maximum number of stereoscopic eyes supported by Filament. The actual number of
     * eyes rendered is set at Engine creation time with the [Config.stereoscopicEyeCount]
     * setting.
     *
     * @return the max number of stereoscopic eyes supported
     * @see Config.stereoscopicEyeCount
     */
    fun getMaxStereoscopicEyes(): Long

    /**
     * Creates an opaque [SwapChain] from the given OS native window handle.
     *
     * @param surface platform-native window/surface handle
     *
     * @return a newly created [SwapChain] object
     *
     * @exception IllegalStateException can be thrown if the SwapChain couldn't be created
     */
    fun createSwapChain(surface: Any): SwapChain

    /**
     * Creates a [SwapChain] from the given OS native window handle.
     *
     * @param surface platform-native window/surface handle
     * @param flags configuration flags, see [SwapChainFlags]
     *
     * @return a newly created [SwapChain] object
     *
     * @exception IllegalStateException can be thrown if the SwapChain couldn't be created
     *
     * @see SwapChainFlags.CONFIG_DEFAULT
     * @see SwapChainFlags.CONFIG_TRANSPARENT
     * @see SwapChainFlags.CONFIG_READABLE
     */
    fun createSwapChain(surface: Any, flags: Long): SwapChain

    /**
     * Creates a headless [SwapChain]
     *
     * @param width  width of the rendering buffer
     * @param height height of the rendering buffer
     * @param flags  configuration flags, see [SwapChainFlags]
     *
     * @return a newly created [SwapChain] object
     *
     * @exception IllegalStateException can be thrown if the SwapChain couldn't be created
     *
     * @see SwapChainFlags.CONFIG_DEFAULT
     * @see SwapChainFlags.CONFIG_TRANSPARENT
     * @see SwapChainFlags.CONFIG_READABLE
     */
    fun createSwapChain(width: Int, height: Int, flags: Long): SwapChain

    /**
     * Creates a [SwapChain] from a [NativeSurface].
     *
     * @param surface a properly initialized [NativeSurface]
     * @param flags configuration flags, see [SwapChainFlags]
     *
     * @return a newly created [SwapChain] object
     *
     * @exception IllegalStateException can be thrown if the swapchain couldn't be created
     */
    fun createSwapChainFromNativeSurface(surface: NativeSurface, flags: Long): SwapChain

    /**
     * Destroys a [SwapChain] and frees all its associated resources.
     * @param swapChain the [SwapChain] to destroy
     */
    fun destroySwapChain(swapChain: SwapChain)

    /**
     * Returns whether the object is valid.
     * @param renderer Object to check for validity
     * @return returns true if the specified object is valid.
     */
    fun isValidRenderer(renderer: Renderer): Boolean

    /**
     * Returns whether the object is valid.
     * @param view Object to check for validity
     * @return returns true if the specified object is valid.
     */
    fun isValidView(view: View): Boolean

    /**
     * Returns whether the object is valid.
     * @param scene Object to check for validity
     * @return returns true if the specified object is valid.
     */
    fun isValidScene(scene: Scene): Boolean

    /**
     * Returns whether the object is valid.
     * @param fence Object to check for validity
     * @return returns true if the specified object is valid.
     */
    fun isValidFence(fence: Fence): Boolean

    /**
     * Returns whether the object is valid.
     * @param stream Object to check for validity
     * @return returns true if the specified object is valid.
     */
    fun isValidStream(stream: Stream): Boolean

    /**
     * Returns whether the object is valid.
     * @param indexBuffer Object to check for validity
     * @return returns true if the specified object is valid.
     */
    fun isValidIndexBuffer(indexBuffer: IndexBuffer): Boolean

    /**
     * Returns whether the object is valid.
     * @param vertexBuffer Object to check for validity
     * @return returns true if the specified object is valid.
     */
    fun isValidVertexBuffer(vertexBuffer: VertexBuffer): Boolean

    /**
     * Returns whether the object is valid.
     * @param skinningBuffer Object to check for validity
     * @return returns true if the specified object is valid.
     */
    fun isValidSkinningBuffer(skinningBuffer: SkinningBuffer): Boolean

    /**
     * Returns whether the object is valid.
     * @param morphTargetBuffer Object to check for validity
     * @return returns true if the specified object is valid.
     */
    fun isValidMorphTargetBuffer(morphTargetBuffer: MorphTargetBuffer): Boolean

    /**
     * Returns whether the object is valid.
     * @param indirectLight Object to check for validity
     * @return returns true if the specified object is valid.
     */
    fun isValidIndirectLight(indirectLight: IndirectLight): Boolean

    /**
     * Returns whether the object is valid.
     * @param material Object to check for validity
     * @return returns true if the specified object is valid.
     */
    fun isValidMaterial(material: Material): Boolean

    /**
     * Returns whether the object is valid.
     * @param material Material
     * @param materialInstance MaterialInstance to check for validity
     * @return returns true if the specified object is valid.
     */
    fun isValidMaterialInstance(material: Material, materialInstance: MaterialInstance): Boolean

    /**
     * Returns whether the object is valid.
     * @param materialInstance Object to check for validity
     * @return returns true if the specified object is valid.
     */
    fun isValidExpensiveMaterialInstance(materialInstance: MaterialInstance): Boolean

    /**
     * Returns whether the object is valid.
     * @param skybox Object to check for validity
     * @return returns true if the specified object is valid.
     */
    fun isValidSkybox(skybox: Skybox): Boolean

    /**
     * Returns whether the object is valid.
     * @param colorGrading Object to check for validity
     * @return returns true if the specified object is valid.
     */
    fun isValidColorGrading(colorGrading: ColorGrading): Boolean

    /**
     * Returns whether the object is valid.
     * @param texture Object to check for validity
     * @return returns true if the specified object is valid.
     */
    fun isValidTexture(texture: Texture): Boolean

    /**
     * Returns whether the object is valid.
     * @param renderTarget Object to check for validity
     * @return returns true if the specified object is valid.
     */
    fun isValidRenderTarget(renderTarget: RenderTarget): Boolean

    /**
     * Returns whether the object is valid.
     * @param swapChain Object to check for validity
     * @return returns true if the specified object is valid.
     */
    fun isValidSwapChain(swapChain: SwapChain): Boolean

    /**
     * Creates a [View].
     * @return a newly created [View]
     * @exception IllegalStateException can be thrown if the [View] couldn't be created
     */
    fun createView(): View

    /**
     * Destroys a [View] and frees all its associated resources.
     * @param view the [View] to destroy
     */
    fun destroyView(view: View)

    /**
     * Creates a [Renderer].
     * @return a newly created [Renderer]
     * @exception IllegalStateException can be thrown if the [Renderer] couldn't be created
     */
    fun createRenderer(): Renderer

    /**
     * Destroys a [Renderer] and frees all its associated resources.
     * @param renderer the [Renderer] to destroy
     */
    fun destroyRenderer(renderer: Renderer)

    /**
     * Creates and adds a [Camera] component to a given <code>entity</code>.
     *
     * @param entity <code>entity</code> to add the camera component to
     * @return A newly created [Camera]
     * @exception IllegalStateException can be thrown if the [Camera] couldn't be created
     */
    fun createCamera(@Entity entity: Int): Camera

    /**
     * Returns the Camera component of the given <code>entity</code>.
     *
     * @param entity An <code>entity</code>.
     * @return the Camera component for this entity or null if the entity doesn't have a Camera
     *         component
     */
    fun getCameraComponent(@Entity entity: Int): Camera?

    /**
     * Destroys the [Camera] component associated with the given entity.
     *
     * @param entity an entity
     */
    fun destroyCameraComponent(@Entity entity: Int)

    /**
     * Creates a [Scene].
     * @return a newly created [Scene]
     * @exception IllegalStateException can be thrown if the [Scene] couldn't be created
     */
    fun createScene(): Scene

    /**
     * Destroys a [Scene] and frees all its associated resources.
     * @param scene the [Scene] to destroy
     */
    fun destroyScene(scene: Scene)

    /**
     * Destroys a [Stream] and frees all its associated resources.
     * @param stream the [Stream] to destroy
     */
    fun destroyStream(stream: Stream)

    /**
     * Creates a [Fence].
     * @return a newly created [Fence]
     * @exception IllegalStateException can be thrown if the [Fence] couldn't be created
     */
    fun createFence(): Fence

    /**
     * Destroys a [Fence] and frees all its associated resources.
     * @param fence the [Fence] to destroy
     */
    fun destroyFence(fence: Fence)

    /**
     * Destroys a [IndexBuffer] and frees all its associated resources.
     * @param indexBuffer the [IndexBuffer] to destroy
     */
    fun destroyIndexBuffer(indexBuffer: IndexBuffer)

    /**
     * Destroys a [VertexBuffer] and frees all its associated resources.
     * @param vertexBuffer the [VertexBuffer] to destroy
     */
    fun destroyVertexBuffer(vertexBuffer: VertexBuffer)

    /**
     * Destroys a [SkinningBuffer] and frees all its associated resources.
     * @param skinningBuffer the [SkinningBuffer] to destroy
     */
    fun destroySkinningBuffer(skinningBuffer: SkinningBuffer)

    /**
     * Destroys a [MorphTargetBuffer] and frees all its associated resources.
     * @param morphTargetBuffer the [MorphTargetBuffer] to destroy
     */
    fun destroyMorphTargetBuffer(morphTargetBuffer: MorphTargetBuffer)

    /**
     * Destroys a [IndirectLight] and frees all its associated resources.
     * @param indirectLight the [IndirectLight] to destroy
     */
    fun destroyIndirectLight(indirectLight: IndirectLight)

    /**
     * Destroys a [Material] and frees all its associated resources.
     * <p>
     * All [MaterialInstance] of the specified [Material] must be destroyed before
     * destroying it; if some [MaterialInstance] remain, this method fails silently.
     *
     * @param material the [Material] to destroy
     */
    fun destroyMaterial(material: Material)

    /**
     * Destroys a [MaterialInstance] and frees all its associated resources.
     * @param materialInstance the [MaterialInstance] to destroy
     */
    fun destroyMaterialInstance(materialInstance: MaterialInstance)

    /**
     * Destroys a [Skybox] and frees all its associated resources.
     * @param skybox the [Skybox] to destroy
     */
    fun destroySkybox(skybox: Skybox)

    /**
     * Destroys a [ColorGrading] and frees all its associated resources.
     * @param colorGrading the [ColorGrading] to destroy
     */
    fun destroyColorGrading(colorGrading: ColorGrading)

    /**
     * Destroys a [Texture] and frees all its associated resources.
     * @param texture the [Texture] to destroy
     */
    fun destroyTexture(texture: Texture)

    /**
     * Destroys a [RenderTarget] and frees all its associated resources.
     * @param target the [RenderTarget] to destroy
     */
    fun destroyRenderTarget(target: RenderTarget)

    /**
     * Destroys all Filament-known components from this <code>entity</code>.
     * <p>
     * This method destroys Filament components only, not the <code>entity</code> itself. To destroy
     * the <code>entity</code> use <code>EntityManager#destroy</code>.
     *
     * It is recommended to destroy components individually before destroying their
     * <code>entity</code>, this gives more control as to when the destruction really happens.
     * Otherwise, orphaned components are garbage collected, which can happen at a later time.
     * Even when component are garbage collected, the destruction of their <code>entity</code>
     * terminates their participation immediately.
     *
     * @param entity the <code>entity</code> to destroy
     */
    fun destroyEntity(@Entity entity: Int)

    /**
     * @return the [TransformManager] used by this [Engine]
     */
    fun getTransformManager(): TransformManager

    /**
     * @return the [LightManager] used by this [Engine]
     */
    fun getLightManager(): LightManager

    /**
     * @return the [RenderableManager] used by this [Engine]
     */
    fun getRenderableManager(): RenderableManager

    /**
     * @return the [EntityManager] used by this [Engine]
     */
    fun getEntityManager(): EntityManager

    /**
     * Kicks the hardware thread (e.g.: the OpenGL, Vulkan or Metal thread) and blocks until
     * all commands to this point are executed. Note that this does guarantee that the
     * hardware is actually finished.
     *
     * <p>This is typically used right after destroying the <code>SwapChain</code>,
     * in cases where a guarantee about the SwapChain destruction is needed in a timely fashion.</p>
     */
    fun flushAndWait()

    /**
     * Kicks the hardware thread (e.g. the OpenGL, Vulkan or Metal thread) and blocks until
     * all commands to this point are executed. Note that does guarantee that the
     * hardware is actually finished.
     *
     * A timeout can be specified, if for some reason this flushAndWait doesn't complete before the timeout, it will
     * return false, true otherwise.
     *
     * @param timeout A timeout in nanoseconds
     * @return true if successful, false if flushAndWait timed out.
     */
    fun flushAndWait(timeout: Long): Boolean

    /**
     * Kicks the hardware thread (e.g. the OpenGL, Vulkan or Metal thread) but does not wait
     * for commands to be either executed or the hardware finished.
     *
     * <p>This is typically used after creating a lot of objects to start draining the command
     * queue which has a limited size.</p>
     */
    fun flush()

    /**
     * Get paused state of rendering thread.
     *
     * <p>Warning: This is an experimental API.
     *
     * @see setPaused
     */
    fun isPaused(): Boolean

    /**
     * Pause or resume the rendering thread.
     *
     * <p>Warning: This is an experimental API. In particular, note the following caveats.
     *
     * <ul><li>
     * Buffer callbacks will never be called as long as the rendering thread is paused.
     * Do not rely on a buffer callback to unpause the thread.
     * </li><li>
     * While the rendering thread is paused, rendering commands will continue to be queued until the
     * buffer limit is reached. When the limit is reached, the program will abort.
     * </li></ul>
     */
    fun setPaused(paused: Boolean)

    /**
     * Switch the command queue to unprotected mode. Protected mode can be activated via
     * Renderer::beginFrame() using a protected SwapChain.
     * @see Renderer
     * @see SwapChain
     */
    fun unprotected()

    /**
     * Checks if a feature flag exists
     * @param name name of the feature flag to check
     * @return true if it exists false otherwise
     */
    fun hasFeatureFlag(name: String): Boolean

    /**
     * Set the value of a non-constant feature flag.
     * @param name name of the feature flag to set
     * @param value value to set
     * @return true if the value was set, false if the feature flag is constant or doesn't exist.
     */
    fun setFeatureFlag(name: String, value: Boolean): Boolean

    /**
     * Retrieves the value of any feature flag.
     * @param name name of the feature flag
     * @return the value of the flag if it exists
     * @exception IllegalArgumentException is thrown if the feature flag doesn't exist
     */
    fun getFeatureFlag(name: String): Boolean

    fun getNativeObject(): Long

    fun getNativeJobSystem(): Long

    companion object {
        /**
         * Creates an instance of Engine using the default [Backend].
         * <p>
         * This method is one of the few thread-safe methods.
         */
        fun create(): Engine

        /**
         * Creates an instance of Engine using the specified [Backend].
         * <p>
         * This method is one of the few thread-safe methods.
         */
        fun create(backend: Backend): Engine

        /**
         * Creates an instance of Engine using [Backend.OPENGL] and a shared OpenGL context.
         * <p>
         * This method is one of the few thread-safe methods.
         */
        fun create(sharedContext: Any): Engine

        /**
         * Get the current time. This is a convenience function that simply returns the
         * time in nanosecond since epoch of std::chrono::steady_clock.
         * @return current time in nanosecond since epoch of std::chrono::steady_clock.
         * @see Renderer.beginFrame
         */
        fun getSteadyClockTimeNano(): Long
    }
}
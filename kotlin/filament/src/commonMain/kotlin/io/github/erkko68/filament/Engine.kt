package io.github.erkko68.filament

import io.github.erkko68.filament.interop.*

/**
 * Engine is Filament's main entry-point.
 *
 * An Engine instance keeps track of all resources created by the user and manages the
 * rendering thread as well as the hardware renderer.
 *
 * To use Filament, an Engine instance must be created first using `Engine.create()`.
 * Engine essentially represents (or is associated with) a hardware context (e.g., an OpenGL ES
 * context or a Vulkan device).
 *
 * Rendering typically happens in an operating system's window (which can be fullscreen), which is
 * managed by a Renderer.
 *
 * A typical Filament render loop looks like this:
 *
 * ```
 * val engine = Engine.create()
 * val swapChain = engine.createSwapChain(nativeWindow)
 * val renderer = engine.createRenderer()
 * val scene = engine.createScene()
 * val view = engine.createView()
 *
 * view.setScene(scene)
 *
 * while (!quit) {
 *     // Wait for VSYNC and user input events
 *     if (renderer.beginFrame(swapChain)) {
 *         renderer.render(view)
 *         renderer.endFrame()
 *     }
 * }
 *
 * engine.destroy(view)
 * engine.destroy(scene)
 * engine.destroy(renderer)
 * engine.destroy(swapChain)
 * Engine.destroy(engine)
 * ```
 */
class Engine internal constructor(
    internal var nativeHandle: NativePointer,
    /** What the platform set up around this engine (web: its WebGL context and canvas). */
    internal val platform: EnginePlatform,
) : AutoCloseable {
    @InternalFilamentApi
    constructor(nativeHandle: NativePointer) : this(nativeHandle, NoEnginePlatform)

    /** The native object, for interop with code calling the Fila* C API directly. Read-only: this wrapper owns it. */
    @InternalFilamentApi
    val nativeObject: NativePointer get() = nativeHandle

    private val mTransformManager by lazy { TransformManager(FilaEngine_getTransformManager(nativeHandle)) }
    private val mLightManager by lazy { LightManager(FilaEngine_getLightManager(nativeHandle)) }
    private val mRenderableManager by lazy { RenderableManager(FilaEngine_getRenderableManager(nativeHandle)) }
    private val mEntityManager by lazy { EntityManager(FilaEngine_getEntityManager(nativeHandle)) }
    // The C wrapper has no getConfig, so Builder.build() hands us the Config it was given.
    internal var mConfig: Config? = null

    /**
     * Rendering backend selection.
     */
    enum class Backend {
        /** Platform's optimal choice (usually Vulkan or Metal) */
        DEFAULT,
        /** OpenGL ES */
        OPENGL,
        /** Vulkan */
        VULKAN,
        /** Metal (iOS/macOS) */
        METAL,
        /** WebGPU */
        WEBGPU,
        /** No-op backend for testing */
        NOOP,
    }

    /**
     * Backend feature levels control available rendering capabilities and performance characteristics.
     *
     * Higher feature levels provide more capabilities but require more powerful hardware.
     */
    enum class FeatureLevel {
        /**
         * Minimum feature set; OpenGL ES 2 compatible.
         * No post-processing, limited lighting models, minimal texture formats.
         */
        FEATURE_LEVEL_0,
        /** Metal-level feature set; good for mid-range devices. */
        FEATURE_LEVEL_1,
        /** Full feature set with all capabilities. */
        FEATURE_LEVEL_2,
        /** Advanced features beyond the standard feature set. */
        FEATURE_LEVEL_3,
    }

    /**
     * Stereoscopic rendering technique for VR and 3D displays.
     */
    enum class StereoscopicType {
        /** No stereoscopic rendering (monoscopic). */
        NONE,
        /** Instanced stereo rendering (two draw calls, one per eye). */
        INSTANCED,
        /** Multiview stereo rendering (single draw call using instancing, faster). */
        MULTIVIEW,
    }

    /**
     * GPU context priority for work scheduling and preemption.
     *
     * Used to hint the GPU driver about the priority of this context's work.
     */
    enum class GpuContextPriority {
        /** Default priority. */
        DEFAULT,
        /** Low priority; can be preempted by other work. */
        LOW,
        /** Medium priority. */
        MEDIUM,
        /** High priority. */
        HIGH,
        /** Real-time priority; minimal preemption. */
        REALTIME,
    }

    /**
     * Advanced parameters for customizing Engine initialization.
     *
     * These settings control memory allocation, threading, and rendering behavior.
     */
    class Config() {
        /** Size of the command buffer in MB (default depends on backend). */
        var commandBufferSizeMB: Long = 3 * 1
        /** Per-render-pass arena size in MB. */
        var perRenderPassArenaSizeMB: Long = 3
        /** Driver handle arena size in MB. */
        var driverHandleArenaSizeMB: Long = 0
        /** Minimum command buffer size in MB. */
        var minCommandBufferSizeMB: Long = 1
        /** Size of per-frame commands in MB. */
        var perFrameCommandsSizeMB: Long = 2
        /** Number of threads for the job system (0 = CPU count). */
        var jobSystemThreadCount: Long = 0
        /** Disable backend parallel shader compilation, forcing serial compilation. */
        var disableParallelShaderCompile: Boolean = false
        /** Stereoscopic rendering technique to use. */
        var stereoscopicType: StereoscopicType = StereoscopicType.NONE
        /** Number of stereoscopic eyes (usually 2 for VR). */
        var stereoscopicEyeCount: Long = 2
        /** Size of the resource allocator cache in MB. */
        var resourceAllocatorCacheSizeMB: Long = 64
        /** Maximum age of cached resources (in frames). */
        var resourceAllocatorCacheMaxAge: Long = 1
        /** Disable the debug check that catches use of a destroyed backend handle. */
        var disableHandleUseAfterFreeCheck: Boolean = false

        /**
         * Preferred shader language for platform.
         */
        enum class ShaderLanguage {
            /** Use platform default. */
            DEFAULT,
            /** Metal Shading Language (Apple). */
            MSL,
            /** Pre-compiled Metal library. */
            METAL_LIBRARY,
        }
        /** Preferred shader language to use. */
        var preferredShaderLanguage: ShaderLanguage = ShaderLanguage.DEFAULT
        /** Force OpenGL ES 2.0 context (if applicable). */
        var forceGLES2Context: Boolean = false
        /** Assert that the native window handed to `createSwapChain` is valid. */
        var assertNativeWindowIsValid: Boolean = false
        /** GPU context priority hint for the driver. */
        var gpuContextPriority: GpuContextPriority = GpuContextPriority.DEFAULT
        /** Initial size of shared uniform buffer objects in bytes. */
        var sharedUboInitialSizeInBytes: Long = 256 * 64
        /**
         * Evaluate up to four directional lights beyond the dominant one. The extra lights
         * cast no shadows and draw no sun disc. Default: false.
         */
        var enableMultipleDirectionalLights: Boolean = false

        internal fun applyTo(builder: NativePointer) = FilaEngineBuilder_config(
            builder,
            commandBufferSizeMB.toInt(), perRenderPassArenaSizeMB.toInt(), driverHandleArenaSizeMB.toInt(),
            minCommandBufferSizeMB.toInt(), perFrameCommandsSizeMB.toInt(), jobSystemThreadCount.toInt(),
            disableParallelShaderCompile, stereoscopicType.ordinal, stereoscopicEyeCount.toInt(),
            resourceAllocatorCacheSizeMB.toInt(), resourceAllocatorCacheMaxAge.toInt(), disableHandleUseAfterFreeCheck,
            preferredShaderLanguage.ordinal, forceGLES2Context, assertNativeWindowIsValid, gpuContextPriority.ordinal,
            sharedUboInitialSizeInBytes.toInt(), enableMultipleDirectionalLights,
        )
    }

    /**
     * Builder for creating and configuring an Engine instance.
     */
    class Builder() {
        private val nativeBuilder = FilaEngineBuilder_create()
        private var mConfig: Config? = null
        private var backend = Backend.DEFAULT
        private var sharedContext: Any? = null

        /**
         * Set the rendering backend to use.
         *
         * @param backend The backend to use (DEFAULT lets the system choose).
         * @return This Builder, for chaining calls.
         */
        fun backend(backend: Backend): Builder {
            this.backend = backend
            FilaEngineBuilder_backend(nativeBuilder, backend.ordinal)
            return this
        }

        /**
         * Share a platform-specific rendering context with the Engine.
         *
         * This is useful for rendering to multiple windows or integrating with
         * existing rendering systems.
         *
         * @param sharedContext Platform-specific context object (e.g., EGLContext on Android).
         * @return This Builder, for chaining calls.
         */
        fun sharedContext(sharedContext: Any): Builder {
            this.sharedContext = sharedContext
            val pointer = sharedContextPointer(sharedContext)
            if (pointer != NullPointer) FilaEngineBuilder_sharedContext(nativeBuilder, pointer)
            return this
        }

        /**
         * Set advanced Engine configuration options.
         *
         * @param config Configuration object with memory and threading settings.
         * @return This Builder, for chaining calls.
         */
        fun config(config: Config): Builder {
            config.applyTo(nativeBuilder)
            mConfig = config
            return this
        }

        /**
         * Set the feature level to use.
         *
         * The effective feature level is the minimum of this value and the backend's maximum.
         *
         * @param featureLevel Desired feature level.
         * @return This Builder, for chaining calls.
         */
        fun featureLevel(featureLevel: FeatureLevel): Builder {
            FilaEngineBuilder_featureLevel(nativeBuilder, featureLevel.ordinal)
            return this
        }

        /**
         * Pause rendering immediately after Engine creation.
         *
         * Set `engine.isPaused = false` to resume.
         *
         * @param paused true to start paused, false to start active.
         * @return This Builder, for chaining calls.
         */
        fun paused(paused: Boolean): Builder {
            FilaEngineBuilder_paused(nativeBuilder, paused)
            return this
        }

        /**
         * Enable or disable a feature flag.
         *
         * @param name Feature flag name.
         * @param value true to enable, false to disable.
         * @return This Builder, for chaining calls.
         */
        fun feature(name: String, value: Boolean): Builder {
            name.useCString { FilaEngineBuilder_feature(nativeBuilder, it, value) }
            return this
        }

        /**
         * Set the default color grading configuration.
         *
         * @param colorGrading ColorGrading.Builder with default configuration.
         * @return This Builder, for chaining calls.
         */
        fun colorGrading(colorGrading: ColorGrading.Builder): Builder {
            FilaEngineBuilder_colorGrading(nativeBuilder, colorGrading.nativeHandle)
            return this
        }

        /**
         * Creates the Engine instance.
         *
         * @return The newly created Engine.
         */
        fun build(): Engine {
            val platform = enginePlatform(backend, sharedContext)
            val handle = FilaEngineBuilder_build(nativeBuilder)
            FilaEngineBuilder_destroy(nativeBuilder)
            if (handle == NullPointer) platform.release()
            check(handle != NullPointer) { "Failed to build Engine" }
            return Engine(handle, platform).apply { mConfig = this@Builder.mConfig }
        }
    }

    companion object {
        /**
         * Create an Engine with the platform's optimal backend (usually Vulkan or Metal).
         *
         * @return A new Engine instance using the default backend.
         */
        fun create(): Engine = Builder().build()
        /**
         * Create an Engine with a specific rendering backend.
         *
         * @param backend The backend to use (OPENGL, VULKAN, METAL, WEBGPU, or NOOP).
         * @return A new Engine instance using the specified backend.
         */
        fun create(backend: Backend): Engine = Builder().backend(backend).build()
        /**
         * Create an Engine sharing a platform-specific rendering context.
         *
         * This allows multiple Engine instances or integration with existing rendering contexts.
         *
         * @param sharedContext Platform-specific context (e.g., EGLContext on Android).
         * @return A new Engine instance sharing the given context.
         */
        fun create(sharedContext: Any): Engine = Builder().sharedContext(sharedContext).build()
        /**
         * Get the current steady clock time in nanoseconds.
         *
         * This is useful for frame timing and synchronization with Engine's frame pacing.
         *
         * @return Current time in nanoseconds since an unspecified epoch.
         */
        val steadyClockTimeNano: Long
            get() = LongArray(1).also { out -> out.usePinned { FilaEngine_getSteadyClockTimeNano(it) } }[0]
    }

    /**
     * Check if this Engine is still valid (not destroyed).
     *
     * @return true if the Engine is valid and can be used, false if destroyed.
     */
    val isValid: Boolean get() = nativeHandle != NullPointer
    /** Same as [destroy]; lets this be used with `use { }` and try-with-resources. */
    override fun close() = destroy()

    /**
     * Destroy the Engine and all its resources.
     *
     * This is a blocking operation. All Renderer, View, Scene, and other resources
     * should ideally be destroyed first, though the Engine will clean up remaining resources.
     */
    fun destroy() {
        if (nativeHandle == NullPointer) return
        platform.makeCurrent()
        FilaEngine_destroy(nativeHandle)
        nativeHandle = NullPointer
        platform.release()
    }

    /**
     * The rendering backend being used by this Engine.
     */
    val backend: Backend get() = Backend.entries[FilaEngine_getBackend(nativeHandle)]
    /**
     * The highest feature level supported by this backend.
     */
    val supportedFeatureLevel: FeatureLevel get() = FeatureLevel.entries[FilaEngine_getSupportedFeatureLevel(nativeHandle)]
    /**
     * The active feature level.
     *
     * Assigning a level that exceeds [supportedFeatureLevel] clamps it; read the property back
     * to see what was actually set.
     */
    var activeFeatureLevel: FeatureLevel
        get() = FeatureLevel.entries[FilaEngine_getActiveFeatureLevel(nativeHandle)]
        set(value) { FilaEngine_setActiveFeatureLevel(nativeHandle, value.ordinal) }

    /**
     * Whether the engine automatically batches identical renderables to reduce draw calls.
     */
    var isAutomaticInstancingEnabled: Boolean
        get() = FilaEngine_isAutomaticInstancingEnabled(nativeHandle)
        set(value) { FilaEngine_setAutomaticInstancingEnabled(nativeHandle, value) }
    /**
     * The Engine's advanced configuration — the Config object used when creating this Engine.
     * On JVM and iOS the C wrapper has no getConfig, so this is the Config the [Builder] was
     * handed; mutating that object after [Builder.build] changes what is reported here, not the
     * Engine.
     */
    val config: Config get() = mConfig ?: Config()
    /**
     * Get the maximum number of stereoscopic eyes configured for this Engine.
     *
     * @return Number of eyes (typically 2 for VR, 1 for monoscopic).
     */
    val maxStereoscopicEyes: Long get() = FilaEngine_getMaxStereoscopicEyes(nativeHandle).toLong()

    /**
     * Validate a Renderer object created by this Engine.
     *
     * @param renderer Renderer to check.
     * @return true if the Renderer is valid and owned by this Engine.
     */
    fun isValidRenderer(renderer: Renderer): Boolean = FilaEngine_isValidRenderer(nativeHandle, renderer.nativeHandle)
    /** Validate a View. @return true if valid. */
    fun isValidView(view: View): Boolean = FilaEngine_isValidView(nativeHandle, view.nativeHandle)
    /** Validate a Scene. @return true if valid. */
    fun isValidScene(scene: Scene): Boolean = FilaEngine_isValidScene(nativeHandle, scene.nativeHandle)
    /** Validate a Fence. @return true if valid. @throws UnsupportedOperationException on JS — Fence is unbound on web. */
    fun isValidFence(fence: Fence): Boolean = FilaEngine_isValidFence(nativeHandle, fence.nativeHandle)
    /** Validate an IndexBuffer. @return true if valid. */
    fun isValidIndexBuffer(indexBuffer: IndexBuffer): Boolean = FilaEngine_isValidIndexBuffer(nativeHandle, indexBuffer.nativeHandle)
    /** Validate a VertexBuffer. @return true if valid. */
    fun isValidVertexBuffer(vertexBuffer: VertexBuffer): Boolean = FilaEngine_isValidVertexBuffer(nativeHandle, vertexBuffer.nativeHandle)
    /** Validate a SkinningBuffer. @return true if valid. @throws UnsupportedOperationException on JS — SkinningBuffer is unbound on web. */
    fun isValidSkinningBuffer(skinningBuffer: SkinningBuffer): Boolean = FilaEngine_isValidSkinningBuffer(nativeHandle, skinningBuffer.nativeHandle)
    /** Validate a MorphTargetBuffer. @return true if valid. @throws UnsupportedOperationException on JS — MorphTargetBuffer is unbound on web. */
    fun isValidMorphTargetBuffer(morphTargetBuffer: MorphTargetBuffer): Boolean = FilaEngine_isValidMorphTargetBuffer(nativeHandle, morphTargetBuffer.nativeHandle)
    /** Validate an IndirectLight. @return true if valid. */
    fun isValidIndirectLight(ibl: IndirectLight): Boolean = FilaEngine_isValidIndirectLight(nativeHandle, ibl.nativeHandle)
    /** Validate a Material. @return true if valid. */
    fun isValidMaterial(material: Material): Boolean = FilaEngine_isValidMaterial(nativeHandle, material.nativeHandle)
    /** Validate a MaterialInstance for a given Material. @return true if valid. */
    fun isValidMaterialInstance(material: Material, materialInstance: MaterialInstance): Boolean = FilaEngine_isValidMaterialInstance(nativeHandle, material.nativeHandle, materialInstance.nativeHandle)
    /** Validate a MaterialInstance (more expensive check). @return true if valid. */
    fun isValidExpensiveMaterialInstance(materialInstance: MaterialInstance): Boolean = FilaEngine_isValidExpensiveMaterialInstance(nativeHandle, materialInstance.nativeHandle)
    /** Validate a Skybox. @return true if valid. */
    fun isValidSkybox(skybox: Skybox): Boolean = FilaEngine_isValidSkybox(nativeHandle, skybox.nativeHandle)
    /** Validate ColorGrading. @return true if valid. */
    fun isValidColorGrading(colorGrading: ColorGrading): Boolean = FilaEngine_isValidColorGrading(nativeHandle, colorGrading.nativeHandle)
    /** Validate a Texture. @return true if valid. */
    fun isValidTexture(texture: Texture): Boolean = FilaEngine_isValidTexture(nativeHandle, texture.nativeHandle)
    /** Validate a RenderTarget. @return true if valid. */
    fun isValidRenderTarget(renderTarget: RenderTarget): Boolean = FilaEngine_isValidRenderTarget(nativeHandle, renderTarget.nativeHandle)
    /** Validate a Stream. @return true if valid. @throws UnsupportedOperationException on JS — Stream is unbound on web. */
    fun isValidStream(stream: Stream): Boolean = FilaEngine_isValidStream(nativeHandle, stream.nativeHandle)
    /** Validate a SwapChain. @return true if valid. */
    fun isValidSwapChain(swapChain: SwapChain): Boolean = FilaEngine_isValidSwapChain(nativeHandle, swapChain.nativeHandle)

    /** Create a SwapChain from a native display surface. */
    fun createSwapChain(surface: NativeSurface): SwapChain = createSwapChain(surface, 0L)
    /** Create a SwapChain from a native display surface with flags. */
    fun createSwapChain(surface: NativeSurface, flags: Long): SwapChain {
        val window = acquireWindow(surface)
        return SwapChain(FilaEngine_createSwapChain(nativeHandle, window, flags), window)
    }
    /** Create an offscreen SwapChain of specified dimensions. */
    fun createSwapChain(width: Int, height: Int, flags: Long): SwapChain = SwapChain(FilaEngine_createSwapChainHeadless(nativeHandle, width, height, flags))
    /** Destroy a SwapChain. */
    fun destroySwapChain(swapChain: SwapChain) {
        FilaEngine_destroySwapChain(nativeHandle, swapChain.nativeHandle)
        swapChain.nativeHandle = NullPointer
        swapChain.releaseCallbackStubs()
        releaseWindow(swapChain.window)
        swapChain.window = NullPointer
    }

    /** Create a View for rendering. */
    fun createView(): View = View(FilaEngine_createView(nativeHandle))
    /** Destroy a View. */
    fun destroyView(view: View) {
        FilaEngine_destroyView(nativeHandle, view.nativeHandle)
        view.nativeHandle = NullPointer
    }

    /** Create a Renderer associated with this Engine. */
    fun createRenderer(): Renderer = Renderer(FilaEngine_createRenderer(nativeHandle)).setEngine(this)
    /** Destroy a Renderer. */
    fun destroyRenderer(renderer: Renderer) {
        FilaEngine_destroyRenderer(nativeHandle, renderer.nativeHandle)
        renderer.nativeHandle = NullPointer
    }

    /** Create a Camera as a standalone component. */
    fun createCamera(): Camera {
        val handle = FilaEngine_createCameraAuto(nativeHandle)
        val entity = FilaCamera_getEntity(handle)
        return Camera(handle, entity)
    }
    /** Create a Camera attached to an entity. */
    fun createCamera(entity: Entity): Camera = Camera(FilaEngine_createCamera(nativeHandle, entity), entity)
    /** Get the Camera component attached to an entity, or null if not present. */
    fun getCameraComponent(entity: Entity): Camera? {
        val handle = FilaEngine_getCameraComponent(nativeHandle, entity)
        return if (handle != NullPointer) Camera(handle, entity) else null
    }
    /** Destroy a Camera. */
    fun destroyCamera(camera: Camera) {
        FilaEngine_destroyCamera(nativeHandle, camera.nativeHandle)
        camera.nativeHandle = NullPointer
    }
    /** Destroy the Camera component on an entity. */
    fun destroyCameraComponent(entity: Entity) = FilaEngine_destroyCameraComponent(nativeHandle, entity)

    /** Create a Scene for collecting renderable objects. */
    fun createScene(): Scene = Scene(FilaEngine_createScene(nativeHandle))
    /** Destroy a Scene. */
    fun destroyScene(scene: Scene) {
        FilaEngine_destroyScene(nativeHandle, scene.nativeHandle)
        scene.nativeHandle = NullPointer
    }

    /** Create a Fence for GPU synchronization. @throws UnsupportedOperationException on JS — fences are unbound on web. */
    fun createFence(): Fence = Fence(FilaEngine_createFence(nativeHandle), nativeHandle)
    /** Destroy a Fence. */
    fun destroyFence(fence: Fence) {
        FilaEngine_destroyFence(nativeHandle, fence.nativeHandle)
        fence.nativeHandle = NullPointer
    }

    /** Destroy an IndexBuffer. */
    fun destroyIndexBuffer(indexBuffer: IndexBuffer) {
        FilaEngine_destroyIndexBuffer(nativeHandle, indexBuffer.nativeHandle)
        indexBuffer.nativeHandle = NullPointer
    }
    /** Destroy a VertexBuffer. */
    fun destroyVertexBuffer(vertexBuffer: VertexBuffer) {
        FilaEngine_destroyVertexBuffer(nativeHandle, vertexBuffer.nativeHandle)
        vertexBuffer.nativeHandle = NullPointer
    }
    /** Destroy a SkinningBuffer. */
    fun destroySkinningBuffer(skinningBuffer: SkinningBuffer) {
        FilaEngine_destroySkinningBuffer(nativeHandle, skinningBuffer.nativeHandle)
        skinningBuffer.nativeHandle = NullPointer
    }
    /** Destroy a MorphTargetBuffer. */
    fun destroyMorphTargetBuffer(morphTargetBuffer: MorphTargetBuffer) {
        FilaEngine_destroyMorphTargetBuffer(nativeHandle, morphTargetBuffer.nativeHandle)
        morphTargetBuffer.nativeHandle = NullPointer
    }
    /** Destroy an IndirectLight. */
    fun destroyIndirectLight(ibl: IndirectLight) {
        FilaEngine_destroyIndirectLight(nativeHandle, ibl.nativeHandle)
        ibl.nativeHandle = NullPointer
    }
    /** Destroy a Material. */
    fun destroyMaterial(material: Material) {
        FilaEngine_destroyMaterial(nativeHandle, material.nativeHandle)
    }
    /** Destroy a MaterialInstance. */
    fun destroyMaterialInstance(materialInstance: MaterialInstance) {
        FilaEngine_destroyMaterialInstance(nativeHandle, materialInstance.nativeHandle)
    }
    /** Destroy a Skybox. */
    fun destroySkybox(skybox: Skybox) {
        FilaEngine_destroySkybox(nativeHandle, skybox.nativeHandle)
        skybox.nativeHandle = NullPointer
    }
    /** Destroy ColorGrading. */
    fun destroyColorGrading(colorGrading: ColorGrading) {
        FilaEngine_destroyColorGrading(nativeHandle, colorGrading.nativeHandle)
        colorGrading.nativeHandle = NullPointer
    }
    /** Destroy a Texture. */
    fun destroyTexture(texture: Texture) {
        FilaEngine_destroyTexture(nativeHandle, texture.nativeHandle)
    }
    /** Destroy a RenderTarget. */
    fun destroyRenderTarget(target: RenderTarget) {
        FilaEngine_destroyRenderTarget(nativeHandle, target.nativeHandle)
    }
    /** Destroy a Stream. */
    fun destroyStream(stream: Stream) {
        FilaEngine_destroyStream(nativeHandle, stream.nativeHandle)
        stream.nativeHandle = NullPointer
    }
    /** Destroy an Entity. */
    fun destroyEntity(entity: Entity) = FilaEntityManager_destroy(FilaEngine_getEntityManager(nativeHandle), entity)

    /** Get the TransformManager for managing entity transforms. */
    val transformManager: TransformManager get() = mTransformManager
    /** Get the LightManager for managing light components. */
    val lightManager: LightManager get() = mLightManager
    /** Get the RenderableManager for managing renderable components. */
    val renderableManager: RenderableManager get() = mRenderableManager
    /** Get the EntityManager for creating and managing entities. */
    val entityManager: EntityManager get() = mEntityManager

    /** Block until all pending GPU work completes (potentially long wait). */
    fun flushAndWait() { flushAndWait(1_000_000_000L) }
    /** Block until all pending GPU work completes or timeout expires. @return true if successful. */
    fun flushAndWait(timeout: Long): Boolean {
        platform.makeCurrent()
        // Single-threaded wasm flushes synchronously and rejects a non-zero timeout.
        return FilaEngine_flushAndWait(nativeHandle, if (singleThreaded) 0L else timeout)
    }
    /** Flush pending GPU commands to the driver (non-blocking). */
    fun flush() { platform.makeCurrent(); FilaEngine_flush(nativeHandle) }
    /**
     * Whether the Engine is in an unrecoverable failure state (e.g. the GPU device was lost).
     * Once true, the Engine must be destroyed and recreated. @return true if such a failure occurred.
     */
    val hasUnrecoverableFailure: Boolean get() = FilaEngine_hasUnrecoverableFailure(nativeHandle)
    // Filament's pause needs threads (setPaused panics on single-threaded wasm): tracked locally there.
    private var paused = false
    /** Whether rendering is currently paused. Set to pause or resume rendering. */
    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "state is only tracked locally — Filament's pause needs threads, which the wasm build doesn't have, so it has no effect on rendering.")
    var isPaused: Boolean
        get() = if (singleThreaded) paused else FilaEngine_isPaused(nativeHandle)
        set(value) { if (singleThreaded) paused = value else FilaEngine_setPaused(nativeHandle, value) }
    /** Deprecated no-op method. */
    fun unprotected() = FilaEngine_unprotected(nativeHandle)
    /** Check if a feature flag exists. */
    fun hasFeatureFlag(name: String): Boolean = name.useCString { FilaEngine_hasFeatureFlag(nativeHandle, it) }
    /** Set a feature flag value. @return true if successful. */
    fun setFeatureFlag(name: String, value: Boolean): Boolean {
        name.useCString { FilaEngine_setFeatureFlag(nativeHandle, it, value) }
        return true
    }
    /** Get a feature flag value. @return true if enabled. */
    fun getFeatureFlag(name: String): Boolean = name.useCString { FilaEngine_getFeatureFlag(nativeHandle, it) }

    /** Enable high-precision world-space translations for better numerical stability with large translations. */
    fun enableAccurateTranslations() = FilaEngine_enableAccurateTranslations(nativeHandle)

    /**
     * Material compilation priority queue.
     */
    enum class CompilerPriorityQueue {
        /** Compile immediately. */
        CRITICAL,
        /** Compile before LOW priority. */
        HIGH,
        /** Compile last. */
        LOW
    }
    /**
     * Feature state for conditional material compilation.
     */
    enum class FeatureState {
        /** Feature is disabled. */
        FALSE,
        /** Feature is enabled. */
        TRUE,
        /** Feature state is uncertain; material may compile both variants. */
        INDETERMINATE
    }

    /**
     * Asynchronously compile a material variant for specific rendering features.
     *
     * After issuing multiple compile() calls, call flush() to let the backend begin work.
     * The callback is invoked on the main thread when compilation is complete.
     *
     * @param priority Compilation priority (CRITICAL, HIGH, or LOW).
     * @param material Material to compile variants for.
     * @param view View providing rendering context.
     * @param shadowReceiver Whether the material receives shadows.
     * @param skinning Whether the material uses skeletal animation.
     * @param callback Optional callback invoked when compilation completes.
     */
    fun compile(priority: CompilerPriorityQueue, material: Material, view: View, shadowReceiver: FeatureState, skinning: FeatureState, callback: (() -> Unit)? = null) {
        val userData = if (callback != null) Callbacks.register(once = true) { _ -> callback() } else NullPointer
        FilaEngine_compile(
            nativeHandle,
            priority.ordinal,
            material.nativeHandle,
            view.nativeHandle,
            shadowReceiver.ordinal,
            skinning.ordinal,
            if (callback != null) Callbacks.userOnly else NullPointer,
            userData,
        )
    }
}

@ExternalSymbolName("FilaCamera_getEntity")
private external fun FilaCamera_getEntity(camera: NativePointer): Int

@ExternalSymbolName("FilaEngineBuilder_backend")
private external fun FilaEngineBuilder_backend(builder: NativePointer, backend: Int)

@ExternalSymbolName("FilaEngineBuilder_build")
private external fun FilaEngineBuilder_build(builder: NativePointer): NativePointer

@ExternalSymbolName("FilaEngineBuilder_colorGrading")
private external fun FilaEngineBuilder_colorGrading(builder: NativePointer, colorGrading: NativePointer)

@ExternalSymbolName("FilaEngineBuilder_config")
private external fun FilaEngineBuilder_config(builder: NativePointer, commandBufferSizeMB: Int, perRenderPassArenaSizeMB: Int, driverHandleArenaSizeMB: Int, minCommandBufferSizeMB: Int, perFrameCommandsSizeMB: Int, jobSystemThreadCount: Int, disableParallelShaderCompile: Boolean, stereoscopicType: Int, stereoscopicEyeCount: Int, resourceAllocatorCacheSizeMB: Int, resourceAllocatorCacheMaxAge: Int, disableHandleUseAfterFreeCheck: Boolean, preferredShaderLanguage: Int, forceGLES2Context: Boolean, assertNativeWindowIsValid: Boolean, gpuContextPriority: Int, sharedUboInitialSizeInBytes: Int, enableMultipleDirectionalLights: Boolean)

@ExternalSymbolName("FilaEngineBuilder_create")
private external fun FilaEngineBuilder_create(): NativePointer

@ExternalSymbolName("FilaEngineBuilder_destroy")
private external fun FilaEngineBuilder_destroy(builder: NativePointer)

@ExternalSymbolName("FilaEngineBuilder_feature")
private external fun FilaEngineBuilder_feature(builder: NativePointer, name: NativePointer, value: Boolean)

@ExternalSymbolName("FilaEngineBuilder_featureLevel")
private external fun FilaEngineBuilder_featureLevel(builder: NativePointer, featureLevel: Int)

@ExternalSymbolName("FilaEngineBuilder_paused")
private external fun FilaEngineBuilder_paused(builder: NativePointer, paused: Boolean)

@ExternalSymbolName("FilaEngineBuilder_sharedContext")
private external fun FilaEngineBuilder_sharedContext(builder: NativePointer, sharedContext: NativePointer)

@ExternalSymbolName("FilaEngine_compile")
private external fun FilaEngine_compile(engine: NativePointer, priority: Int, material: NativePointer, view: NativePointer, shadowReceiver: Int, skinning: Int, callback: NativePointer, userData: NativePointer)

@ExternalSymbolName("FilaEngine_createCamera")
private external fun FilaEngine_createCamera(engine: NativePointer, entity: Int): NativePointer

@ExternalSymbolName("FilaEngine_createCameraAuto")
private external fun FilaEngine_createCameraAuto(engine: NativePointer): NativePointer

@ExternalSymbolName("FilaEngine_createFence")
private external fun FilaEngine_createFence(engine: NativePointer): NativePointer

@ExternalSymbolName("FilaEngine_createRenderer")
private external fun FilaEngine_createRenderer(engine: NativePointer): NativePointer

@ExternalSymbolName("FilaEngine_createScene")
private external fun FilaEngine_createScene(engine: NativePointer): NativePointer

@ExternalSymbolName("FilaEngine_createSwapChain")
private external fun FilaEngine_createSwapChain(engine: NativePointer, nativeWindow: NativePointer, flags: Long): NativePointer

@ExternalSymbolName("FilaEngine_createSwapChainHeadless")
private external fun FilaEngine_createSwapChainHeadless(engine: NativePointer, width: Int, height: Int, flags: Long): NativePointer

@ExternalSymbolName("FilaEngine_createView")
private external fun FilaEngine_createView(engine: NativePointer): NativePointer

@ExternalSymbolName("FilaEngine_destroy")
private external fun FilaEngine_destroy(engine: NativePointer)

@ExternalSymbolName("FilaEngine_destroyCamera")
private external fun FilaEngine_destroyCamera(engine: NativePointer, camera: NativePointer): Boolean

@ExternalSymbolName("FilaEngine_destroyCameraComponent")
private external fun FilaEngine_destroyCameraComponent(engine: NativePointer, entity: Int)

@ExternalSymbolName("FilaEngine_destroyColorGrading")
private external fun FilaEngine_destroyColorGrading(engine: NativePointer, colorGrading: NativePointer): Boolean


@ExternalSymbolName("FilaEngine_destroyIndexBuffer")
private external fun FilaEngine_destroyIndexBuffer(engine: NativePointer, indexBuffer: NativePointer): Boolean

@ExternalSymbolName("FilaEngine_destroyIndirectLight")
private external fun FilaEngine_destroyIndirectLight(engine: NativePointer, indirectLight: NativePointer): Boolean

@ExternalSymbolName("FilaEngine_destroyMaterial")
private external fun FilaEngine_destroyMaterial(engine: NativePointer, material: NativePointer): Boolean

@ExternalSymbolName("FilaEngine_destroyMaterialInstance")
private external fun FilaEngine_destroyMaterialInstance(engine: NativePointer, materialInstance: NativePointer): Boolean

@ExternalSymbolName("FilaEngine_destroyMorphTargetBuffer")
private external fun FilaEngine_destroyMorphTargetBuffer(engine: NativePointer, morphTargetBuffer: NativePointer): Boolean

@ExternalSymbolName("FilaEngine_destroyRenderTarget")
private external fun FilaEngine_destroyRenderTarget(engine: NativePointer, target: NativePointer): Boolean

@ExternalSymbolName("FilaEngine_destroyRenderer")
private external fun FilaEngine_destroyRenderer(engine: NativePointer, renderer: NativePointer): Boolean

@ExternalSymbolName("FilaEngine_destroyScene")
private external fun FilaEngine_destroyScene(engine: NativePointer, scene: NativePointer): Boolean

@ExternalSymbolName("FilaEngine_destroySkinningBuffer")
private external fun FilaEngine_destroySkinningBuffer(engine: NativePointer, skinningBuffer: NativePointer): Boolean

@ExternalSymbolName("FilaEngine_destroySkybox")
private external fun FilaEngine_destroySkybox(engine: NativePointer, skybox: NativePointer): Boolean

@ExternalSymbolName("FilaEngine_destroyStream")
private external fun FilaEngine_destroyStream(engine: NativePointer, stream: NativePointer): Boolean

@ExternalSymbolName("FilaEngine_destroySwapChain")
private external fun FilaEngine_destroySwapChain(engine: NativePointer, swapChain: NativePointer): Boolean

@ExternalSymbolName("FilaEngine_destroyTexture")
private external fun FilaEngine_destroyTexture(engine: NativePointer, texture: NativePointer): Boolean

@ExternalSymbolName("FilaEngine_destroyVertexBuffer")
private external fun FilaEngine_destroyVertexBuffer(engine: NativePointer, vertexBuffer: NativePointer): Boolean

@ExternalSymbolName("FilaEngine_destroyView")
private external fun FilaEngine_destroyView(engine: NativePointer, view: NativePointer): Boolean

@ExternalSymbolName("FilaEngine_enableAccurateTranslations")
private external fun FilaEngine_enableAccurateTranslations(engine: NativePointer)

@ExternalSymbolName("FilaEngine_flush")
private external fun FilaEngine_flush(engine: NativePointer)

@ExternalSymbolName("FilaEngine_flushAndWait")
private external fun FilaEngine_flushAndWait(engine: NativePointer, timeout: Long): Boolean

@ExternalSymbolName("FilaEngine_getActiveFeatureLevel")
private external fun FilaEngine_getActiveFeatureLevel(engine: NativePointer): Int

@ExternalSymbolName("FilaEngine_getBackend")
private external fun FilaEngine_getBackend(engine: NativePointer): Int

@ExternalSymbolName("FilaEngine_getCameraComponent")
private external fun FilaEngine_getCameraComponent(engine: NativePointer, entity: Int): NativePointer

@ExternalSymbolName("FilaEngine_getEntityManager")
private external fun FilaEngine_getEntityManager(engine: NativePointer): NativePointer

@ExternalSymbolName("FilaEngine_getFeatureFlag")
private external fun FilaEngine_getFeatureFlag(engine: NativePointer, name: NativePointer): Boolean

@ExternalSymbolName("FilaEngine_getLightManager")
private external fun FilaEngine_getLightManager(engine: NativePointer): NativePointer

@ExternalSymbolName("FilaEngine_getMaxStereoscopicEyes")
private external fun FilaEngine_getMaxStereoscopicEyes(engine: NativePointer): Int

@ExternalSymbolName("FilaEngine_getRenderableManager")
private external fun FilaEngine_getRenderableManager(engine: NativePointer): NativePointer

@ExternalSymbolName("FilaEngine_getSteadyClockTimeNano")
private external fun FilaEngine_getSteadyClockTimeNano(out: NativePointer)

@ExternalSymbolName("FilaEngine_getSupportedFeatureLevel")
private external fun FilaEngine_getSupportedFeatureLevel(engine: NativePointer): Int

@ExternalSymbolName("FilaEngine_getTransformManager")
private external fun FilaEngine_getTransformManager(engine: NativePointer): NativePointer

@ExternalSymbolName("FilaEngine_hasFeatureFlag")
private external fun FilaEngine_hasFeatureFlag(engine: NativePointer, name: NativePointer): Boolean

@ExternalSymbolName("FilaEngine_hasUnrecoverableFailure")
private external fun FilaEngine_hasUnrecoverableFailure(engine: NativePointer): Boolean

@ExternalSymbolName("FilaEngine_isAutomaticInstancingEnabled")
private external fun FilaEngine_isAutomaticInstancingEnabled(engine: NativePointer): Boolean

@ExternalSymbolName("FilaEngine_isPaused")
private external fun FilaEngine_isPaused(engine: NativePointer): Boolean

@ExternalSymbolName("FilaEngine_isValidColorGrading")
private external fun FilaEngine_isValidColorGrading(engine: NativePointer, colorGrading: NativePointer): Boolean

@ExternalSymbolName("FilaEngine_isValidExpensiveMaterialInstance")
private external fun FilaEngine_isValidExpensiveMaterialInstance(engine: NativePointer, materialInstance: NativePointer): Boolean

@ExternalSymbolName("FilaEngine_isValidFence")
private external fun FilaEngine_isValidFence(engine: NativePointer, fence: NativePointer): Boolean

@ExternalSymbolName("FilaEngine_isValidIndexBuffer")
private external fun FilaEngine_isValidIndexBuffer(engine: NativePointer, indexBuffer: NativePointer): Boolean

@ExternalSymbolName("FilaEngine_isValidIndirectLight")
private external fun FilaEngine_isValidIndirectLight(engine: NativePointer, indirectLight: NativePointer): Boolean

@ExternalSymbolName("FilaEngine_isValidMaterial")
private external fun FilaEngine_isValidMaterial(engine: NativePointer, material: NativePointer): Boolean

@ExternalSymbolName("FilaEngine_isValidMaterialInstance")
private external fun FilaEngine_isValidMaterialInstance(engine: NativePointer, material: NativePointer, materialInstance: NativePointer): Boolean

@ExternalSymbolName("FilaEngine_isValidMorphTargetBuffer")
private external fun FilaEngine_isValidMorphTargetBuffer(engine: NativePointer, morphTargetBuffer: NativePointer): Boolean

@ExternalSymbolName("FilaEngine_isValidRenderTarget")
private external fun FilaEngine_isValidRenderTarget(engine: NativePointer, target: NativePointer): Boolean

@ExternalSymbolName("FilaEngine_isValidRenderer")
private external fun FilaEngine_isValidRenderer(engine: NativePointer, renderer: NativePointer): Boolean

@ExternalSymbolName("FilaEngine_isValidScene")
private external fun FilaEngine_isValidScene(engine: NativePointer, scene: NativePointer): Boolean

@ExternalSymbolName("FilaEngine_isValidSkinningBuffer")
private external fun FilaEngine_isValidSkinningBuffer(engine: NativePointer, skinningBuffer: NativePointer): Boolean

@ExternalSymbolName("FilaEngine_isValidSkybox")
private external fun FilaEngine_isValidSkybox(engine: NativePointer, skybox: NativePointer): Boolean

@ExternalSymbolName("FilaEngine_isValidStream")
private external fun FilaEngine_isValidStream(engine: NativePointer, stream: NativePointer): Boolean

@ExternalSymbolName("FilaEngine_isValidSwapChain")
private external fun FilaEngine_isValidSwapChain(engine: NativePointer, swapChain: NativePointer): Boolean

@ExternalSymbolName("FilaEngine_isValidTexture")
private external fun FilaEngine_isValidTexture(engine: NativePointer, texture: NativePointer): Boolean

@ExternalSymbolName("FilaEngine_isValidVertexBuffer")
private external fun FilaEngine_isValidVertexBuffer(engine: NativePointer, vertexBuffer: NativePointer): Boolean

@ExternalSymbolName("FilaEngine_isValidView")
private external fun FilaEngine_isValidView(engine: NativePointer, view: NativePointer): Boolean

@ExternalSymbolName("FilaEngine_setActiveFeatureLevel")
private external fun FilaEngine_setActiveFeatureLevel(engine: NativePointer, featureLevel: Int): Int

@ExternalSymbolName("FilaEngine_setAutomaticInstancingEnabled")
private external fun FilaEngine_setAutomaticInstancingEnabled(engine: NativePointer, enable: Boolean)

@ExternalSymbolName("FilaEngine_setFeatureFlag")
private external fun FilaEngine_setFeatureFlag(engine: NativePointer, name: NativePointer, value: Boolean)

@ExternalSymbolName("FilaEngine_setPaused")
private external fun FilaEngine_setPaused(engine: NativePointer, paused: Boolean)

@ExternalSymbolName("FilaEngine_unprotected")
private external fun FilaEngine_unprotected(engine: NativePointer)


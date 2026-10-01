package io.github.erkko68.filament

import io.github.erkko68.filament.capi.*
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
 * val engine = Engine.create()!!
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

    /** Rendering backend selection. */
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
        /** Minimum feature set; OpenGL ES 2 compatible. No post-processing, limited lighting models, minimal texture formats. */
        FEATURE_LEVEL_0,
        /** Metal-level feature set; good for mid-range devices. */
        FEATURE_LEVEL_1,
        /** Full feature set with all capabilities. */
        FEATURE_LEVEL_2,
        /** Advanced features beyond the standard feature set. */
        FEATURE_LEVEL_3,
    }

    /** Stereoscopic rendering technique for VR and 3D displays. */
    enum class StereoscopicType {
        /** No stereoscopic rendering (monoscopic). */
        NONE,
        /** Instanced stereo rendering (two draw calls, one per eye). */
        INSTANCED,
        /** Multiview stereo rendering (single draw call using instancing, faster). */
        MULTIVIEW,
    }

    /** GPU context priority, a hint to the driver for work scheduling and preemption. */
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

    /** How the engine handles asynchronous operations. */
    enum class AsynchronousMode {
        /** Asynchronous operations are disabled. */
        NONE,
        /** Asynchronous operations run on a dedicated thread where the platform supports one. */
        THREAD_PREFERRED,
        /** Asynchronous operations are spread across frames on the render thread. */
        AMORTIZATION,
    }

    /**
     * Advanced parameters for customizing Engine initialization.
     *
     * These settings control memory allocation, threading, and rendering behavior.
     */
    class Config {
        /** Size in MiB of the low-level command buffer arena; typically `minCommandBufferSizeMB * 3`. */
        var commandBufferSizeMB: Int = 3 * 1
        /** Size in MiB of the per-frame data arena, the main area used by the engine to allocate per-frame data. */
        var perRenderPassArenaSizeMB: Int = 3
        /** Size in MiB of the backend's handle arena; 0 uses the build's default. */
        var driverHandleArenaSizeMB: Int = 0
        /** Minimum size in MiB of a low-level command buffer. */
        var minCommandBufferSizeMB: Int = 1
        /** Size in MiB of the per-frame high-level command buffer. */
        var perFrameCommandsSizeMB: Int = 2
        /** Number of threads for the job system: 0 picks by heuristic, [SINGLE_THREADED] runs jobs on the calling thread. */
        var jobSystemThreadCount: Int = 0
        /** Total size of the Metal backend's shared upload staging buffer; 0 disables it. */
        var metalUploadBufferSizeBytes: Int = 512 * 1024
        /** Don't panic when the Metal backend can't acquire a drawable. */
        var metalDisablePanicOnDrawableFailure: Boolean = false
        /** Disable backend parallel shader compilation, forcing serial compilation. */
        var disableParallelShaderCompile: Boolean = false
        /** Stereoscopic rendering technique to use. */
        var stereoscopicType: StereoscopicType = StereoscopicType.NONE
        /** Number of stereoscopic eyes (usually 2 for VR). */
        var stereoscopicEyeCount: Int = 2
        /** Size of the resource allocator cache in MiB. */
        var resourceAllocatorCacheSizeMB: Int = 64
        /** Maximum age of cached resources, in frames. */
        var resourceAllocatorCacheMaxAge: Int = 1
        /** Disable the debug check that catches use of a destroyed backend handle. */
        var disableHandleUseAfterFreeCheck: Boolean = false

        /** The shader language the Metal backend prefers; no effect on other backends. */
        enum class ShaderLanguage {
            /** Use the platform default. */
            DEFAULT,
            /** Metal Shading Language source. */
            MSL,
            /** Precompiled Metal library. */
            METAL_LIBRARY,
        }
        /** Preferred shader language to use. */
        var preferredShaderLanguage: ShaderLanguage = ShaderLanguage.DEFAULT
        /** Force an OpenGL ES 2.0 context (if applicable). */
        var forceGLES2Context: Boolean = false
        /** Assert that the native window handed to `createSwapChain` is valid. */
        var assertNativeWindowIsValid: Boolean = false
        /** GPU context priority hint for the driver. */
        var gpuContextPriority: GpuContextPriority = GpuContextPriority.DEFAULT
        /** Initial size in bytes of the shared uniform buffer used for batching. */
        var sharedUboInitialSizeInBytes: Int = 256 * 64
        /**
         * How asynchronous operations are handled. They also need the `backend.enable_asynchronous_operation`
         * [Builder.feature] and a backend that supports them; check [Engine.isAsynchronousModeEnabled] before using them.
         * Without threads (web) THREAD_PREFERRED falls back to AMORTIZATION, which advances only as frames render.
         */
        var asynchronousMode: AsynchronousMode = AsynchronousMode.NONE
        /** Unreferenced material definitions kept alive to avoid recompiling; 0 destroys them immediately. */
        var materialCacheCapacity: Int = 0
        /** Unreferenced program specializations kept alive; 0 destroys them immediately. */
        var programCacheCapacity: Int = 0
        /**
         * Evaluate up to four directional lights beyond the dominant one. The extra lights
         * cast no shadows and draw no sun disc.
         */
        var enableMultipleDirectionalLights: Boolean = false

        internal fun <T> useNative(block: (NativePointer) -> T): T = withHandle({ FilaEngineConfig_create() }, { FilaEngineConfig_destroy(it) }) { c ->
            FilaEngineConfig_setCommandBufferSizeMB(c, commandBufferSizeMB)
            FilaEngineConfig_setPerRenderPassArenaSizeMB(c, perRenderPassArenaSizeMB)
            FilaEngineConfig_setDriverHandleArenaSizeMB(c, driverHandleArenaSizeMB)
            FilaEngineConfig_setMinCommandBufferSizeMB(c, minCommandBufferSizeMB)
            FilaEngineConfig_setPerFrameCommandsSizeMB(c, perFrameCommandsSizeMB)
            FilaEngineConfig_setJobSystemThreadCount(c, jobSystemThreadCount)
            FilaEngineConfig_setMetalUploadBufferSizeBytes(c, metalUploadBufferSizeBytes)
            FilaEngineConfig_setMetalDisablePanicOnDrawableFailure(c, metalDisablePanicOnDrawableFailure)
            FilaEngineConfig_setDisableParallelShaderCompile(c, disableParallelShaderCompile)
            FilaEngineConfig_setStereoscopicType(c, stereoscopicType.ordinal)
            FilaEngineConfig_setStereoscopicEyeCount(c, stereoscopicEyeCount)
            FilaEngineConfig_setResourceAllocatorCacheSizeMB(c, resourceAllocatorCacheSizeMB)
            FilaEngineConfig_setResourceAllocatorCacheMaxAge(c, resourceAllocatorCacheMaxAge)
            FilaEngineConfig_setDisableHandleUseAfterFreeCheck(c, disableHandleUseAfterFreeCheck)
            FilaEngineConfig_setPreferredShaderLanguage(c, preferredShaderLanguage.ordinal)
            FilaEngineConfig_setForceGLES2Context(c, forceGLES2Context)
            FilaEngineConfig_setAssertNativeWindowIsValid(c, assertNativeWindowIsValid)
            FilaEngineConfig_setGpuContextPriority(c, gpuContextPriority.ordinal)
            FilaEngineConfig_setSharedUboInitialSizeInBytes(c, sharedUboInitialSizeInBytes)
            FilaEngineConfig_setAsynchronousMode(c, asynchronousMode.ordinal)
            FilaEngineConfig_setMaterialCacheCapacity(c, materialCacheCapacity)
            FilaEngineConfig_setProgramCacheCapacity(c, programCacheCapacity)
            FilaEngineConfig_setEnableMultipleDirectionalLights(c, enableMultipleDirectionalLights)
            block(c)
        }

        companion object {
            /** [jobSystemThreadCount] value that runs the JobSystem's jobs on the calling thread. */
            const val SINGLE_THREADED: Int = -1 // uint32_t max

            internal fun of(c: NativePointer) = Config().apply {
                commandBufferSizeMB = FilaEngineConfig_getCommandBufferSizeMB(c)
                perRenderPassArenaSizeMB = FilaEngineConfig_getPerRenderPassArenaSizeMB(c)
                driverHandleArenaSizeMB = FilaEngineConfig_getDriverHandleArenaSizeMB(c)
                minCommandBufferSizeMB = FilaEngineConfig_getMinCommandBufferSizeMB(c)
                perFrameCommandsSizeMB = FilaEngineConfig_getPerFrameCommandsSizeMB(c)
                jobSystemThreadCount = FilaEngineConfig_getJobSystemThreadCount(c)
                metalUploadBufferSizeBytes = FilaEngineConfig_getMetalUploadBufferSizeBytes(c)
                metalDisablePanicOnDrawableFailure = FilaEngineConfig_getMetalDisablePanicOnDrawableFailure(c)
                disableParallelShaderCompile = FilaEngineConfig_getDisableParallelShaderCompile(c)
                stereoscopicType = StereoscopicType.entries[FilaEngineConfig_getStereoscopicType(c)]
                stereoscopicEyeCount = FilaEngineConfig_getStereoscopicEyeCount(c)
                resourceAllocatorCacheSizeMB = FilaEngineConfig_getResourceAllocatorCacheSizeMB(c)
                resourceAllocatorCacheMaxAge = FilaEngineConfig_getResourceAllocatorCacheMaxAge(c)
                disableHandleUseAfterFreeCheck = FilaEngineConfig_getDisableHandleUseAfterFreeCheck(c)
                preferredShaderLanguage = ShaderLanguage.entries[FilaEngineConfig_getPreferredShaderLanguage(c)]
                forceGLES2Context = FilaEngineConfig_getForceGLES2Context(c)
                assertNativeWindowIsValid = FilaEngineConfig_getAssertNativeWindowIsValid(c)
                gpuContextPriority = GpuContextPriority.entries[FilaEngineConfig_getGpuContextPriority(c)]
                sharedUboInitialSizeInBytes = FilaEngineConfig_getSharedUboInitialSizeInBytes(c)
                asynchronousMode = AsynchronousMode.entries[FilaEngineConfig_getAsynchronousMode(c)]
                materialCacheCapacity = FilaEngineConfig_getMaterialCacheCapacity(c)
                programCacheCapacity = FilaEngineConfig_getProgramCacheCapacity(c)
                enableMultipleDirectionalLights = FilaEngineConfig_getEnableMultipleDirectionalLights(c)
            }
        }
    }

    /**
     * A feature flag: a last-resort switch for a faulty feature, set when the Engine is built and,
     * unless [constant], at any time with [setFeatureFlag].
     */
    class FeatureFlag(
        val name: String,
        val description: String,
        /** The flag's value when it was read. */
        val value: Boolean,
        /** Whether the flag can only be set when the Engine is built. */
        val constant: Boolean,
    )

    /** Builder for creating and configuring an Engine instance. */
    class Builder() {
        init { Filament.init() }
        private val nativeBuilder = FilaEngineBuilder_create()
        private var backend = Backend.DEFAULT
        private var sharedContext: Any? = null

        /** Sets the rendering backend; DEFAULT lets the platform choose. */
        fun backend(backend: Backend): Builder = apply {
            this.backend = backend
            FilaEngineBuilder_backend(nativeBuilder, backend.ordinal)
        }

        /**
         * Shares a platform-specific rendering context with the Engine (e.g. an EGLContext on Android),
         * to render to several windows or alongside an existing renderer.
         */
        fun sharedContext(sharedContext: Any?): Builder = apply {
            this.sharedContext = sharedContext
            FilaEngineBuilder_sharedContext(nativeBuilder, sharedContext?.let(::sharedContextPointer) ?: NullPointer)
        }

        /** Sets the Engine's advanced configuration; null restores the defaults. */
        fun config(config: Config?): Builder = apply {
            config?.useNative { FilaEngineBuilder_config(nativeBuilder, it) } ?: FilaEngineBuilder_config(nativeBuilder, NullPointer)
        }

        /** Sets the feature level; the effective level is the minimum of this and the backend's maximum. */
        fun featureLevel(featureLevel: FeatureLevel): Builder = apply { FilaEngineBuilder_featureLevel(nativeBuilder, featureLevel.ordinal) }

        // Single-threaded wasm can't unpause Filament's queue, so there the pause is only tracked by the Engine.
        private var startPaused = false

        /** Starts the Engine paused; set [Engine.isPaused] to false to resume. */
        @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "only sets Engine.isPaused, which is tracked locally there: the wasm build has no render thread to pause.")
        fun paused(paused: Boolean): Builder = apply {
            if (singleThreaded) startPaused = paused else FilaEngineBuilder_paused(nativeBuilder, paused)
        }

        /** Sets a feature flag's value. */
        fun feature(name: String, value: Boolean): Builder = apply { name.useCString { FilaEngineBuilder_feature(nativeBuilder, it, value) } }

        /** Sets the default color grading configuration. */
        fun colorGrading(colorGrading: ColorGrading.Builder): Builder = apply { FilaEngineBuilder_colorGrading(nativeBuilder, colorGrading.nativeHandle) }

        /** Creates the Engine, or returns null if the backend couldn't be initialized. */
        fun build(): Engine? {
            val platform = enginePlatform(backend, sharedContext)
            val handle = FilaEngineBuilder_build(nativeBuilder)
            FilaEngineBuilder_destroy(nativeBuilder)
            return engineOf(handle, platform)?.also { if (startPaused) it.isPaused = true }
        }

        /**
         * Creates the Engine asynchronously. [callback] runs, possibly on another thread, once it's safe to call
         * [getEngine] with its token, which must happen on the thread that called this.
         */
        @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "builds synchronously and calls back before returning: the wasm build has no threads.")
        fun build(callback: (Token) -> Unit) {
            val platform = enginePlatform(backend, sharedContext)
            if (singleThreaded) {
                val engine = build()
                return callback(Token(NullPointer, platform, engine))
            }
            val user = Callbacks.register(once = true) { token -> callback(Token(token, platform)) }
            FilaEngineBuilder_build_Invocable(nativeBuilder, Callbacks.argUser, user)
            FilaEngineBuilder_destroy(nativeBuilder)
        }
    }

    /** The opaque token [Builder.build]'s callback gets, for [getEngine]. */
    class Token internal constructor(
        internal val token: NativePointer,
        internal val platform: EnginePlatform,
        // Web builds synchronously: the engine, already built.
        internal val engine: Engine? = null,
    )

    companion object {
        init { Filament.init() } // statics are callable before any Engine exists

        /**
         * Creates an Engine, or returns null if the backend couldn't be initialized.
         *
         * @param backend The backend to use; DEFAULT lets the platform choose.
         * @param sharedContext A platform-specific context to share (e.g. an EGLContext on Android).
         * @param config Advanced configuration, or null for the defaults.
         */
        fun create(backend: Backend = Backend.DEFAULT, sharedContext: Any? = null, config: Config? = null): Engine? =
            Builder().backend(backend).sharedContext(sharedContext).config(config).build()

        /** Creates an Engine asynchronously; see [Builder.build]. */
        @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "creates the engine synchronously and calls back before returning: the wasm build has no threads.")
        fun createAsync(backend: Backend = Backend.DEFAULT, sharedContext: Any? = null, config: Config? = null, callback: (Token) -> Unit) =
            Builder().backend(backend).sharedContext(sharedContext).config(config).build(callback)

        /**
         * The Engine [createAsync] or [Builder.build] created, or null if it couldn't be. Call it on the thread that
         * started the creation.
         */
        fun getEngine(token: Token): Engine? =
            if (singleThreaded) token.engine else engineOf(FilaEngine_getEngine(token.token), token.platform)

        private fun engineOf(handle: NativePointer, platform: EnginePlatform): Engine? {
            if (handle == NullPointer) {
                platform.release()
                return null
            }
            return Engine(handle, platform)
        }

        /**
         * Destroys [engine] and every resource it still tracks. Blocking; destroy the Renderer, View, Scene and other
         * resources first.
         */
        fun destroy(engine: Engine?) {
            if (engine == null || engine.nativeHandle == NullPointer) return
            engine.platform.makeCurrent()
            FilaEngine_destroy_Engine(engine.nativeHandle)
            engine.nativeHandle = NullPointer
            engine.platform.release()
        }

        /** The maximum number of stereoscopic eyes supported by Filament. */
        val maxStereoscopicEyes: Int get() = FilaEngine_getMaxStereoscopicEyes()

        /** The current time in nanoseconds since the epoch of the steady clock, the one [Renderer.beginFrame] uses. */
        val steadyClockTimeNano: Long
            get() = LongArray(1).also { out -> out.usePinned { FilaEngine_getSteadyClockTimeNano(it) } }[0]
    }

    /** Whether this Engine hasn't been destroyed. */
    val isValid: Boolean get() = nativeHandle != NullPointer
    /** Same as [destroy]; lets this be used with `use { }` and try-with-resources. */
    override fun close() = destroy(this)

    /** The rendering backend being used by this Engine. */
    val backend: Backend get() = Backend.entries[FilaEngine_getBackend(nativeHandle)]
    /** The highest feature level supported by this backend. */
    val supportedFeatureLevel: FeatureLevel get() = FeatureLevel.entries[FilaEngine_getSupportedFeatureLevel(nativeHandle)]
    /**
     * Activates [featureLevel], at most [supportedFeatureLevel]; a level can't be lowered once activated.
     *
     * @return The active feature level.
     */
    fun setActiveFeatureLevel(featureLevel: FeatureLevel): FeatureLevel =
        FeatureLevel.entries[FilaEngine_setActiveFeatureLevel(nativeHandle, featureLevel.ordinal)]
    /** The currently active feature level. */
    val activeFeatureLevel: FeatureLevel get() = FeatureLevel.entries[FilaEngine_getActiveFeatureLevel(nativeHandle)]

    /** The maximum number of instances automatic instancing batches together. */
    val maxAutomaticInstances: Int get() = FilaEngine_getMaxAutomaticInstances(nativeHandle)
    /** Whether this Engine supports [stereoscopicType] rendering. */
    fun isStereoSupported(stereoscopicType: StereoscopicType): Boolean = FilaEngine_isStereoSupported(nativeHandle, stereoscopicType.ordinal)
    /** Whether asynchronous operations can be used (see [Config.asynchronousMode]). */
    val isAsynchronousModeEnabled: Boolean get() = FilaEngine_isAsynchronousModeEnabled(nativeHandle)
    /**
     * Whether the Engine is in an unrecoverable failure state (e.g. the GPU device was lost).
     * Once true, the Engine must be destroyed and recreated.
     */
    val hasUnrecoverableFailure: Boolean get() = FilaEngine_hasUnrecoverableFailure(nativeHandle)
    /** The configuration this Engine was built with. */
    val config: Config
        get() = withHandle({ FilaEngineConfig_create() }, { FilaEngineConfig_destroy(it) }) { c -> FilaEngine_getConfig(nativeHandle, c); Config.of(c) }

    /** The EntityManager for creating and managing entities. */
    val entityManager: EntityManager get() = mEntityManager
    /** The RenderableManager for managing renderable components. */
    val renderableManager: RenderableManager get() = mRenderableManager
    /** The LightManager for managing light components. */
    val lightManager: LightManager get() = mLightManager
    /** The TransformManager for managing entity transforms. */
    val transformManager: TransformManager get() = mTransformManager

    /** Enables high-precision world-space translations, for better numerical stability with large translations. */
    fun enableAccurateTranslations() = FilaEngine_enableAccurateTranslations(nativeHandle)

    /** Whether the engine batches identical renderables into instanced draw calls. */
    var isAutomaticInstancingEnabled: Boolean
        get() = FilaEngine_isAutomaticInstancingEnabled(nativeHandle)
        set(value) { FilaEngine_setAutomaticInstancingEnabled(nativeHandle, value) }

    /** Creates a SwapChain rendering into a native display surface. */
    fun createSwapChain(surface: NativeSurface, flags: Long = 0L): SwapChain {
        val window = acquireWindow(surface)
        return SwapChain(FilaEngine_createSwapChain_void_uint64_t(nativeHandle, window, flags), window)
    }
    /** Creates an offscreen SwapChain of the given size. */
    fun createSwapChain(width: Int, height: Int, flags: Long = 0L): SwapChain =
        SwapChain(FilaEngine_createSwapChain_uint32_t_uint32_t_uint64_t(nativeHandle, width, height, flags))
    /** Creates a Renderer. */
    fun createRenderer(): Renderer = Renderer(FilaEngine_createRenderer(nativeHandle)).setEngine(this)
    /** Creates a View. */
    fun createView(): View = View(FilaEngine_createView(nativeHandle))
    /** Creates a Scene. */
    fun createScene(): Scene = Scene(FilaEngine_createScene(nativeHandle))
    /** Creates a Camera component on [entity]. */
    fun createCamera(entity: Entity): Camera = Camera(FilaEngine_createCamera(nativeHandle, entity), entity)
    /** The Camera component of [entity], or null if it has none. */
    fun getCameraComponent(entity: Entity): Camera? =
        FilaEngine_getCameraComponent(nativeHandle, entity).takeIf { it != NullPointer }?.let { Camera(it, entity) }
    /** Destroys the Camera component of [entity]. */
    fun destroyCameraComponent(entity: Entity) = FilaEngine_destroyCameraComponent(nativeHandle, entity)
    /** Creates a Fence. */
    fun createFence(): Fence = Fence(FilaEngine_createFence(nativeHandle), nativeHandle)

    /** Destroys a BufferObject. */
    fun destroy(bufferObject: BufferObject): Boolean =
        FilaEngine_destroy_BufferObject(nativeHandle, bufferObject.nativeHandle).also { bufferObject.nativeHandle = NullPointer }
    /** Destroys a VertexBuffer. */
    fun destroy(vertexBuffer: VertexBuffer): Boolean =
        FilaEngine_destroy_VertexBuffer(nativeHandle, vertexBuffer.nativeHandle).also { vertexBuffer.nativeHandle = NullPointer }
    /** Destroys a Fence. */
    fun destroy(fence: Fence): Boolean =
        FilaEngine_destroy_Fence(nativeHandle, fence.nativeHandle).also { fence.nativeHandle = NullPointer }
    /** Destroys a FramePacer. */
    fun destroy(framePacer: FramePacer): Boolean =
        FilaEngine_destroy_FramePacer(nativeHandle, framePacer.nativeHandle).also { framePacer.nativeHandle = NullPointer }
    /** Destroys an InstanceBuffer. */
    fun destroy(instanceBuffer: InstanceBuffer): Boolean =
        FilaEngine_destroy_InstanceBuffer(nativeHandle, instanceBuffer.nativeHandle).also { instanceBuffer.nativeHandle = NullPointer }
    /** Destroys an IndexBuffer. */
    fun destroy(indexBuffer: IndexBuffer): Boolean =
        FilaEngine_destroy_IndexBuffer(nativeHandle, indexBuffer.nativeHandle).also { indexBuffer.nativeHandle = NullPointer }
    /** Destroys a SkinningBuffer. */
    fun destroy(skinningBuffer: SkinningBuffer): Boolean =
        FilaEngine_destroy_SkinningBuffer(nativeHandle, skinningBuffer.nativeHandle).also { skinningBuffer.nativeHandle = NullPointer }
    /** Destroys a MorphTargetBuffer. */
    fun destroy(morphTargetBuffer: MorphTargetBuffer): Boolean =
        FilaEngine_destroy_MorphTargetBuffer(nativeHandle, morphTargetBuffer.nativeHandle).also { morphTargetBuffer.nativeHandle = NullPointer }
    /** Destroys an IndirectLight. */
    fun destroy(indirectLight: IndirectLight): Boolean =
        FilaEngine_destroy_IndirectLight(nativeHandle, indirectLight.nativeHandle).also { indirectLight.nativeHandle = NullPointer }
    /** Destroys a Material; its instances must be destroyed first. */
    fun destroy(material: Material): Boolean = FilaEngine_destroy_Material(nativeHandle, material.nativeHandle)
    /** Destroys a MaterialInstance. */
    fun destroy(materialInstance: MaterialInstance): Boolean = FilaEngine_destroy_MaterialInstance(nativeHandle, materialInstance.nativeHandle)
    /** Destroys a Renderer. */
    fun destroy(renderer: Renderer): Boolean =
        FilaEngine_destroy_Renderer(nativeHandle, renderer.nativeHandle).also { renderer.nativeHandle = NullPointer }
    /** Destroys a Scene. */
    fun destroy(scene: Scene): Boolean = FilaEngine_destroy_Scene(nativeHandle, scene.nativeHandle).also { scene.nativeHandle = NullPointer }
    /** Destroys a Skybox. */
    fun destroy(skybox: Skybox): Boolean = FilaEngine_destroy_Skybox(nativeHandle, skybox.nativeHandle).also { skybox.nativeHandle = NullPointer }
    /** Destroys a ColorGrading. */
    fun destroy(colorGrading: ColorGrading): Boolean =
        FilaEngine_destroy_ColorGrading(nativeHandle, colorGrading.nativeHandle).also { colorGrading.nativeHandle = NullPointer }
    /** Destroys a SwapChain, and releases the native window it rendered into. */
    fun destroy(swapChain: SwapChain): Boolean {
        val destroyed = FilaEngine_destroy_SwapChain(nativeHandle, swapChain.nativeHandle)
        swapChain.nativeHandle = NullPointer
        swapChain.releaseCallbackStubs()
        releaseWindow(swapChain.window)
        swapChain.window = NullPointer
        return destroyed
    }
    /** Destroys a Stream. */
    fun destroy(stream: Stream): Boolean = FilaEngine_destroy_Stream(nativeHandle, stream.nativeHandle).also { stream.nativeHandle = NullPointer }
    /** Destroys a Texture. */
    fun destroy(texture: Texture): Boolean = FilaEngine_destroy_Texture(nativeHandle, texture.nativeHandle)
    /** Destroys a RenderTarget. */
    fun destroy(renderTarget: RenderTarget): Boolean = FilaEngine_destroy_RenderTarget(nativeHandle, renderTarget.nativeHandle)
    /** Destroys a View. */
    fun destroy(view: View): Boolean = FilaEngine_destroy_View(nativeHandle, view.nativeHandle).also { view.nativeHandle = NullPointer }
    /** Destroys every Filament component of [entity]; the entity itself is the EntityManager's. */
    fun destroy(entity: Entity) = FilaEngine_destroy_Entity(nativeHandle, entity)

    /** Whether [bufferObject] is a live object of this Engine. */
    fun isValid(bufferObject: BufferObject): Boolean = FilaEngine_isValid_BufferObject(nativeHandle, bufferObject.nativeHandle)
    /** Whether [vertexBuffer] is a live object of this Engine. */
    fun isValid(vertexBuffer: VertexBuffer): Boolean = FilaEngine_isValid_VertexBuffer(nativeHandle, vertexBuffer.nativeHandle)
    /** Whether [fence] is a live object of this Engine. */
    fun isValid(fence: Fence): Boolean = FilaEngine_isValid_Fence(nativeHandle, fence.nativeHandle)
    /** Whether [instanceBuffer] is a live object of this Engine. */
    fun isValid(instanceBuffer: InstanceBuffer): Boolean = FilaEngine_isValid_InstanceBuffer(nativeHandle, instanceBuffer.nativeHandle)
    /** Whether [indexBuffer] is a live object of this Engine. */
    fun isValid(indexBuffer: IndexBuffer): Boolean = FilaEngine_isValid_IndexBuffer(nativeHandle, indexBuffer.nativeHandle)
    /** Whether [skinningBuffer] is a live object of this Engine. */
    fun isValid(skinningBuffer: SkinningBuffer): Boolean = FilaEngine_isValid_SkinningBuffer(nativeHandle, skinningBuffer.nativeHandle)
    /** Whether [morphTargetBuffer] is a live object of this Engine. */
    fun isValid(morphTargetBuffer: MorphTargetBuffer): Boolean = FilaEngine_isValid_MorphTargetBuffer(nativeHandle, morphTargetBuffer.nativeHandle)
    /** Whether [indirectLight] is a live object of this Engine. */
    fun isValid(indirectLight: IndirectLight): Boolean = FilaEngine_isValid_IndirectLight(nativeHandle, indirectLight.nativeHandle)
    /** Whether [material] is a live object of this Engine. */
    fun isValid(material: Material): Boolean = FilaEngine_isValid_Material(nativeHandle, material.nativeHandle)
    /** Whether [materialInstance] is a live instance of [material]. */
    fun isValid(material: Material, materialInstance: MaterialInstance): Boolean =
        FilaEngine_isValid_Material_MaterialInstance(nativeHandle, material.nativeHandle, materialInstance.nativeHandle)
    /** Whether [materialInstance] is a live instance of any of this Engine's materials; slower than [isValid]. */
    fun isValidExpensive(materialInstance: MaterialInstance): Boolean = FilaEngine_isValidExpensive(nativeHandle, materialInstance.nativeHandle)
    /** Whether [renderer] is a live object of this Engine. */
    fun isValid(renderer: Renderer): Boolean = FilaEngine_isValid_Renderer(nativeHandle, renderer.nativeHandle)
    /** Whether [scene] is a live object of this Engine. */
    fun isValid(scene: Scene): Boolean = FilaEngine_isValid_Scene(nativeHandle, scene.nativeHandle)
    /** Whether [skybox] is a live object of this Engine. */
    fun isValid(skybox: Skybox): Boolean = FilaEngine_isValid_Skybox(nativeHandle, skybox.nativeHandle)
    /** Whether [colorGrading] is a live object of this Engine. */
    fun isValid(colorGrading: ColorGrading): Boolean = FilaEngine_isValid_ColorGrading(nativeHandle, colorGrading.nativeHandle)
    /** Whether [swapChain] is a live object of this Engine. */
    fun isValid(swapChain: SwapChain): Boolean = FilaEngine_isValid_SwapChain(nativeHandle, swapChain.nativeHandle)
    /** Whether [stream] is a live object of this Engine. */
    fun isValid(stream: Stream): Boolean = FilaEngine_isValid_Stream(nativeHandle, stream.nativeHandle)
    /** Whether [texture] is a live object of this Engine. */
    fun isValid(texture: Texture): Boolean = FilaEngine_isValid_Texture(nativeHandle, texture.nativeHandle)
    /** Whether [renderTarget] is a live object of this Engine. */
    fun isValid(renderTarget: RenderTarget): Boolean = FilaEngine_isValid_RenderTarget(nativeHandle, renderTarget.nativeHandle)
    /** Whether [view] is a live object of this Engine. */
    fun isValid(view: View): Boolean = FilaEngine_isValid_View(nativeHandle, view.nativeHandle)

    // Live object counts, for debugging.
    val bufferObjectCount: Int get() = FilaEngine_getBufferObjectCount(nativeHandle)
    val viewCount: Int get() = FilaEngine_getViewCount(nativeHandle)
    val sceneCount: Int get() = FilaEngine_getSceneCount(nativeHandle)
    val swapChainCount: Int get() = FilaEngine_getSwapChainCount(nativeHandle)
    val streamCount: Int get() = FilaEngine_getStreamCount(nativeHandle)
    val indexBufferCount: Int get() = FilaEngine_getIndexBufferCount(nativeHandle)
    val skinningBufferCount: Int get() = FilaEngine_getSkinningBufferCount(nativeHandle)
    val morphTargetBufferCount: Int get() = FilaEngine_getMorphTargetBufferCount(nativeHandle)
    val instanceBufferCount: Int get() = FilaEngine_getInstanceBufferCount(nativeHandle)
    val vertexBufferCount: Int get() = FilaEngine_getVertexBufferCount(nativeHandle)
    val indirectLightCount: Int get() = FilaEngine_getIndirectLightCount(nativeHandle)
    val materialCount: Int get() = FilaEngine_getMaterialCount(nativeHandle)
    val textureCount: Int get() = FilaEngine_getTextureCount(nativeHandle)
    val skyboxeCount: Int get() = FilaEngine_getSkyboxeCount(nativeHandle)
    val colorGradingCount: Int get() = FilaEngine_getColorGradingCount(nativeHandle)
    val renderTargetCount: Int get() = FilaEngine_getRenderTargetCount(nativeHandle)

    /** Blocks until all pending commands have been executed by the GPU. */
    fun flushAndWait() {
        platform.makeCurrent()
        // Single-threaded wasm flushes synchronously and rejects a non-zero timeout.
        if (singleThreaded) FilaEngine_flushAndWait_uint64_t(nativeHandle, 0L) else FilaEngine_flushAndWait(nativeHandle)
    }
    /**
     * Blocks until all pending commands have been executed by the GPU, or [timeout] nanoseconds pass.
     *
     * @return false on timeout.
     */
    fun flushAndWait(timeout: Long): Boolean {
        platform.makeCurrent()
        return FilaEngine_flushAndWait_uint64_t(nativeHandle, if (singleThreaded) 0L else timeout)
    }
    /** Kicks the hardware thread (e.g. the OpenGL, Vulkan or Metal thread) without blocking. */
    fun flush() { platform.makeCurrent(); FilaEngine_flush(nativeHandle) }
    // Filament's pause needs threads (setPaused panics on single-threaded wasm): tracked locally there.
    private var paused = false
    /** Whether the render thread is paused. Set to pause or resume it. */
    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "state is only tracked locally — Filament's pause needs threads, which the wasm build doesn't have, so it has no effect on rendering.")
    var isPaused: Boolean
        get() = if (singleThreaded) paused else FilaEngine_isPaused(nativeHandle)
        set(value) { if (singleThreaded) paused = value else FilaEngine_setPaused(nativeHandle, value) }
    /**
     * Queues [command] to run asynchronously, in order with the other async calls (texture and buffer uploads), and
     * returns an ID for [cancelAsyncCall]. Meant for resource preparation such as asset loading; flooding it delays
     * those uploads. [onComplete] runs once on the main thread (see [pumpMessageQueues]): [AsyncCallStatus.COMPLETED]
     * if [command] ran, [AsyncCallStatus.CANCELED] if it never did. Needs [isAsynchronousModeEnabled].
     */
    fun runCommandAsync(command: () -> Unit, onComplete: ((AsyncCallStatus) -> Unit)? = null): Int {
        val commandUser = Callbacks.register(once = false) { command() }
        val user = Callbacks.registerStatus(once = true) { _, status ->
            Callbacks.release(commandUser) // the command ran or never will
            onComplete?.invoke(AsyncCallStatus.entries[status])
        }
        return FilaEngine_runCommandAsync(nativeHandle, Callbacks.userOnly, commandUser, NullPointer, Callbacks.userStatus, user)
    }

    /**
     * Cancels the async call [id] ([runCommandAsync], `setBufferAsync`, `setBufferAtAsync`, `setImageAsync`…). Its
     * completion callback still runs, with [AsyncCallStatus.CANCELED]. False if it's running, done or already canceled.
     */
    fun cancelAsyncCall(id: Int): Boolean = FilaEngine_cancelAsyncCall(nativeHandle, id)

    /** Runs the pending user callbacks now instead of later, e.g. once per frame after the vsync tick. */
    fun pumpMessageQueues() = FilaEngine_pumpMessageQueues(nativeHandle)
    /** Switches the command queue to unprotected mode, after a frame on a protected SwapChain. */
    fun unprotected() = FilaEngine_unprotected(nativeHandle)
    /** The default Material: 80% white, lit. Owned by the Engine. */
    val defaultMaterial: Material get() = Material(FilaEngine_getDefaultMaterial(nativeHandle))
    /** Runs the engine's pending work on this thread, for platforms without a render thread. */
    fun execute() = FilaEngine_execute(nativeHandle)

    /** The feature flags this Engine knows. */
    fun getFeatureFlags(): List<FeatureFlag> {
        val count = FilaEngine_getFeatureFlags(nativeHandle, NullPointer, 0)
        val handles = List(count) { FilaEngineFeatureFlag_create() }
        try {
            interopScope { FilaEngine_getFeatureFlags(nativeHandle, toInterop(handles), count) }
            return handles.map { f ->
                val name = stringFromInterop(FilaEngineFeatureFlag_getName(f)) ?: ""
                FeatureFlag(name, stringFromInterop(FilaEngineFeatureFlag_getDescription(f)) ?: "",
                    getFeatureFlag(name) ?: false, FilaEngineFeatureFlag_getConstant(f))
            }
        } finally {
            handles.forEach { FilaEngineFeatureFlag_destroy(it) }
        }
    }
    /** Whether a feature flag named [name] exists. */
    fun hasFeatureFlag(name: String): Boolean = name.useCString { FilaEngine_hasFeatureFlag(nativeHandle, it) }
    /** Sets a feature flag; constant flags can only be set on the [Builder]. @return false if it doesn't exist or is constant. */
    fun setFeatureFlag(name: String, value: Boolean): Boolean = name.useCString { FilaEngine_setFeatureFlag(nativeHandle, it, value) }
    /** A feature flag's value, or null if it doesn't exist. */
    fun getFeatureFlag(name: String): Boolean? = interopScope {
        val out = ByteArray(1)
        val ptr = toInterop(out)
        val present = FilaEngine_getFeatureFlag(nativeHandle, toInterop(name), ptr)
        ptr.fromInterop(out)
        if (present) out[0] != 0.toByte() else null
    }

    /** A tri-state for [compile]'s shadow-receiver and skinning variants. */
    enum class FeatureState {
        /** Feature is disabled. */
        FALSE,
        /** Feature is enabled. */
        TRUE,
        /** Feature state is uncertain; both variants are compiled. */
        INDETERMINATE
    }

    /**
     * Asynchronously compiles the variants of [material] needed to render it in [view], taking into account the
     * view's features (lighting, fog, stereo, shadowing) and [shadowReceiver] and [skinning].
     *
     * Call [flush] after several calls so the backend starts right away. [callback] is always called, on the main
     * thread, once the variants are compiled (or discarded, if the Engine is destroyed first).
     */
    fun compile(
        priority: Material.CompilerPriorityQueue,
        material: Material,
        view: View,
        shadowReceiver: FeatureState,
        skinning: FeatureState,
        callback: ((Material) -> Unit)? = null,
    ) {
        val userData = if (callback != null) Callbacks.register(once = true) { _ -> callback(material) } else NullPointer
        FilaEngine_compile(
            nativeHandle, priority.ordinal, material.nativeHandle, view.nativeHandle, shadowReceiver.ordinal, skinning.ordinal,
            NullPointer, if (callback != null) Callbacks.argUser else NullPointer, userData,
        )
    }
}

/** How an asynchronous call ended; its completion callback runs exactly once either way. */
enum class AsyncCallStatus {
    /** The operation ran to completion. */
    COMPLETED,
    /** The operation never ran: it was canceled ([Engine.cancelAsyncCall]) or dropped at shutdown. */
    CANCELED,
}

/** userData for an `argUserStatus` completion that hands [callback] the Kotlin object [wrap] makes of its argument. */
internal fun <T> asyncCompletion(wrap: (NativePointer) -> T, callback: (T, AsyncCallStatus) -> Unit): NativePointer =
    Callbacks.registerStatus(once = true) { arg, status -> callback(wrap(arg), AsyncCallStatus.entries[status]) }

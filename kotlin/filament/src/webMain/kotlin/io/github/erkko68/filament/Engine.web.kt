package io.github.erkko68.filament

import io.github.erkko68.filament.wasm.*
import kotlinx.browser.document
import org.w3c.dom.HTMLCanvasElement

/**
 * On web an engine is bound to one WebGL2 context, on [jsCanvas]. [glContext] is its Emscripten
 * handle (0 for the NOOP backend), made current again whenever a different engine was driven last.
 */
actual class Engine @InternalFilamentApi constructor(
    internal var nativeHandle: Int,
    internal val jsCanvas: HTMLCanvasElement? = null,
    private val glContext: Int = 0,
    // Only the hidden canvas we allocated is ours to tear down; a caller's canvas outlives us.
    private val ownsCanvas: Boolean = false,
) : AutoCloseable {
    private val mTransformManager by lazy { TransformManager(FilaEngine_getTransformManager(nativeHandle)) }
    private val mLightManager by lazy { LightManager(FilaEngine_getLightManager(nativeHandle)) }
    private val mRenderableManager by lazy { RenderableManager(FilaEngine_getRenderableManager(nativeHandle)) }
    private val mEntityManager by lazy { EntityManager(FilaEngine_getEntityManager(nativeHandle)) }
    // The C wrapper has no getConfig, so Builder.build() hands us the Config it was given.
    internal var mConfig: Config? = null

    actual enum class Backend {
        DEFAULT, OPENGL, VULKAN, METAL, WEBGPU, NOOP;
        internal fun toNative(): Int = ordinal
        companion object {
            internal fun fromNative(backend: Int): Backend = entries[backend]
        }
    }

    actual enum class FeatureLevel {
        FEATURE_LEVEL_0, FEATURE_LEVEL_1, FEATURE_LEVEL_2, FEATURE_LEVEL_3;
        internal fun toNative(): Int = ordinal
        companion object {
            internal fun fromNative(level: Int): FeatureLevel = entries[level]
        }
    }

    actual enum class StereoscopicType {
        NONE, INSTANCED, MULTIVIEW;
        internal fun toNative(): Int = ordinal
        companion object {
            internal fun fromNative(type: Int): StereoscopicType = entries[type]
        }
    }

    actual enum class GpuContextPriority {
        DEFAULT, LOW, MEDIUM, HIGH, REALTIME;
        internal fun toNative(): Int = ordinal
        companion object {
            internal fun fromNative(priority: Int): GpuContextPriority = entries[priority]
        }
    }

    actual class Config actual constructor() {
        actual var commandBufferSizeMB: Long = 3 * 1
        actual var perRenderPassArenaSizeMB: Long = 3
        actual var driverHandleArenaSizeMB: Long = 0
        actual var minCommandBufferSizeMB: Long = 1
        actual var perFrameCommandsSizeMB: Long = 2
        actual var jobSystemThreadCount: Long = 0
        actual var disableParallelShaderCompile: Boolean = false
        actual var stereoscopicType: StereoscopicType = StereoscopicType.NONE
        actual var stereoscopicEyeCount: Long = 2
        actual var resourceAllocatorCacheSizeMB: Long = 64
        actual var resourceAllocatorCacheMaxAge: Long = 1
        actual var disableHandleUseAfterFreeCheck: Boolean = false

        actual enum class ShaderLanguage {
            DEFAULT, MSL, METAL_LIBRARY;
        }
        actual var preferredShaderLanguage: ShaderLanguage = ShaderLanguage.DEFAULT
        actual var forceGLES2Context: Boolean = false
        actual var assertNativeWindowIsValid: Boolean = false
        actual var gpuContextPriority: GpuContextPriority = GpuContextPriority.DEFAULT
        actual var sharedUboInitialSizeInBytes: Long = 256 * 64
        actual var enableMultipleDirectionalLights: Boolean = false

        internal fun toNative(ptr: Int) = with(fila) {
            setI32(ptr + FilaEngineConfig.commandBufferSizeMB, commandBufferSizeMB.toInt())
            setI32(ptr + FilaEngineConfig.perRenderPassArenaSizeMB, perRenderPassArenaSizeMB.toInt())
            setI32(ptr + FilaEngineConfig.driverHandleArenaSizeMB, driverHandleArenaSizeMB.toInt())
            setI32(ptr + FilaEngineConfig.minCommandBufferSizeMB, minCommandBufferSizeMB.toInt())
            setI32(ptr + FilaEngineConfig.perFrameCommandsSizeMB, perFrameCommandsSizeMB.toInt())
            setI32(ptr + FilaEngineConfig.jobSystemThreadCount, jobSystemThreadCount.toInt())
            setBool(ptr + FilaEngineConfig.disableParallelShaderCompile, disableParallelShaderCompile)
            setI32(ptr + FilaEngineConfig.stereoscopicType, stereoscopicType.toNative())
            setU8(ptr + FilaEngineConfig.stereoscopicEyeCount, stereoscopicEyeCount.toInt())
            setI32(ptr + FilaEngineConfig.resourceAllocatorCacheSizeMB, resourceAllocatorCacheSizeMB.toInt())
            setU8(ptr + FilaEngineConfig.resourceAllocatorCacheMaxAge, resourceAllocatorCacheMaxAge.toInt())
            setBool(ptr + FilaEngineConfig.disableHandleUseAfterFreeCheck, disableHandleUseAfterFreeCheck)
            setI32(ptr + FilaEngineConfig.preferredShaderLanguage, preferredShaderLanguage.ordinal)
            setBool(ptr + FilaEngineConfig.forceGLES2Context, forceGLES2Context)
            setBool(ptr + FilaEngineConfig.assertNativeWindowIsValid, assertNativeWindowIsValid)
            setI32(ptr + FilaEngineConfig.gpuContextPriority, gpuContextPriority.toNative())
            setI32(ptr + FilaEngineConfig.sharedUboInitialSizeInBytes, sharedUboInitialSizeInBytes.toInt())
            setBool(ptr + FilaEngineConfig.enableMultipleDirectionalLights, enableMultipleDirectionalLights)
        }
    }

    actual class Builder actual constructor() {
        private val nativeBuilder = FilaEngineBuilder_create()
        private var mConfig: Config? = null
        private var backend = Backend.DEFAULT
        private var canvas: HTMLCanvasElement? = null

        actual fun backend(backend: Backend): Builder {
            this.backend = backend
            FilaEngineBuilder_backend(nativeBuilder, backend.toNative())
            return this
        }

        /** On web the shared context is the [HTMLCanvasElement] to render into; anything else is ignored. */
        actual fun sharedContext(sharedContext: Any): Builder {
            canvas = sharedContext as? HTMLCanvasElement
            return this
        }

        actual fun config(config: Config): Builder {
            fila.heapScoped {
                val ptr = alloc(FilaEngineConfig.SIZE)
                config.toNative(ptr)
                FilaEngineBuilder_config(nativeBuilder, ptr)
            }
            mConfig = config
            return this
        }

        actual fun featureLevel(featureLevel: FeatureLevel): Builder {
            FilaEngineBuilder_featureLevel(nativeBuilder, featureLevel.toNative())
            return this
        }

        actual fun paused(paused: Boolean): Builder {
            FilaEngineBuilder_paused(nativeBuilder, paused)
            return this
        }

        actual fun feature(name: String, value: Boolean): Builder {
            FilaEngineBuilder_feature(nativeBuilder, name, value)
            return this
        }

        actual fun colorGrading(colorGrading: ColorGrading.Builder): Builder {
            FilaEngineBuilder_colorGrading(nativeBuilder, colorGrading.nativeHandle)
            return this
        }

        actual fun build(): Engine {
            // WebGL is the only GPU backend in the wasm build: DEFAULT and OPENGL both mean it.
            val gpu = backend != Backend.NOOP
            val target = canvas ?: if (gpu) hiddenCanvas() else null
            val context = if (gpu && target != null) fila.createGlContext(target, alpha = true).also { current = it } else 0
            val handle = FilaEngineBuilder_build(nativeBuilder)
            FilaEngineBuilder_destroy(nativeBuilder)
            check(handle != 0) { "Failed to build Engine" }
            return Engine(handle, target, context, ownsCanvas = target != null && canvas == null)
                .apply { mConfig = this@Builder.mConfig }
        }

        // Parked off-screen on body until a consumer (e.g. FilamentView) adopts it.
        private fun hiddenCanvas(): HTMLCanvasElement =
            (document.createElement("canvas") as HTMLCanvasElement).apply {
                width = 1
                height = 1
                style.position = "absolute"
                style.left = "-9999px"
                style.top = "0"
                document.body?.appendChild(this)
            }
    }

    actual companion object {
        actual fun create(): Engine = Builder().build()
        actual fun create(backend: Backend): Engine = Builder().backend(backend).build()
        actual fun create(sharedContext: Any): Engine = Builder().sharedContext(sharedContext).build()
        actual val steadyClockTimeNano: Long get() = FilaEngine_getSteadyClockTimeNano()

        // GL context handle last made current; the Emscripten GL layer has one global current context.
        private var current = 0
    }

    /** Makes this engine's WebGL context current if another engine's is (called before driving GL). */
    internal fun makeCurrent() {
        if (glContext != 0 && current != glContext) {
            fila.GL.makeContextCurrent(glContext)
            current = glContext
        }
    }

    actual val isValid: Boolean get() = nativeHandle != 0
    actual override fun close() = destroy()

    actual fun destroy() {
        if (nativeHandle == 0) return
        makeCurrent()
        FilaEngine_destroy(nativeHandle)
        nativeHandle = 0
        if (glContext != 0) {
            jsCanvas?.let { fila.releaseGlContext(it, glContext) }
            if (current == glContext) current = 0
        }
        if (ownsCanvas) jsCanvas?.remove()
    }

    actual val backend: Backend get() = Backend.fromNative(FilaEngine_getBackend(nativeHandle))
    actual val supportedFeatureLevel: FeatureLevel get() = FeatureLevel.fromNative(FilaEngine_getSupportedFeatureLevel(nativeHandle))
    actual var activeFeatureLevel: FeatureLevel
        get() = FeatureLevel.fromNative(FilaEngine_getActiveFeatureLevel(nativeHandle))
        set(value) { FilaEngine_setActiveFeatureLevel(nativeHandle, value.toNative()) }

    actual var isAutomaticInstancingEnabled: Boolean
        get() = FilaEngine_isAutomaticInstancingEnabled(nativeHandle)
        set(value) { FilaEngine_setAutomaticInstancingEnabled(nativeHandle, value) }
    actual val config: Config get() = mConfig ?: Config()
    actual val maxStereoscopicEyes: Long get() = FilaEngine_getMaxStereoscopicEyes(nativeHandle).toLong()

    actual fun isValidRenderer(renderer: Renderer): Boolean = FilaEngine_isValidRenderer(nativeHandle, renderer.nativeHandle)
    actual fun isValidView(view: View): Boolean = FilaEngine_isValidView(nativeHandle, view.nativeHandle)
    actual fun isValidScene(scene: Scene): Boolean = FilaEngine_isValidScene(nativeHandle, scene.nativeHandle)
    actual fun isValidFence(fence: Fence): Boolean = FilaEngine_isValidFence(nativeHandle, fence.nativeHandle)
    actual fun isValidIndexBuffer(indexBuffer: IndexBuffer): Boolean = FilaEngine_isValidIndexBuffer(nativeHandle, indexBuffer.nativeHandle)
    actual fun isValidVertexBuffer(vertexBuffer: VertexBuffer): Boolean = FilaEngine_isValidVertexBuffer(nativeHandle, vertexBuffer.nativeHandle)
    actual fun isValidSkinningBuffer(skinningBuffer: SkinningBuffer): Boolean = FilaEngine_isValidSkinningBuffer(nativeHandle, skinningBuffer.nativeHandle)
    actual fun isValidMorphTargetBuffer(morphTargetBuffer: MorphTargetBuffer): Boolean = FilaEngine_isValidMorphTargetBuffer(nativeHandle, morphTargetBuffer.nativeHandle)
    actual fun isValidIndirectLight(ibl: IndirectLight): Boolean = FilaEngine_isValidIndirectLight(nativeHandle, ibl.nativeHandle)
    actual fun isValidMaterial(material: Material): Boolean = FilaEngine_isValidMaterial(nativeHandle, material.nativeHandle)
    actual fun isValidMaterialInstance(material: Material, materialInstance: MaterialInstance): Boolean = FilaEngine_isValidMaterialInstance(nativeHandle, material.nativeHandle, materialInstance.nativeHandle)
    actual fun isValidExpensiveMaterialInstance(materialInstance: MaterialInstance): Boolean = FilaEngine_isValidExpensiveMaterialInstance(nativeHandle, materialInstance.nativeHandle)
    actual fun isValidSkybox(skybox: Skybox): Boolean = FilaEngine_isValidSkybox(nativeHandle, skybox.nativeHandle)
    actual fun isValidColorGrading(colorGrading: ColorGrading): Boolean = FilaEngine_isValidColorGrading(nativeHandle, colorGrading.nativeHandle)
    actual fun isValidTexture(texture: Texture): Boolean = FilaEngine_isValidTexture(nativeHandle, texture.nativeHandle)
    actual fun isValidRenderTarget(renderTarget: RenderTarget): Boolean = FilaEngine_isValidRenderTarget(nativeHandle, renderTarget.nativeHandle)
    actual fun isValidStream(stream: Stream): Boolean = FilaEngine_isValidStream(nativeHandle, stream.nativeHandle)
    actual fun isValidSwapChain(swapChain: SwapChain): Boolean = FilaEngine_isValidSwapChain(nativeHandle, swapChain.nativeHandle)

    actual fun createSwapChain(surface: NativeSurface): SwapChain = SwapChain(FilaEngine_createSwapChain(nativeHandle, 0, 0L))
    actual fun createSwapChain(surface: NativeSurface, flags: Long): SwapChain = SwapChain(FilaEngine_createSwapChain(nativeHandle, 0, flags))
    actual fun createSwapChain(width: Int, height: Int, flags: Long): SwapChain = SwapChain(FilaEngine_createSwapChainHeadless(nativeHandle, width, height, flags))
    actual fun destroySwapChain(swapChain: SwapChain) {
        FilaEngine_destroySwapChain(nativeHandle, swapChain.nativeHandle)
        swapChain.nativeHandle = 0
        swapChain.releaseCallbackStubs()
    }

    actual fun createView(): View = View(FilaEngine_createView(nativeHandle))
    actual fun destroyView(view: View) {
        FilaEngine_destroyView(nativeHandle, view.nativeHandle)
        view.nativeHandle = 0
    }

    actual fun createRenderer(): Renderer = Renderer(FilaEngine_createRenderer(nativeHandle)).setEngine(this)
    actual fun destroyRenderer(renderer: Renderer) {
        FilaEngine_destroyRenderer(nativeHandle, renderer.nativeHandle)
        renderer.nativeHandle = 0
    }

    actual fun createCamera(): Camera {
        val handle = FilaEngine_createCameraAuto(nativeHandle)
        val entity = FilaCamera_getEntity(handle)
        return Camera(handle, entity)
    }
    actual fun createCamera(entity: Entity): Camera = Camera(FilaEngine_createCamera(nativeHandle, entity), entity)
    actual fun getCameraComponent(entity: Entity): Camera? {
        val handle = FilaEngine_getCameraComponent(nativeHandle, entity)
        return if (handle != 0) Camera(handle, entity) else null
    }
    actual fun destroyCamera(camera: Camera) {
        FilaEngine_destroyCamera(nativeHandle, camera.nativeHandle)
        camera.nativeHandle = 0
    }
    actual fun destroyCameraComponent(entity: Entity) = FilaEngine_destroyCameraComponent(nativeHandle, entity)

    actual fun createScene(): Scene = Scene(FilaEngine_createScene(nativeHandle))
    actual fun destroyScene(scene: Scene) {
        FilaEngine_destroyScene(nativeHandle, scene.nativeHandle)
        scene.nativeHandle = 0
    }

    actual fun createFence(): Fence = Fence(FilaEngine_createFence(nativeHandle), nativeHandle)
    actual fun destroyFence(fence: Fence) {
        FilaEngine_destroyFence(nativeHandle, fence.nativeHandle)
        fence.nativeHandle = 0
    }

    actual fun destroyIndexBuffer(indexBuffer: IndexBuffer) {
        FilaEngine_destroyIndexBuffer(nativeHandle, indexBuffer.nativeHandle)
        indexBuffer.nativeHandle = 0
    }
    actual fun destroyVertexBuffer(vertexBuffer: VertexBuffer) {
        FilaEngine_destroyVertexBuffer(nativeHandle, vertexBuffer.nativeHandle)
        vertexBuffer.nativeHandle = 0
    }
    actual fun destroySkinningBuffer(skinningBuffer: SkinningBuffer) {
        FilaEngine_destroySkinningBuffer(nativeHandle, skinningBuffer.nativeHandle)
        skinningBuffer.nativeHandle = 0
    }
    actual fun destroyMorphTargetBuffer(morphTargetBuffer: MorphTargetBuffer) {
        FilaEngine_destroyMorphTargetBuffer(nativeHandle, morphTargetBuffer.nativeHandle)
        morphTargetBuffer.nativeHandle = 0
    }
    actual fun destroyIndirectLight(ibl: IndirectLight) {
        FilaEngine_destroyIndirectLight(nativeHandle, ibl.nativeHandle)
        ibl.nativeHandle = 0
    }
    actual fun destroyMaterial(material: Material) {
        FilaEngine_destroyMaterial(nativeHandle, material.nativeHandle)
    }
    actual fun destroyMaterialInstance(materialInstance: MaterialInstance) {
        FilaEngine_destroyMaterialInstance(nativeHandle, materialInstance.nativeHandle)
    }
    actual fun destroySkybox(skybox: Skybox) {
        FilaEngine_destroySkybox(nativeHandle, skybox.nativeHandle)
        skybox.nativeHandle = 0
    }
    actual fun destroyColorGrading(colorGrading: ColorGrading) {
        FilaEngine_destroyColorGrading(nativeHandle, colorGrading.nativeHandle)
        colorGrading.nativeHandle = 0
    }
    actual fun destroyTexture(texture: Texture) {
        FilaEngine_destroyTexture(nativeHandle, texture.nativeHandle)
    }
    actual fun destroyRenderTarget(target: RenderTarget) {
        FilaEngine_destroyRenderTarget(nativeHandle, target.nativeHandle)
    }
    actual fun destroyStream(stream: Stream) {
        FilaEngine_destroyStream(nativeHandle, stream.nativeHandle)
        stream.nativeHandle = 0
    }
    actual fun destroyEntity(entity: Entity) = FilaEntityManager_destroy(FilaEngine_getEntityManager(nativeHandle), entity)

    actual val transformManager: TransformManager get() = mTransformManager
    actual val lightManager: LightManager get() = mLightManager
    actual val renderableManager: RenderableManager get() = mRenderableManager
    actual val entityManager: EntityManager get() = mEntityManager

    actual fun flushAndWait() { flushAndWait(0L) }
    // Single-threaded wasm: the flush runs synchronously and Filament rejects a non-zero timeout.
    actual fun flushAndWait(timeout: Long): Boolean { makeCurrent(); return FilaEngine_flushAndWait(nativeHandle, 0L) }
    actual fun flush() { makeCurrent(); FilaEngine_flush(nativeHandle) }
    actual val hasUnrecoverableFailure: Boolean get() = FilaEngine_hasUnrecoverableFailure(nativeHandle)
    // Filament's pause is multi-threaded only (setPaused panics on single-threaded wasm).
    private var paused = false
    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "state is only tracked locally — Filament's pause needs threads, which the wasm build doesn't have, so it has no effect on rendering.")
    actual var isPaused: Boolean
        get() = paused
        set(value) { paused = value }
    actual fun unprotected() = FilaEngine_unprotected(nativeHandle)
    actual fun hasFeatureFlag(name: String): Boolean = FilaEngine_hasFeatureFlag(nativeHandle, name)
    actual fun setFeatureFlag(name: String, value: Boolean): Boolean {
        FilaEngine_setFeatureFlag(nativeHandle, name, value)
        return true
    }
    actual fun getFeatureFlag(name: String): Boolean = FilaEngine_getFeatureFlag(nativeHandle, name)

    actual fun enableAccurateTranslations() = FilaEngine_enableAccurateTranslations(nativeHandle)

    actual enum class CompilerPriorityQueue { CRITICAL, HIGH, LOW }
    actual enum class FeatureState { FALSE, TRUE, INDETERMINATE }

    actual fun compile(priority: CompilerPriorityQueue, material: Material, view: View, shadowReceiver: FeatureState, skinning: FeatureState, callback: (() -> Unit)?) {
        val userData = if (callback != null) Callbacks.register(once = true) { _, _ -> callback() } else 0
        FilaEngine_compile(
            nativeHandle,
            priority.ordinal,
            material.nativeHandle,
            view.nativeHandle,
            shadowReceiver.ordinal,
            skinning.ordinal,
            if (callback != null) Callbacks.userOnly else 0,
            userData,
        )
    }
}

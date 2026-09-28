package io.github.erkko68.filament

import android.opengl.EGLContext
import io.github.erkko68.filament.jni.*

actual class Engine @InternalFilamentApi constructor(internal var nativeHandle: Long) : AutoCloseable {
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

        internal fun toNative(native: FilaEngineConfig) {
            native.commandBufferSizeMB = commandBufferSizeMB.toInt()
            native.perRenderPassArenaSizeMB = perRenderPassArenaSizeMB.toInt()
            native.driverHandleArenaSizeMB = driverHandleArenaSizeMB.toInt()
            native.minCommandBufferSizeMB = minCommandBufferSizeMB.toInt()
            native.perFrameCommandsSizeMB = perFrameCommandsSizeMB.toInt()
            native.jobSystemThreadCount = jobSystemThreadCount.toInt()
            native.disableParallelShaderCompile = disableParallelShaderCompile
            native.stereoscopicType = stereoscopicType.toNative()
            native.stereoscopicEyeCount = stereoscopicEyeCount.toInt()
            native.resourceAllocatorCacheSizeMB = resourceAllocatorCacheSizeMB.toInt()
            native.resourceAllocatorCacheMaxAge = resourceAllocatorCacheMaxAge.toInt()
            native.disableHandleUseAfterFreeCheck = disableHandleUseAfterFreeCheck
            native.preferredShaderLanguage = preferredShaderLanguage.ordinal
            native.forceGLES2Context = forceGLES2Context
            native.assertNativeWindowIsValid = assertNativeWindowIsValid
            native.gpuContextPriority = gpuContextPriority.toNative()
            native.sharedUboInitialSizeInBytes = sharedUboInitialSizeInBytes.toInt()
            native.enableMultipleDirectionalLights = enableMultipleDirectionalLights
        }
    }

    actual class Builder actual constructor() {
        private val nativeBuilder = FilaEngineBuilder_create()
        private var mConfig: Config? = null

        actual fun backend(backend: Backend): Builder {
            FilaEngineBuilder_backend(nativeBuilder, backend.toNative())
            return this
        }

        /** On Android the shared context is an [EGLContext] (or its native handle as a Long), as in filament-android. */
        actual fun sharedContext(sharedContext: Any): Builder {
            val handle = when (sharedContext) {
                is EGLContext -> sharedContext.nativeHandle
                is Long -> sharedContext
                else -> throw IllegalArgumentException("sharedContext must be an EGLContext, got ${sharedContext::class}")
            }
            FilaEngineBuilder_sharedContext(nativeBuilder, handle)
            return this
        }

        actual fun config(config: Config): Builder {
            heapScoped {
                val native = FilaEngineConfig(alloc(FilaEngineConfig.SIZE))
                config.toNative(native)
                FilaEngineBuilder_config(nativeBuilder, native.ptr)
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
            val handle = FilaEngineBuilder_build(nativeBuilder)
            FilaEngineBuilder_destroy(nativeBuilder)
            check(handle != 0L) { "Failed to build Engine" }
            return Engine(handle).apply { mConfig = this@Builder.mConfig }
        }
    }

    actual companion object {
        actual fun create(): Engine = Builder().build()
        actual fun create(backend: Backend): Engine = Builder().backend(backend).build()
        actual fun create(sharedContext: Any): Engine = Builder().sharedContext(sharedContext).build()
        actual val steadyClockTimeNano: Long get() = FilaEngine_getSteadyClockTimeNano()
    }

    actual val isValid: Boolean get() = nativeHandle != 0L
    actual override fun close() = destroy()

    actual fun destroy() {
        if (nativeHandle == 0L) return
        FilaEngine_destroy(nativeHandle)
        nativeHandle = 0
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
    actual val maxStereoscopicEyes: Long get() = FilaEngine_getMaxStereoscopicEyes(nativeHandle)

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

    actual fun createSwapChain(surface: NativeSurface): SwapChain = createSwapChain(surface, 0L)
    // Holds an ANativeWindow reference until destroySwapChain; EGL takes its own when it builds the surface.
    actual fun createSwapChain(surface: NativeSurface, flags: Long): SwapChain {
        val androidSurface = requireNotNull(surface.surface as? android.view.Surface) {
            "NativeSurface must wrap an android.view.Surface, got ${surface.surface::class}"
        }
        val window = FilaAndroid.windowFromSurface(androidSurface)
        check(window != 0L) { "No ANativeWindow for $androidSurface (released?)" }
        return SwapChain(FilaEngine_createSwapChain(nativeHandle, window, flags), window)
    }
    actual fun createSwapChain(width: Int, height: Int, flags: Long): SwapChain = SwapChain(FilaEngine_createSwapChainHeadless(nativeHandle, width, height, flags))
    actual fun destroySwapChain(swapChain: SwapChain) {
        FilaEngine_destroySwapChain(nativeHandle, swapChain.nativeHandle)
        swapChain.nativeHandle = 0
        swapChain.releaseCallbackStubs()
        if (swapChain.window != 0L) FilaAndroid.releaseWindow(swapChain.window)
        swapChain.window = 0L
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
        return if (handle != 0L) Camera(handle, entity) else null
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

    actual fun createFence(): Fence = Fence(FilaEngine_createFence(nativeHandle))
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

    actual fun flushAndWait() { FilaEngine_flushAndWait(nativeHandle, 1_000_000_000L) }
    actual fun flushAndWait(timeout: Long): Boolean = FilaEngine_flushAndWait(nativeHandle, timeout)
    actual fun flush() = FilaEngine_flush(nativeHandle)
    actual val hasUnrecoverableFailure: Boolean get() = FilaEngine_hasUnrecoverableFailure(nativeHandle)
    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "state is only tracked locally — Filament's pause needs threads, which the wasm build doesn't have, so it has no effect on rendering.")
    actual var isPaused: Boolean
        get() = FilaEngine_isPaused(nativeHandle)
        set(value) { FilaEngine_setPaused(nativeHandle, value) }
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

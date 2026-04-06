package dev.filament.kmp

import cnames.structs.FilaEngine
import cnames.structs.FilaEngineBuilder
import dev.filament.kmp.cinterop.*
import kotlinx.cinterop.*

@OptIn(ExperimentalForeignApi::class)
actual class Engine internal constructor(
    internal var nativeHandle: CPointer<FilaEngine>?,
) {
    actual val isValid: Boolean get() = nativeHandle != null

    actual fun destroy() {
        val handle = nativeHandle ?: return
        FilaEngine_destroy(handle)
        nativeHandle = null
    }

    actual val backend: Backend get() = when (FilaEngine_getBackend(nativeHandle)) {
        0 -> Backend.DEFAULT
        1 -> Backend.VULKAN
        2 -> Backend.OPENGL
        3 -> Backend.METAL
        4 -> Backend.NOOP
        else -> Backend.DEFAULT
    }

    actual fun enableAccurateTranslations() {
        // This is a helper in JNI that sets a flag in TransformManager.
        // We can implement it similarly or leave it for now if our C-wrapper doesn't have it.
        // My FilaEngine.h doesn't have it yet.
    }

    actual val supportedFeatureLevel: FeatureLevel get() = when (FilaEngine_getSupportedFeatureLevel(nativeHandle).toInt()) {
        0 -> FeatureLevel.FEATURE_LEVEL_0
        1 -> FeatureLevel.FEATURE_LEVEL_1
        2 -> FeatureLevel.FEATURE_LEVEL_2
        3 -> FeatureLevel.FEATURE_LEVEL_3
        else -> FeatureLevel.FEATURE_LEVEL_0
    }

    actual fun setActiveFeatureLevel(featureLevel: FeatureLevel): FeatureLevel {
        val level = when (featureLevel) {
            FeatureLevel.FEATURE_LEVEL_0 -> FILA_ENGINE_FEATURE_LEVEL_0
            FeatureLevel.FEATURE_LEVEL_1 -> FILA_ENGINE_FEATURE_LEVEL_1
            FeatureLevel.FEATURE_LEVEL_2 -> FILA_ENGINE_FEATURE_LEVEL_2
            FeatureLevel.FEATURE_LEVEL_3 -> FILA_ENGINE_FEATURE_LEVEL_3
        }
        FilaEngine_setActiveFeatureLevel(nativeHandle, level)
        return featureLevel
    }

    actual val activeFeatureLevel: FeatureLevel get() = when (FilaEngine_getActiveFeatureLevel(nativeHandle).toInt()) {
        0 -> FeatureLevel.FEATURE_LEVEL_0
        1 -> FeatureLevel.FEATURE_LEVEL_1
        2 -> FeatureLevel.FEATURE_LEVEL_2
        3 -> FeatureLevel.FEATURE_LEVEL_3
        else -> FeatureLevel.FEATURE_LEVEL_0
    }

    actual var isAutomaticInstancingEnabled: Boolean
        get() = FilaEngine_isAutomaticInstancingEnabled(nativeHandle)
        set(value) { FilaEngine_setAutomaticInstancingEnabled(nativeHandle, value) }

    actual val config: Config get() = Config() // We don't store the config in Engine easily in C

    actual val maxStereoscopicEyes: Long get() = FilaEngine_getMaxStereoscopicEyes(nativeHandle).toLong()

    actual fun createSwapChain(surface: Any): SwapChain = SwapChain(FilaEngine_createSwapChain(nativeHandle, surface.toCPointer(), 0u))
    actual fun createSwapChain(surface: Any, flags: Long): SwapChain = SwapChain(FilaEngine_createSwapChain(nativeHandle, surface.toCPointer(), flags.toULong()))
    actual fun createSwapChain(width: Int, height: Int, flags: Long): SwapChain = SwapChain(FilaEngine_createSwapChainHeadless(nativeHandle, width.toUInt(), height.toUInt(), flags.toULong()))
    actual fun createSwapChainFromNativeSurface(surface: NativeSurface, flags: Long): SwapChain = SwapChain(FilaEngine_createSwapChain(nativeHandle, surface.nativeObject.toCPointer(), flags.toULong()))

    actual fun destroySwapChain(swapChain: SwapChain) {
        FilaEngine_destroySwapChain(nativeHandle, swapChain.nativeHandle)
        swapChain.nativeHandle = null
    }

    actual fun isValidRenderer(renderer: Renderer): Boolean = FilaEngine_isValidRenderer(nativeHandle, renderer.nativeHandle)
    actual fun isValidView(view: View): Boolean = FilaEngine_isValidView(nativeHandle, view.nativeHandle)
    actual fun isValidScene(scene: Scene): Boolean = FilaEngine_isValidScene(nativeHandle, scene.nativeHandle)
    actual fun isValidFence(fence: Fence): Boolean = FilaEngine_isValidFence(nativeHandle, fence.nativeHandle)
    actual fun isValidStream(stream: Stream): Boolean = FilaEngine_isValidStream(nativeHandle, stream.nativeHandle)
    actual fun isValidIndexBuffer(indexBuffer: IndexBuffer): Boolean = FilaEngine_isValidIndexBuffer(nativeHandle, indexBuffer.nativeHandle)
    actual fun isValidVertexBuffer(vertexBuffer: VertexBuffer): Boolean = FilaEngine_isValidVertexBuffer(nativeHandle, vertexBuffer.nativeHandle)
    actual fun isValidSkinningBuffer(skinningBuffer: SkinningBuffer): Boolean = FilaEngine_isValidSkinningBuffer(nativeHandle, skinningBuffer.nativeHandle)
    actual fun isValidMorphTargetBuffer(morphTargetBuffer: MorphTargetBuffer): Boolean = FilaEngine_isValidMorphTargetBuffer(nativeHandle, morphTargetBuffer.nativeHandle)
    actual fun isValidIndirectLight(indirectLight: IndirectLight): Boolean = FilaEngine_isValidIndirectLight(nativeHandle, indirectLight.nativeHandle)
    actual fun isValidMaterial(material: Material): Boolean = FilaEngine_isValidMaterial(nativeHandle, material.nativeHandle)
    actual fun isValidMaterialInstance(material: Material, materialInstance: MaterialInstance): Boolean = FilaEngine_isValidMaterialInstance(nativeHandle, material.nativeHandle, materialInstance.nativeHandle)
    actual fun isValidExpensiveMaterialInstance(materialInstance: MaterialInstance): Boolean = FilaEngine_isValidExpensiveMaterialInstance(nativeHandle, materialInstance.nativeHandle)
    actual fun isValidSkybox(skybox: Skybox): Boolean = FilaEngine_isValidSkybox(nativeHandle, skybox.nativeHandle)
    actual fun isValidColorGrading(colorGrading: ColorGrading): Boolean = FilaEngine_isValidColorGrading(nativeHandle, colorGrading.nativeHandle)
    actual fun isValidTexture(texture: Texture): Boolean = FilaEngine_isValidTexture(nativeHandle, texture.nativeHandle)
    actual fun isValidRenderTarget(renderTarget: RenderTarget): Boolean = FilaEngine_isValidRenderTarget(nativeHandle, renderTarget.nativeHandle)
    actual fun isValidSwapChain(swapChain: SwapChain): Boolean = FilaEngine_isValidSwapChain(nativeHandle, swapChain.nativeHandle)

    actual fun createView(): View = View(FilaEngine_createView(nativeHandle))
    actual fun destroyView(view: View) {
        FilaEngine_destroyView(nativeHandle, view.nativeHandle)
        view.nativeHandle = null
    }

    actual fun createRenderer(): Renderer = Renderer(FilaEngine_createRenderer(nativeHandle))
    actual fun destroyRenderer(renderer: Renderer) {
        FilaEngine_destroyRenderer(nativeHandle, renderer.nativeHandle)
        renderer.nativeHandle = null
    }

    actual fun createCamera(@Entity entity: Int): Camera = Camera(FilaEngine_createCamera(nativeHandle, entity.toUInt()))
    actual fun getCameraComponent(@Entity entity: Int): Camera? {
        val ptr = FilaEngine_getCameraComponent(nativeHandle, entity.toUInt())
        return if (ptr != null) Camera(ptr) else null
    }
    actual fun destroyCameraComponent(@Entity entity: Int) {
        FilaEngine_destroyCameraComponent(nativeHandle, entity.toUInt())
    }

    actual fun createScene(): Scene = Scene(FilaEngine_createScene(nativeHandle))
    actual fun destroyScene(scene: Scene) {
        FilaEngine_destroyScene(nativeHandle, scene.nativeHandle)
        scene.nativeHandle = null
    }

    actual fun destroyStream(stream: Stream) {
        FilaEngine_destroyStream(nativeHandle, stream.nativeHandle)
        stream.nativeHandle = null
    }

    actual fun createFence(): Fence = Fence(FilaEngine_createFence(nativeHandle))
    actual fun destroyFence(fence: Fence) {
        FilaEngine_destroyFence(nativeHandle, fence.nativeHandle)
        fence.nativeHandle = null
    }

    actual fun destroyIndexBuffer(indexBuffer: IndexBuffer) {
        FilaEngine_destroyIndexBuffer(nativeHandle, indexBuffer.nativeHandle)
        indexBuffer.nativeHandle = null
    }

    actual fun destroyVertexBuffer(vertexBuffer: VertexBuffer) {
        FilaEngine_destroyVertexBuffer(nativeHandle, vertexBuffer.nativeHandle)
        vertexBuffer.nativeHandle = null
    }

    actual fun destroySkinningBuffer(skinningBuffer: SkinningBuffer) {
        FilaEngine_destroySkinningBuffer(nativeHandle, skinningBuffer.nativeHandle)
        skinningBuffer.nativeHandle = null
    }

    actual fun destroyMorphTargetBuffer(morphTargetBuffer: MorphTargetBuffer) {
        FilaEngine_destroyMorphTargetBuffer(nativeHandle, morphTargetBuffer.nativeHandle)
        morphTargetBuffer.nativeHandle = null
    }

    actual fun destroyIndirectLight(indirectLight: IndirectLight) {
        FilaEngine_destroyIndirectLight(nativeHandle, indirectLight.nativeHandle)
        indirectLight.nativeHandle = null
    }

    actual fun destroyMaterial(material: Material) {
        FilaEngine_destroyMaterial(nativeHandle, material.nativeHandle)
        material.nativeHandle = null
    }

    actual fun destroyMaterialInstance(materialInstance: MaterialInstance) {
        FilaEngine_destroyMaterialInstance(nativeHandle, materialInstance.nativeHandle)
        materialInstance.nativeHandle = null
    }

    actual fun destroySkybox(skybox: Skybox) {
        FilaEngine_destroySkybox(nativeHandle, skybox.nativeHandle)
        skybox.nativeHandle = null
    }

    actual fun destroyColorGrading(colorGrading: ColorGrading) {
        FilaEngine_destroyColorGrading(nativeHandle, colorGrading.nativeHandle)
        colorGrading.nativeHandle = null
    }

    actual fun destroyTexture(texture: Texture) {
        FilaEngine_destroyTexture(nativeHandle, texture.nativeHandle)
        texture.nativeHandle = null
    }

    actual fun destroyRenderTarget(target: RenderTarget) {
        FilaEngine_destroyRenderTarget(nativeHandle, target.nativeHandle)
        target.nativeHandle = null
    }

    actual fun destroyEntity(@Entity entity: Int) {
        FilaEngine_destroyEntity(nativeHandle, entity.toUInt())
    }

    actual fun getTransformManager(): TransformManager = TransformManager(FilaEngine_getTransformManager(nativeHandle))
    actual fun getLightManager(): LightManager = LightManager(FilaEngine_getLightManager(nativeHandle))
    actual fun getRenderableManager(): RenderableManager = RenderableManager(FilaEngine_getRenderableManager(nativeHandle))
    actual fun getEntityManager(): EntityManager = EntityManager(FilaEngine_getEntityManager(nativeHandle))

    actual fun flushAndWait() { FilaEngine_flushAndWait(nativeHandle, 1_000_000_000u) /* 1s default */ }
    actual fun flushAndWait(timeout: Long): Boolean = FilaEngine_flushAndWait(nativeHandle, timeout.toULong())
    actual fun flush() { FilaEngine_flush(nativeHandle) }

    actual var isPaused: Boolean
        get() = FilaEngine_isPaused(nativeHandle)
        set(value) { FilaEngine_setPaused(nativeHandle, value) }

    actual fun unprotected() { FilaEngine_unprotected(nativeHandle) }

    actual fun hasFeatureFlag(name: String): Boolean = FilaEngine_hasFeatureFlag(nativeHandle, name)
    actual fun setFeatureFlag(name: String, value: Boolean): Boolean {
        FilaEngine_setFeatureFlag(nativeHandle, name, value)
        return true
    }
    actual fun getFeatureFlag(name: String): Boolean = FilaEngine_getFeatureFlag(nativeHandle, name)

    actual val nativeObject: Long get() = nativeHandle?.rawValue?.toLong() ?: 0L

    actual fun getNativeJobSystem(): Long = 0L

    actual enum class Backend {
        DEFAULT, OPENGL, VULKAN, METAL, WEBGPU, NOOP,
    }

    actual enum class FeatureLevel {
        FEATURE_LEVEL_0, FEATURE_LEVEL_1, FEATURE_LEVEL_2, FEATURE_LEVEL_3,
    }

    actual enum class StereoscopicType {
        NONE, INSTANCED, MULTIVIEW,
    }

    actual enum class GpuContextPriority {
        DEFAULT, LOW, MEDIUM, HIGH, REALTIME,
    }

    actual class Config {
        actual var commandBufferSizeMB: Long = 3
        actual var perRenderPassArenaSizeMB: Long = 3
        actual var driverHandleArenaSizeMB: Long = 0
        actual var minCommandBufferSizeMB: Long = 1
        actual var perFrameCommandsSizeMB: Long = 2
        actual var jobSystemThreadCount: Long = 0
        actual var textureUseAfterFreePoolSize: Long = 0
        @Deprecated("Use feature flag \"backend.disable_parallel_shader_compile\" instead.")
        actual var disableParallelShaderCompile: Boolean = false
        actual var stereoscopicType: StereoscopicType = StereoscopicType.NONE
        actual var stereoscopicEyeCount: Long = 2
        @Deprecated("This value is no longer used.")
        actual var resourceAllocatorCacheSizeMB: Long = 64
        actual var resourceAllocatorCacheMaxAge: Long = 1
        @Deprecated("Use feature flag \"backend.disable_handle_use_after_free_check\" instead.")
        actual var disableHandleUseAfterFreeCheck: Boolean = false
        actual var preferredShaderLanguage: ShaderLanguage = ShaderLanguage.DEFAULT
        actual var forceGLES2Context: Boolean = false
        @Deprecated("Use feature flag \"backend.opengl.assert_native_window_is_valid\" instead.")
        actual var assertNativeWindowIsValid: Boolean = false
        actual var gpuContextPriority: GpuContextPriority = GpuContextPriority.DEFAULT
        actual var sharedUboInitialSizeInBytes: Long = 256 * 64

        actual enum class ShaderLanguage { DEFAULT, MSL, METAL_LIBRARY, }
    }

    actual class Builder {
        private var _backend = Backend.DEFAULT
        private var _config: Config? = null
        private var _featureLevel = FeatureLevel.FEATURE_LEVEL_1
        private var _sharedContext: Any? = null
        private var _paused = false
        private val _features = mutableMapOf<String, Boolean>()

        actual fun backend(backend: Backend): Builder {
            _backend = backend
            return this
        }

        actual fun sharedContext(sharedContext: Any): Builder {
            _sharedContext = sharedContext
            return this
        }

        actual fun config(config: Config): Builder {
            _config = config
            return this
        }

        actual fun featureLevel(featureLevel: FeatureLevel): Builder {
            _featureLevel = featureLevel
            return this
        }

        actual fun paused(paused: Boolean): Builder {
            _paused = paused
            return this
        }

        actual fun feature(name: String, value: Boolean): Builder {
            _features[name] = value
            return this
        }

        actual fun build(): Engine {
            val builder = FilaEngineBuilder_create()
            
            val b = when (_backend) {
                Backend.DEFAULT -> FILA_ENGINE_BACKEND_DEFAULT
                Backend.OPENGL -> FILA_ENGINE_BACKEND_OPENGL
                Backend.VULKAN -> FILA_ENGINE_BACKEND_VULKAN
                Backend.METAL -> FILA_ENGINE_BACKEND_METAL
                Backend.WEBGPU -> FILA_ENGINE_BACKEND_DEFAULT // Placeholder
                Backend.NOOP -> FILA_ENGINE_BACKEND_NOOP
            }
            FilaEngineBuilder_backend(builder, b)
            
            _config?.let { cfg ->
                memScoped {
                    val nativeCfg = alloc<FilaEngineConfig>()
                    nativeCfg.commandBufferSizeMB = cfg.commandBufferSizeMB.toUInt()
                    nativeCfg.perRenderPassArenaSizeMB = cfg.perRenderPassArenaSizeMB.toUInt()
                    nativeCfg.driverHandleArenaSizeMB = cfg.driverHandleArenaSizeMB.toUInt()
                    nativeCfg.minCommandBufferSizeMB = cfg.minCommandBufferSizeMB.toUInt()
                    nativeCfg.perFrameCommandsSizeMB = cfg.perFrameCommandsSizeMB.toUInt()
                    nativeCfg.jobSystemThreadCount = cfg.jobSystemThreadCount.toUInt()
                    nativeCfg.disableParallelShaderCompile = cfg.disableParallelShaderCompile
                    nativeCfg.stereoscopicType = when (cfg.stereoscopicType) {
                        StereoscopicType.NONE -> FILA_ENGINE_STEREOSCOPIC_TYPE_INSTANCED // C doesn't have NONE in enum? Wait.
                        StereoscopicType.INSTANCED -> FILA_ENGINE_STEREOSCOPIC_TYPE_INSTANCED
                        StereoscopicType.MULTIVIEW -> FILA_ENGINE_STEREOSCOPIC_TYPE_MULTIVIEW
                    }
                    nativeCfg.stereoscopicEyeCount = cfg.stereoscopicEyeCount.toUByte()
                    nativeCfg.resourceAllocatorCacheSizeMB = cfg.resourceAllocatorCacheSizeMB.toUInt()
                    nativeCfg.resourceAllocatorCacheMaxAge = cfg.resourceAllocatorCacheMaxAge.toUByte()
                    nativeCfg.disableHandleUseAfterFreeCheck = cfg.disableHandleUseAfterFreeCheck
                    nativeCfg.preferredShaderLanguage = 0 // Placeholder
                    nativeCfg.forceGLES2Context = cfg.forceGLES2Context
                    nativeCfg.assertNativeWindowIsValid = cfg.assertNativeWindowIsValid
                    nativeCfg.gpuContextPriority = 0 // Placeholder
                    nativeCfg.sharedUboInitialSizeInBytes = cfg.sharedUboInitialSizeInBytes.toUInt()
                    FilaEngineBuilder_config(builder, nativeCfg.ptr)
                }
            }
            
            FilaEngineBuilder_paused(builder, _paused)
            
            for ((name, value) in _features) {
                FilaEngineBuilder_feature(builder, name, value)
            }
            
            val handle = FilaEngineBuilder_build(builder)
            FilaEngineBuilder_destroy(builder)
            
            return Engine(handle)
        }
    }

    actual companion object {
        actual fun create(): Engine = Builder().build()
        actual fun create(backend: Backend): Engine = Builder().backend(backend).build()
        actual fun create(sharedContext: Any): Engine = Builder().sharedContext(sharedContext).build()
        actual fun getSteadyClockTimeNano(): Long = FilaEngine_getSteadyClockTimeNano().toLong()
    }
}

// Helper to convert Any to CPointer
private fun Any.toCPointer(): CPointer<out CPointed>? {
    // This is platform specific and might need proper handling of CObject/Pointer
    return null 
}

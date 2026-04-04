package dev.filament.kmp

import cnames.structs.FilaEngine
import dev.filament.kmp.cinterop.FILA_ENGINE_STEREOSCOPIC_TYPE_INSTANCED
import dev.filament.kmp.cinterop.FILA_ENGINE_STEREOSCOPIC_TYPE_MULTIVIEW
import dev.filament.kmp.cinterop.FILA_ENGINE_STEREOSCOPIC_TYPE_NONE
import dev.filament.kmp.cinterop.FilaEngineConfig
import dev.filament.kmp.cinterop.FilaEngine_createWithConfig
import dev.filament.kmp.cinterop.FilaEngine_destroy
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CPointerVar
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value

@OptIn(ExperimentalForeignApi::class)
actual class Engine private constructor(
    private var nativeHandle: CPointer<FilaEngine>?,
) {
    actual fun isValid(): Boolean = nativeHandle != null

    actual fun destroy() {
        val handle = nativeHandle ?: return
        memScoped {
            val handleVar = alloc<CPointerVar<FilaEngine>>()
            handleVar.value = handle
            FilaEngine_destroy(handleVar.ptr)
        }
        nativeHandle = null
    }

    actual fun getBackend(): Backend = Backend.DEFAULT

    actual fun enableAccurateTranslations() {
        TODO("Not yet implemented")
    }

    actual fun getSupportedFeatureLevel(): FeatureLevel = TODO("Not yet implemented")

    actual fun setActiveFeatureLevel(featureLevel: FeatureLevel): FeatureLevel = TODO("Not yet implemented")

    actual fun getActiveFeatureLevel(): FeatureLevel = TODO("Not yet implemented")

    actual fun setAutomaticInstancingEnabled(enable: Boolean) {
        TODO("Not yet implemented")
    }

    actual fun isAutomaticInstancingEnabled(): Boolean = TODO("Not yet implemented")

    actual fun getConfig(): Config = Config()

    actual fun getMaxStereoscopicEyes(): Long = TODO("Not yet implemented")

    actual fun createSwapChain(surface: Any): SwapChain = TODO("Not yet implemented")

    actual fun createSwapChain(surface: Any, flags: Long): SwapChain = TODO("Not yet implemented")

    actual fun createSwapChain(width: Int, height: Int, flags: Long): SwapChain = TODO("Not yet implemented")

    actual fun createSwapChainFromNativeSurface(surface: NativeSurface, flags: Long): SwapChain = TODO("Not yet implemented")

    actual fun destroySwapChain(swapChain: SwapChain) {
        swapChain.invalidate()
    }

    actual fun isValidRenderer(renderer: Renderer): Boolean = true

    actual fun isValidView(view: View): Boolean = true

    actual fun isValidScene(scene: Scene): Boolean = true

    actual fun isValidFence(fence: Fence): Boolean = true

    actual fun isValidStream(stream: Stream): Boolean = true

    actual fun isValidIndexBuffer(indexBuffer: IndexBuffer): Boolean = true

    actual fun isValidVertexBuffer(vertexBuffer: VertexBuffer): Boolean = true

    actual fun isValidSkinningBuffer(skinningBuffer: SkinningBuffer): Boolean = true

    actual fun isValidMorphTargetBuffer(morphTargetBuffer: MorphTargetBuffer): Boolean = true

    actual fun isValidIndirectLight(indirectLight: IndirectLight): Boolean = true

    actual fun isValidMaterial(material: Material): Boolean = true

    actual fun isValidMaterialInstance(material: Material, materialInstance: MaterialInstance): Boolean = true

    actual fun isValidExpensiveMaterialInstance(materialInstance: MaterialInstance): Boolean = true

    actual fun isValidSkybox(skybox: Skybox): Boolean = true

    actual fun isValidColorGrading(colorGrading: ColorGrading): Boolean = true

    actual fun isValidTexture(texture: Texture): Boolean = true

    actual fun isValidRenderTarget(renderTarget: RenderTarget): Boolean = true

    actual fun isValidSwapChain(swapChain: SwapChain): Boolean = true

    actual fun createView(): View = TODO("Not yet implemented")

    actual fun destroyView(view: View) {
        view.invalidate()
    }

    actual fun createRenderer(): Renderer = TODO("Not yet implemented")

    actual fun destroyRenderer(renderer: Renderer) {
        renderer.invalidate()
    }

    actual fun createCamera(@Entity entity: Int): Camera = TODO("Not yet implemented")

    actual fun getCameraComponent(@Entity entity: Int): Camera? = TODO("Not yet implemented")

    actual fun destroyCameraComponent(@Entity entity: Int) {
    }

    actual fun createScene(): Scene = TODO("Not yet implemented")

    actual fun destroyScene(scene: Scene) {
        scene.invalidate()
    }

    actual fun destroyStream(stream: Stream) {
        stream.invalidate()
    }

    actual fun createFence(): Fence = TODO("Not yet implemented")

    actual fun destroyFence(fence: Fence) {
    }

    actual fun destroyIndexBuffer(indexBuffer: IndexBuffer) {
        indexBuffer.invalidate()
    }

    actual fun destroyVertexBuffer(vertexBuffer: VertexBuffer) {
        vertexBuffer.invalidate()
    }

    actual fun destroySkinningBuffer(skinningBuffer: SkinningBuffer) {
        skinningBuffer.invalidate()
    }

    actual fun destroyMorphTargetBuffer(morphTargetBuffer: MorphTargetBuffer) {
        morphTargetBuffer.invalidate()
    }

    actual fun destroyIndirectLight(indirectLight: IndirectLight) {
        indirectLight.invalidate()
    }

    actual fun destroyMaterial(material: Material) {
        material.invalidate()
    }

    actual fun destroyMaterialInstance(materialInstance: MaterialInstance) {
        materialInstance.invalidate()
    }

    actual fun destroySkybox(skybox: Skybox) {
        skybox.invalidate()
    }

    actual fun destroyColorGrading(colorGrading: ColorGrading) {
        colorGrading.invalidate()
    }

    actual fun destroyTexture(texture: Texture) {
        texture.invalidate()
    }

    actual fun destroyRenderTarget(target: RenderTarget) {
        target.invalidate()
    }

    actual fun destroyEntity(@Entity entity: Int) {
    }

    actual fun getTransformManager(): TransformManager = TODO("Not yet implemented")

    actual fun getLightManager(): LightManager = TODO("Not yet implemented")

    actual fun getRenderableManager(): RenderableManager = TODO("Not yet implemented")

    actual fun getEntityManager(): EntityManager = EntityManager.get()

    actual fun flushAndWait() {
        TODO("Not yet implemented")
    }

    actual fun flushAndWait(timeout: Long): Boolean = TODO("Not yet implemented")

    actual fun flush() {
        TODO("Not yet implemented")
    }

    actual fun isPaused(): Boolean = TODO("Not yet implemented")

    actual fun setPaused(paused: Boolean) {
        TODO("Not yet implemented")
    }

    actual fun unprotected() {
        TODO("Not yet implemented")
    }

    actual fun hasFeatureFlag(name: String): Boolean = TODO("Not yet implemented")

    actual fun setFeatureFlag(name: String, value: Boolean): Boolean = TODO("Not yet implemented")

    actual fun getFeatureFlag(name: String): Boolean = TODO("Not yet implemented")

    actual fun getNativeObject(): Long = nativeHandle?.rawValue?.toLong() ?: 0L

    actual fun getNativeJobSystem(): Long = TODO("Not yet implemented")

    actual enum class Backend {
        DEFAULT,
        OPENGL,
        VULKAN,
        METAL,
        WEBGPU,
        NOOP,
    }

    actual enum class FeatureLevel {
        FEATURE_LEVEL_0,
        FEATURE_LEVEL_1,
        FEATURE_LEVEL_2,
        FEATURE_LEVEL_3,
    }

    actual enum class StereoscopicType {
        NONE,
        INSTANCED,
        MULTIVIEW,
    }

    actual enum class GpuContextPriority {
        DEFAULT,
        LOW,
        MEDIUM,
        HIGH,
        REALTIME,
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

        actual enum class ShaderLanguage {
            DEFAULT,
            MSL,
            METAL_LIBRARY,
        }
    }

    actual class Builder {
        private val config = Config()

        actual fun backend(backend: Backend): Builder = this

        actual fun sharedContext(sharedContext: Any): Builder = this

        actual fun config(config: Config): Builder {
            this.config.commandBufferSizeMB = config.commandBufferSizeMB
            this.config.perRenderPassArenaSizeMB = config.perRenderPassArenaSizeMB
            this.config.driverHandleArenaSizeMB = config.driverHandleArenaSizeMB
            this.config.minCommandBufferSizeMB = config.minCommandBufferSizeMB
            this.config.perFrameCommandsSizeMB = config.perFrameCommandsSizeMB
            this.config.jobSystemThreadCount = config.jobSystemThreadCount
            this.config.textureUseAfterFreePoolSize = config.textureUseAfterFreePoolSize
            this.config.disableParallelShaderCompile = config.disableParallelShaderCompile
            this.config.stereoscopicType = config.stereoscopicType
            this.config.stereoscopicEyeCount = config.stereoscopicEyeCount
            this.config.resourceAllocatorCacheSizeMB = config.resourceAllocatorCacheSizeMB
            this.config.resourceAllocatorCacheMaxAge = config.resourceAllocatorCacheMaxAge
            this.config.disableHandleUseAfterFreeCheck = config.disableHandleUseAfterFreeCheck
            this.config.preferredShaderLanguage = config.preferredShaderLanguage
            this.config.forceGLES2Context = config.forceGLES2Context
            this.config.assertNativeWindowIsValid = config.assertNativeWindowIsValid
            this.config.gpuContextPriority = config.gpuContextPriority
            this.config.sharedUboInitialSizeInBytes = config.sharedUboInitialSizeInBytes
            return this
        }

        actual fun featureLevel(featureLevel: FeatureLevel): Builder = this

        actual fun paused(paused: Boolean): Builder = this

        actual fun feature(name: String, value: Boolean): Builder = this

        actual fun build(): Engine {
            val handle = memScoped {
                val nativeConfig = alloc<FilaEngineConfig>()
                nativeConfig.stereoscopicType = when (config.stereoscopicType) {
                    StereoscopicType.NONE -> FILA_ENGINE_STEREOSCOPIC_TYPE_NONE
                    StereoscopicType.INSTANCED -> FILA_ENGINE_STEREOSCOPIC_TYPE_INSTANCED
                    StereoscopicType.MULTIVIEW -> FILA_ENGINE_STEREOSCOPIC_TYPE_MULTIVIEW
                }.convert()
                nativeConfig.stereoscopicEyeCount = config.stereoscopicEyeCount.toUByte()
                FilaEngine_createWithConfig(nativeConfig.ptr)
            }
            requireNotNull(handle) { "Failed to create Filament Engine." }
            return Engine(handle)
        }
    }

    actual companion object {
        actual fun create(): Engine = Builder().build()

        actual fun create(backend: Backend): Engine = Builder().backend(backend).build()

        actual fun create(sharedContext: Any): Engine = Builder().sharedContext(sharedContext).build()

        actual fun getSteadyClockTimeNano(): Long = TODO("Not yet implemented")
    }
}

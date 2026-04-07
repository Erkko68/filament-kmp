@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package dev.filament.kmp

import kotlinx.cinterop.*
import dev.filament.kmp.cinterop.*
import cnames.structs.FilaEngine
import cnames.structs.FilaEngineBuilder

actual class Engine internal constructor(internal var nativeHandle: CPointer<FilaEngine>?) {
    private val mTransformManager by lazy { TransformManager(FilaEngine_getTransformManager(nativeHandle)!!) }
    private val mLightManager by lazy { LightManager(FilaEngine_getLightManager(nativeHandle)!!) }
    private val mRenderableManager by lazy { RenderableManager(FilaEngine_getRenderableManager(nativeHandle)!!) }
    private val mEntityManager by lazy { EntityManager(FilaEngine_getEntityManager(nativeHandle)!!) }

    actual enum class Backend {
        DEFAULT, OPENGL, VULKAN, METAL, WEBGPU, NOOP;
        internal fun toNative(): UInt = ordinal.toUInt()
        companion object {
            internal fun fromNative(backend: Int): Backend = values()[backend]
        }
    }

    actual enum class FeatureLevel {
        FEATURE_LEVEL_0, FEATURE_LEVEL_1, FEATURE_LEVEL_2, FEATURE_LEVEL_3;
        internal fun toNative(): UInt = ordinal.toUInt()
        companion object {
            internal fun fromNative(level: UInt): FeatureLevel = values()[level.toInt()]
        }
    }

    actual enum class StereoscopicType {
        NONE, INSTANCED, MULTIVIEW;
        internal fun toNative(): UInt = ordinal.toUInt()
        companion object {
            internal fun fromNative(type: UInt): StereoscopicType = values()[type.toInt()]
        }
    }

    actual enum class GpuContextPriority {
        DEFAULT, LOW, MEDIUM, HIGH, REALTIME;
        internal fun toNative(): Int = ordinal
        companion object {
            internal fun fromNative(priority: Int): GpuContextPriority = values()[priority]
        }
    }

    actual class Config actual constructor() {
        actual var commandBufferSizeMB: Long = 3 * 1
        actual var perRenderPassArenaSizeMB: Long = 3
        actual var driverHandleArenaSizeMB: Long = 0
        actual var minCommandBufferSizeMB: Long = 1
        actual var perFrameCommandsSizeMB: Long = 2
        actual var jobSystemThreadCount: Long = 0
        actual var textureUseAfterFreePoolSize: Long = 0
        actual var stereoscopicType: StereoscopicType = StereoscopicType.NONE
        actual var stereoscopicEyeCount: Long = 2
        actual var resourceAllocatorCacheSizeMB: Long = 64
        actual var resourceAllocatorCacheMaxAge: Long = 1

        actual enum class ShaderLanguage {
            DEFAULT, MSL, METAL_LIBRARY;
        }
        actual var preferredShaderLanguage: ShaderLanguage = ShaderLanguage.DEFAULT
        actual var forceGLES2Context: Boolean = false
        actual var gpuContextPriority: GpuContextPriority = GpuContextPriority.DEFAULT
        actual var sharedUboInitialSizeInBytes: Long = 256 * 64

        internal fun toNative(native: FilaEngineConfig) {
            native.commandBufferSizeMB = commandBufferSizeMB.toUInt()
            native.perRenderPassArenaSizeMB = perRenderPassArenaSizeMB.toUInt()
            native.driverHandleArenaSizeMB = driverHandleArenaSizeMB.toUInt()
            native.minCommandBufferSizeMB = minCommandBufferSizeMB.toUInt()
            native.perFrameCommandsSizeMB = perFrameCommandsSizeMB.toUInt()
            native.jobSystemThreadCount = jobSystemThreadCount.toUInt()
            // native.textureUseAfterFreePoolSize is not present in FilaEngineConfig
            native.stereoscopicType = stereoscopicType.toNative()
            native.stereoscopicEyeCount = stereoscopicEyeCount.toUByte()
            native.resourceAllocatorCacheSizeMB = resourceAllocatorCacheSizeMB.toUInt()
            native.resourceAllocatorCacheMaxAge = resourceAllocatorCacheMaxAge.toUByte()
            native.preferredShaderLanguage = preferredShaderLanguage.ordinal
            native.forceGLES2Context = forceGLES2Context
            native.gpuContextPriority = gpuContextPriority.toNative()
            native.sharedUboInitialSizeInBytes = sharedUboInitialSizeInBytes.toUInt()
        }
    }

    actual class Builder actual constructor() {
        private val nativeBuilder = FilaEngineBuilder_create()!!

        actual fun backend(backend: Backend): Builder {
            FilaEngineBuilder_backend(nativeBuilder, backend.toNative())
            return this
        }

        actual fun sharedContext(sharedContext: Any): Builder {
            // sharedContext is void* in C, but Any in KMP. 
            // On Native, we assume it's a Long or COpaquePointer?
            // For now, mapping Any to null if not common.
            return this
        }

        actual fun config(config: Config): Builder {
            memScoped {
                val nativeConfig = alloc<FilaEngineConfig>()
                config.toNative(nativeConfig)
                FilaEngineBuilder_config(nativeBuilder, nativeConfig.ptr)
            }
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

        actual fun build(): Engine {
            val handle = FilaEngineBuilder_build(nativeBuilder)
            FilaEngineBuilder_destroy(nativeBuilder)
            return Engine(handle ?: throw IllegalStateException("Failed to build Engine"))
        }
    }

    actual companion object {
        actual fun create(): Engine = Builder().build()
        actual fun create(backend: Backend): Engine = Builder().backend(backend).build()
        actual fun create(sharedContext: Any): Engine = Builder().sharedContext(sharedContext).build()
        actual fun getSteadyClockTimeNano(): Long = FilaEngine_getSteadyClockTimeNano().toLong()
    }

    actual fun isValid(): Boolean = nativeHandle != null
    actual fun destroy() {
        nativeHandle?.let { FilaEngine_destroy(it) }
        nativeHandle = null
    }

    actual fun getBackend(): Backend = Backend.fromNative(FilaEngine_getBackend(nativeHandle))
    actual fun getSupportedFeatureLevel(): FeatureLevel = FeatureLevel.fromNative(FilaEngine_getSupportedFeatureLevel(nativeHandle))
    actual fun setActiveFeatureLevel(featureLevel: FeatureLevel): FeatureLevel = FeatureLevel.fromNative(FilaEngine_setActiveFeatureLevel(nativeHandle, featureLevel.toNative()))
    actual fun getActiveFeatureLevel(): FeatureLevel = FeatureLevel.fromNative(FilaEngine_getActiveFeatureLevel(nativeHandle))
    actual fun setAutomaticInstancingEnabled(enable: Boolean) = FilaEngine_setAutomaticInstancingEnabled(nativeHandle, enable)
    actual fun isAutomaticInstancingEnabled(): Boolean = FilaEngine_isAutomaticInstancingEnabled(nativeHandle)
    actual fun getConfig(): Config {
        // C-wrapper doesn't expose getConfig, we'd need to track it or return default
        return Config()
    }
    actual fun getMaxStereoscopicEyes(): Long = FilaEngine_getMaxStereoscopicEyes(nativeHandle).toLong()

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

    actual fun createSwapChain(surface: Any): SwapChain = SwapChain(FilaEngine_createSwapChain(nativeHandle, null /* TODO */, 0UL))
    actual fun createSwapChain(surface: Any, flags: Long): SwapChain = SwapChain(FilaEngine_createSwapChain(nativeHandle, null /* TODO */, flags.toULong()))
    actual fun createSwapChain(width: Int, height: Int, flags: Long): SwapChain = SwapChain(FilaEngine_createSwapChainHeadless(nativeHandle, width.toUInt(), height.toUInt(), flags.toULong()))
    actual fun destroySwapChain(swapChain: SwapChain) {
        FilaEngine_destroySwapChain(nativeHandle, swapChain.nativeHandle)
        swapChain.nativeHandle = null
    }

    actual fun createView(): View = View(FilaEngine_createView(nativeHandle))
    actual fun destroyView(view: View) {
        FilaEngine_destroyView(nativeHandle, view.nativeHandle)
        view.nativeHandle = null
    }

    actual fun createRenderer(): Renderer = Renderer(FilaEngine_createRenderer(nativeHandle)).setEngine(this)
    actual fun destroyRenderer(renderer: Renderer) {
        FilaEngine_destroyRenderer(nativeHandle, renderer.nativeHandle)
        renderer.nativeHandle = null
    }

    actual fun createCamera(): Camera {
        val handle = FilaEngine_createCameraAuto(nativeHandle)
        val entity = FilaCamera_getEntity(handle).toInt()
        return Camera(handle, entity)
    }
    actual fun createCamera(entity: Int): Camera = Camera(FilaEngine_createCamera(nativeHandle, entity.toUInt()), entity)
    actual fun getCameraComponent(entity: Int): Camera? {
        val handle = FilaEngine_getCameraComponent(nativeHandle, entity.toUInt())
        return if (handle != null) Camera(handle, entity) else null
    }
    actual fun destroyCamera(camera: Camera) {
        FilaEngine_destroyCamera(nativeHandle, camera.nativeHandle)
        camera.nativeHandle = null
    }
    actual fun destroyCameraComponent(entity: Int) = FilaEngine_destroyCameraComponent(nativeHandle, entity.toUInt())

    actual fun createScene(): Scene = Scene(FilaEngine_createScene(nativeHandle))
    actual fun destroyScene(scene: Scene) {
        FilaEngine_destroyScene(nativeHandle, scene.nativeHandle)
        scene.nativeHandle = null
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
    actual fun destroyIndirectLight(ibl: IndirectLight) {
        FilaEngine_destroyIndirectLight(nativeHandle, ibl.nativeHandle)
        ibl.nativeHandle = null
    }
    actual fun destroyMaterial(material: Material) {
        FilaEngine_destroyMaterial(nativeHandle, material.nativeHandle)
    }
    actual fun destroyMaterialInstance(materialInstance: MaterialInstance) {
        FilaEngine_destroyMaterialInstance(nativeHandle, materialInstance.nativeHandle)
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
    }
    actual fun destroyRenderTarget(target: RenderTarget) {
        FilaEngine_destroyRenderTarget(nativeHandle, target.nativeHandle)
    }
    actual fun destroyStream(stream: Stream) {
        FilaEngine_destroyStream(nativeHandle, stream.nativeHandle)
        stream.nativeHandle = null
    }
    actual fun destroyEntity(entity: Int) = FilaEntityManager_destroy(FilaEngine_getEntityManager(nativeHandle), entity.toUInt())

    actual fun getTransformManager(): TransformManager = mTransformManager
    actual fun getLightManager(): LightManager = mLightManager
    actual fun getRenderableManager(): RenderableManager = mRenderableManager
    actual fun getEntityManager(): EntityManager = mEntityManager

    actual fun flushAndWait() { FilaEngine_flushAndWait(nativeHandle, 1_000_000_000u) }
    actual fun flushAndWait(timeout: Long): Boolean = FilaEngine_flushAndWait(nativeHandle, timeout.toULong())
    actual fun flush() = FilaEngine_flush(nativeHandle)
    actual fun isPaused(): Boolean = FilaEngine_isPaused(nativeHandle)
    actual fun setPaused(paused: Boolean) = FilaEngine_setPaused(nativeHandle, paused)
    actual fun unprotected() = FilaEngine_unprotected(nativeHandle)
    actual fun hasFeatureFlag(name: String): Boolean = FilaEngine_hasFeatureFlag(nativeHandle, name)
    actual fun setFeatureFlag(name: String, value: Boolean): Boolean {
        FilaEngine_setFeatureFlag(nativeHandle, name, value)
        return true
    }
    actual fun getFeatureFlag(name: String): Boolean = FilaEngine_getFeatureFlag(nativeHandle, name)
}

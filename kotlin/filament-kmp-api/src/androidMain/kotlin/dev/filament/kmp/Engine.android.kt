package dev.filament.kmp

import com.google.android.filament.Engine as AndroidEngine

actual class Engine internal constructor(val nativeEngine: AndroidEngine) {
    actual enum class Backend {
        DEFAULT, OPENGL, VULKAN, METAL, WEBGPU, NOOP;
        internal fun toAndroid() = AndroidEngine.Backend.values()[ordinal]
        companion object {
            internal fun fromAndroid(backend: AndroidEngine.Backend) = values()[backend.ordinal]
        }
    }

    actual enum class FeatureLevel {
        FEATURE_LEVEL_0, FEATURE_LEVEL_1, FEATURE_LEVEL_2, FEATURE_LEVEL_3;
        internal fun toAndroid() = AndroidEngine.FeatureLevel.values()[ordinal]
        companion object {
            internal fun fromAndroid(level: AndroidEngine.FeatureLevel) = values()[level.ordinal]
        }
    }

    actual enum class StereoscopicType {
        NONE, INSTANCED, MULTIVIEW;
        internal fun toAndroid() = AndroidEngine.StereoscopicType.values()[ordinal]
        companion object {
            internal fun fromAndroid(type: AndroidEngine.StereoscopicType) = values()[type.ordinal]
        }
    }

    actual enum class GpuContextPriority {
        DEFAULT, LOW, MEDIUM, HIGH, REALTIME;
        internal fun toAndroid() = AndroidEngine.GpuContextPriority.values()[ordinal]
        companion object {
            internal fun fromAndroid(priority: AndroidEngine.GpuContextPriority) = values()[priority.ordinal]
        }
    }

    actual class Config actual constructor() {
        private val android = AndroidEngine.Config()

        actual var commandBufferSizeMB: Long
            get() = android.commandBufferSizeMB
            set(value) { android.commandBufferSizeMB = value }
        actual var perRenderPassArenaSizeMB: Long
            get() = android.perRenderPassArenaSizeMB
            set(value) { android.perRenderPassArenaSizeMB = value }
        actual var driverHandleArenaSizeMB: Long
            get() = android.driverHandleArenaSizeMB
            set(value) { android.driverHandleArenaSizeMB = value }
        actual var minCommandBufferSizeMB: Long
            get() = android.minCommandBufferSizeMB
            set(value) { android.minCommandBufferSizeMB = value }
        actual var perFrameCommandsSizeMB: Long
            get() = android.perFrameCommandsSizeMB
            set(value) { android.perFrameCommandsSizeMB = value }
        actual var jobSystemThreadCount: Long
            get() = android.jobSystemThreadCount
            set(value) { android.jobSystemThreadCount = value }
        actual var textureUseAfterFreePoolSize: Long
            get() = android.textureUseAfterFreePoolSize
            set(value) { android.textureUseAfterFreePoolSize = value }
        actual var disableParallelShaderCompile: Boolean
            get() = android.disableParallelShaderCompile
            set(value) { android.disableParallelShaderCompile = value }
        actual var stereoscopicType: StereoscopicType
            get() = StereoscopicType.fromAndroid(android.stereoscopicType)
            set(value) { android.stereoscopicType = value.toAndroid() }
        actual var stereoscopicEyeCount: Long
            get() = android.stereoscopicEyeCount
            set(value) { android.stereoscopicEyeCount = value }
        actual var resourceAllocatorCacheSizeMB: Long
            get() = android.resourceAllocatorCacheSizeMB
            set(value) { android.resourceAllocatorCacheSizeMB = value }
        actual var resourceAllocatorCacheMaxAge: Long
            get() = android.resourceAllocatorCacheMaxAge
            set(value) { android.resourceAllocatorCacheMaxAge = value }
        actual var disableHandleUseAfterFreeCheck: Boolean
            get() = android.disableHandleUseAfterFreeCheck
            set(value) { android.disableHandleUseAfterFreeCheck = value }

        actual enum class ShaderLanguage {
            DEFAULT, MSL, METAL_LIBRARY;
            internal fun toAndroid() = AndroidEngine.Config.ShaderLanguage.values()[ordinal]
            companion object {
                internal fun fromAndroid(lang: AndroidEngine.Config.ShaderLanguage) = values()[lang.ordinal]
            }
        }
        actual var preferredShaderLanguage: ShaderLanguage
            get() = ShaderLanguage.fromAndroid(android.preferredShaderLanguage)
            set(value) { android.preferredShaderLanguage = value.toAndroid() }
        actual var forceGLES2Context: Boolean
            get() = android.forceGLES2Context
            set(value) { android.forceGLES2Context = value }
        actual var assertNativeWindowIsValid: Boolean
            get() = android.assertNativeWindowIsValid
            set(value) { android.assertNativeWindowIsValid = value }
        actual var gpuContextPriority: GpuContextPriority
            get() = GpuContextPriority.fromAndroid(android.gpuContextPriority)
            set(value) { android.gpuContextPriority = value.toAndroid() }
        actual var sharedUboInitialSizeInBytes: Long
            get() = android.sharedUboInitialSizeInBytes
            set(value) { android.sharedUboInitialSizeInBytes = value }

        internal fun toAndroid() = android
    }

    actual class Builder actual constructor() {
        private val android = AndroidEngine.Builder()

        actual fun backend(backend: Backend): Builder {
            android.backend(backend.toAndroid())
            return this
        }

        actual fun sharedContext(sharedContext: Any): Builder {
            android.sharedContext(sharedContext)
            return this
        }

        actual fun config(config: Config): Builder {
            android.config(config.toAndroid())
            return this
        }

        actual fun featureLevel(featureLevel: FeatureLevel): Builder {
            android.featureLevel(featureLevel.toAndroid())
            return this
        }

        actual fun paused(paused: Boolean): Builder {
            android.paused(paused)
            return this
        }

        actual fun feature(name: String, value: Boolean): Builder {
            android.feature(name, value)
            return this
        }

        actual fun build(): Engine = Engine(android.build())
    }

    actual companion object {
        actual fun create(): Engine = Engine(AndroidEngine.create())
        actual fun create(backend: Backend): Engine = Engine(AndroidEngine.create(backend.toAndroid()))
        actual fun create(sharedContext: Any): Engine = Engine(AndroidEngine.create(sharedContext))
        actual fun getSteadyClockTimeNano(): Long = AndroidEngine.getSteadyClockTimeNano()
    }

    actual fun isValid(): Boolean = nativeEngine.isValid
    actual fun destroy() = nativeEngine.destroy()
    actual fun getBackend(): Backend = Backend.fromAndroid(nativeEngine.backend)
    actual fun getSupportedFeatureLevel(): FeatureLevel = FeatureLevel.fromAndroid(nativeEngine.supportedFeatureLevel)
    actual fun setActiveFeatureLevel(featureLevel: FeatureLevel): FeatureLevel = FeatureLevel.fromAndroid(nativeEngine.setActiveFeatureLevel(featureLevel.toAndroid()))
    actual fun getActiveFeatureLevel(): FeatureLevel = FeatureLevel.fromAndroid(nativeEngine.activeFeatureLevel)
    actual fun setAutomaticInstancingEnabled(enable: Boolean) = nativeEngine.setAutomaticInstancingEnabled(enable)
    actual fun isAutomaticInstancingEnabled(): Boolean = nativeEngine.isAutomaticInstancingEnabled
    actual fun getConfig(): Config {
        val config = Config()
        val androidConfig = nativeEngine.config
        config.commandBufferSizeMB = androidConfig.commandBufferSizeMB
        config.perRenderPassArenaSizeMB = androidConfig.perRenderPassArenaSizeMB
        config.driverHandleArenaSizeMB = androidConfig.driverHandleArenaSizeMB
        config.minCommandBufferSizeMB = androidConfig.minCommandBufferSizeMB
        config.perFrameCommandsSizeMB = androidConfig.perFrameCommandsSizeMB
        config.jobSystemThreadCount = androidConfig.jobSystemThreadCount
        config.textureUseAfterFreePoolSize = androidConfig.textureUseAfterFreePoolSize
        config.disableParallelShaderCompile = androidConfig.disableParallelShaderCompile
        config.stereoscopicType = StereoscopicType.fromAndroid(androidConfig.stereoscopicType)
        config.stereoscopicEyeCount = androidConfig.stereoscopicEyeCount
        config.resourceAllocatorCacheSizeMB = androidConfig.resourceAllocatorCacheSizeMB
        config.resourceAllocatorCacheMaxAge = androidConfig.resourceAllocatorCacheMaxAge
        config.disableHandleUseAfterFreeCheck = androidConfig.disableHandleUseAfterFreeCheck
        config.preferredShaderLanguage = Config.ShaderLanguage.fromAndroid(androidConfig.preferredShaderLanguage)
        config.forceGLES2Context = androidConfig.forceGLES2Context
        config.assertNativeWindowIsValid = androidConfig.assertNativeWindowIsValid
        config.gpuContextPriority = GpuContextPriority.fromAndroid(androidConfig.gpuContextPriority)
        config.sharedUboInitialSizeInBytes = androidConfig.sharedUboInitialSizeInBytes
        return config
    }
    actual fun getMaxStereoscopicEyes(): Long = nativeEngine.maxStereoscopicEyes

    actual fun createSwapChain(surface: Any): SwapChain = SwapChain(nativeEngine.createSwapChain(surface))
    actual fun createSwapChain(surface: Any, flags: Long): SwapChain = SwapChain(nativeEngine.createSwapChain(surface, flags))
    actual fun createSwapChain(width: Int, height: Int, flags: Long): SwapChain = SwapChain(nativeEngine.createSwapChain(width, height, flags))
    actual fun destroySwapChain(swapChain: SwapChain) { nativeEngine.destroySwapChain(swapChain.nativeSwapChain) }

    actual fun createView(): View = View(nativeEngine.createView())
    actual fun destroyView(view: View) { nativeEngine.destroyView(view.nativeView) }

    actual fun createRenderer(): Renderer = Renderer(this, nativeEngine.createRenderer())
    actual fun destroyRenderer(renderer: Renderer) { nativeEngine.destroyRenderer(renderer.nativeRenderer) }

    actual fun createCamera(): Camera {
        val entity = EntityManager.get().create()
        return Camera(nativeEngine.createCamera(entity))
    }
    actual fun createCamera(entity: Int): Camera = Camera(nativeEngine.createCamera(entity))
    actual fun getCameraComponent(entity: Int): Camera? = nativeEngine.getCameraComponent(entity)?.let { Camera(it) }
    actual fun destroyCamera(camera: Camera) { nativeEngine.destroyCameraComponent(camera.nativeCamera.entity) }
    actual fun destroyCameraComponent(entity: Int) { nativeEngine.destroyCameraComponent(entity) }

    actual fun createScene(): Scene = Scene(nativeEngine.createScene())
    actual fun destroyScene(scene: Scene) { nativeEngine.destroyScene(scene.nativeScene) }

    actual fun createFence(): Fence = Fence(nativeEngine.createFence())
    actual fun destroyFence(fence: Fence) { nativeEngine.destroyFence(fence.nativeFence) }

    actual fun destroyIndexBuffer(indexBuffer: IndexBuffer) { nativeEngine.destroyIndexBuffer(indexBuffer.nativeIndexBuffer) }
    actual fun destroyVertexBuffer(vertexBuffer: VertexBuffer) { nativeEngine.destroyVertexBuffer(vertexBuffer.nativeVertexBuffer) }
    actual fun destroyIndirectLight(ibl: IndirectLight) { nativeEngine.destroyIndirectLight(ibl.nativeIndirectLight) }
    actual fun destroyMaterial(material: Material) { nativeEngine.destroyMaterial(material.nativeMaterial) }
    actual fun destroyMaterialInstance(materialInstance: MaterialInstance) { nativeEngine.destroyMaterialInstance(materialInstance.nativeMaterialInstance) }
    actual fun destroySkybox(skybox: Skybox) { nativeEngine.destroySkybox(skybox.nativeSkybox) }
    actual fun destroyColorGrading(colorGrading: ColorGrading) { nativeEngine.destroyColorGrading(colorGrading.nativeColorGrading) }
    actual fun destroyTexture(texture: Texture) { nativeEngine.destroyTexture(texture.nativeTexture) }
    actual fun destroyRenderTarget(target: RenderTarget) { nativeEngine.destroyRenderTarget(target.nativeRenderTarget) }
    actual fun destroyEntity(entity: Int) { nativeEngine.destroyEntity(entity) }

    actual fun getTransformManager(): TransformManager = TransformManager(nativeEngine.transformManager)
    actual fun getLightManager(): LightManager = LightManager(nativeEngine.lightManager)
    actual fun getRenderableManager(): RenderableManager = RenderableManager(nativeEngine.renderableManager)
    actual fun getEntityManager(): EntityManager = EntityManager(nativeEngine.entityManager)

    actual fun flushAndWait() = nativeEngine.flushAndWait()
    actual fun flushAndWait(timeout: Long): Boolean = nativeEngine.flushAndWait(timeout)
    actual fun flush() = nativeEngine.flush()
    actual fun isPaused(): Boolean = nativeEngine.isPaused
    actual fun setPaused(paused: Boolean) { nativeEngine.isPaused = paused }
    actual fun unprotected() = nativeEngine.unprotected()
    actual fun hasFeatureFlag(name: String): Boolean = nativeEngine.hasFeatureFlag(name)
    actual fun setFeatureFlag(name: String, value: Boolean): Boolean = nativeEngine.setFeatureFlag(name, value)
    actual fun getFeatureFlag(name: String): Boolean = nativeEngine.getFeatureFlag(name)
}

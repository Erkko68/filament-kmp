package dev.filament.kmp

import com.google.android.filament.Engine as AndroidEngine

actual class Engine private constructor(
    internal var androidEngine: AndroidEngine?,
) {
    private fun engine(): AndroidEngine = requireNotNull(androidEngine) { "Engine is destroyed." }

    actual fun isValid(): Boolean = androidEngine != null

    actual fun destroy() {
        engine().destroy()
        androidEngine = null
    }

    actual fun getBackend(): Backend = engine().backend.toKmp()

    actual fun enableAccurateTranslations() {
        engine().enableAccurateTranslations()
    }

    actual fun getSupportedFeatureLevel(): FeatureLevel = engine().supportedFeatureLevel.toKmp()

    actual fun setActiveFeatureLevel(featureLevel: FeatureLevel): FeatureLevel {
        return engine().setActiveFeatureLevel(featureLevel.toAndroid()).toKmp()
    }

    actual fun getActiveFeatureLevel(): FeatureLevel = engine().activeFeatureLevel.toKmp()

    actual fun setAutomaticInstancingEnabled(enable: Boolean) {
        engine().setAutomaticInstancingEnabled(enable)
    }

    actual fun isAutomaticInstancingEnabled(): Boolean = engine().isAutomaticInstancingEnabled

    actual fun getConfig(): Config = Config(engine().config)

    actual fun getMaxStereoscopicEyes(): Long = engine().maxStereoscopicEyes

    actual fun createSwapChain(surface: Any): SwapChain = SwapChain(engine().createSwapChain(surface))

    actual fun createSwapChain(surface: Any, flags: Long): SwapChain = SwapChain(engine().createSwapChain(surface, flags))

    actual fun createSwapChain(width: Int, height: Int, flags: Long): SwapChain {
        return SwapChain(engine().createSwapChain(width, height, flags))
    }

    actual fun createSwapChainFromNativeSurface(surface: NativeSurface, flags: Long): SwapChain {
        val androidSurface = requireNotNull(surface.androidNativeSurface) { "NativeSurface is destroyed." }
        return SwapChain(engine().createSwapChainFromNativeSurface(androidSurface, flags))
    }

    actual fun destroySwapChain(swapChain: SwapChain) {
        val handle = swapChain.androidSwapChain ?: return
        engine().destroySwapChain(handle)
        swapChain.invalidate()
    }

    actual fun isValidRenderer(renderer: Renderer): Boolean {
        val handle = renderer.androidRenderer ?: return false
        return engine().isValidRenderer(handle)
    }

    actual fun isValidView(view: View): Boolean {
        val handle = view.androidView ?: return false
        return engine().isValidView(handle)
    }

    actual fun isValidScene(scene: Scene): Boolean {
        val handle = scene.androidScene ?: return false
        return engine().isValidScene(handle)
    }

    actual fun isValidFence(fence: Fence): Boolean {
        val handle = fence.androidFence ?: return false
        return engine().isValidFence(handle)
    }

    actual fun isValidStream(stream: Stream): Boolean {
        val handle = stream.androidStream ?: return false
        return engine().isValidStream(handle)
    }

    actual fun isValidIndexBuffer(indexBuffer: IndexBuffer): Boolean {
        val handle = indexBuffer.androidIndexBuffer ?: return false
        return engine().isValidIndexBuffer(handle)
    }

    actual fun isValidVertexBuffer(vertexBuffer: VertexBuffer): Boolean {
        val handle = vertexBuffer.androidVertexBuffer ?: return false
        return engine().isValidVertexBuffer(handle)
    }

    actual fun isValidSkinningBuffer(skinningBuffer: SkinningBuffer): Boolean {
        val handle = skinningBuffer.androidSkinningBuffer ?: return false
        return engine().isValidSkinningBuffer(handle)
    }

    actual fun isValidMorphTargetBuffer(morphTargetBuffer: MorphTargetBuffer): Boolean {
        val handle = morphTargetBuffer.androidMorphTargetBuffer ?: return false
        return engine().isValidMorphTargetBuffer(handle)
    }

    actual fun isValidIndirectLight(indirectLight: IndirectLight): Boolean {
        val handle = indirectLight.androidIndirectLight ?: return false
        return engine().isValidIndirectLight(handle)
    }

    actual fun isValidMaterial(material: Material): Boolean {
        val handle = material.androidMaterial ?: return false
        return engine().isValidMaterial(handle)
    }

    actual fun isValidMaterialInstance(material: Material, materialInstance: MaterialInstance): Boolean {
        val androidMaterial = material.androidMaterial ?: return false
        val androidInstance = materialInstance.androidMaterialInstance ?: return false
        return engine().isValidMaterialInstance(androidMaterial, androidInstance)
    }

    actual fun isValidExpensiveMaterialInstance(materialInstance: MaterialInstance): Boolean {
        val handle = materialInstance.androidMaterialInstance ?: return false
        return engine().isValidExpensiveMaterialInstance(handle)
    }

    actual fun isValidSkybox(skybox: Skybox): Boolean {
        val handle = skybox.androidSkybox ?: return false
        return engine().isValidSkybox(handle)
    }

    actual fun isValidColorGrading(colorGrading: ColorGrading): Boolean {
        val handle = colorGrading.androidColorGrading ?: return false
        return engine().isValidColorGrading(handle)
    }

    actual fun isValidTexture(texture: Texture): Boolean {
        val handle = texture.androidTexture ?: return false
        return engine().isValidTexture(handle)
    }

    actual fun isValidRenderTarget(renderTarget: RenderTarget): Boolean {
        val handle = renderTarget.androidRenderTarget ?: return false
        return engine().isValidRenderTarget(handle)
    }

    actual fun isValidSwapChain(swapChain: SwapChain): Boolean {
        val handle = swapChain.androidSwapChain ?: return false
        return engine().isValidSwapChain(handle)
    }

    actual fun createView(): View = View(engine().createView())

    actual fun destroyView(view: View) {
        val handle = view.androidView ?: return
        engine().destroyView(handle)
        view.invalidate()
    }

    actual fun createRenderer(): Renderer = Renderer(engine().createRenderer(), this)

    actual fun destroyRenderer(renderer: Renderer) {
        val handle = renderer.androidRenderer ?: return
        engine().destroyRenderer(handle)
        renderer.invalidate()
    }

    actual fun createCamera(@Entity entity: Int): Camera = Camera(engine().createCamera(entity))

    actual fun getCameraComponent(@Entity entity: Int): Camera? {
        val camera = engine().getCameraComponent(entity) ?: return null
        return Camera(camera)
    }

    actual fun destroyCameraComponent(@Entity entity: Int) {
        engine().destroyCameraComponent(entity)
    }

    actual fun createScene(): Scene = Scene(engine().createScene())

    actual fun destroyScene(scene: Scene) {
        val handle = scene.androidScene ?: return
        engine().destroyScene(handle)
        scene.invalidate()
    }

    actual fun destroyStream(stream: Stream) {
        val handle = stream.androidStream ?: return
        engine().destroyStream(handle)
        stream.invalidate()
    }

    actual fun createFence(): Fence = Fence(engine().createFence())

    actual fun destroyFence(fence: Fence) {
        val handle = fence.androidFence ?: return
        engine().destroyFence(handle)
        fence.clearNativeObject()
    }

    actual fun destroyIndexBuffer(indexBuffer: IndexBuffer) {
        val handle = indexBuffer.androidIndexBuffer ?: return
        engine().destroyIndexBuffer(handle)
        indexBuffer.invalidate()
    }

    actual fun destroyVertexBuffer(vertexBuffer: VertexBuffer) {
        val handle = vertexBuffer.androidVertexBuffer ?: return
        engine().destroyVertexBuffer(handle)
        vertexBuffer.invalidate()
    }

    actual fun destroySkinningBuffer(skinningBuffer: SkinningBuffer) {
        val handle = skinningBuffer.androidSkinningBuffer ?: return
        engine().destroySkinningBuffer(handle)
        skinningBuffer.invalidate()
    }

    actual fun destroyMorphTargetBuffer(morphTargetBuffer: MorphTargetBuffer) {
        val handle = morphTargetBuffer.androidMorphTargetBuffer ?: return
        engine().destroyMorphTargetBuffer(handle)
        morphTargetBuffer.invalidate()
    }

    actual fun destroyIndirectLight(indirectLight: IndirectLight) {
        val handle = indirectLight.androidIndirectLight ?: return
        engine().destroyIndirectLight(handle)
        indirectLight.invalidate()
    }

    actual fun destroyMaterial(material: Material) {
        val handle = material.androidMaterial ?: return
        engine().destroyMaterial(handle)
        material.invalidate()
    }

    actual fun destroyMaterialInstance(materialInstance: MaterialInstance) {
        val handle = materialInstance.androidMaterialInstance ?: return
        engine().destroyMaterialInstance(handle)
        materialInstance.invalidate()
    }

    actual fun destroySkybox(skybox: Skybox) {
        val handle = skybox.androidSkybox ?: return
        engine().destroySkybox(handle)
        skybox.invalidate()
    }

    actual fun destroyColorGrading(colorGrading: ColorGrading) {
        val handle = colorGrading.androidColorGrading ?: return
        engine().destroyColorGrading(handle)
        colorGrading.invalidate()
    }

    actual fun destroyTexture(texture: Texture) {
        val handle = texture.androidTexture ?: return
        engine().destroyTexture(handle)
        texture.invalidate()
    }

    actual fun destroyRenderTarget(target: RenderTarget) {
        val handle = target.androidRenderTarget ?: return
        engine().destroyRenderTarget(handle)
        target.invalidate()
    }

    actual fun destroyEntity(@Entity entity: Int) {
        engine().destroyEntity(entity)
    }

    actual fun getTransformManager(): TransformManager = TransformManager(engine().transformManager)

    actual fun getLightManager(): LightManager = LightManager(engine().lightManager)

    actual fun getRenderableManager(): RenderableManager = RenderableManager(engine().renderableManager)

    actual fun getEntityManager(): EntityManager = EntityManager(engine().entityManager)

    actual fun flushAndWait() {
        engine().flushAndWait()
    }

    actual fun flushAndWait(timeout: Long): Boolean = engine().flushAndWait(timeout)

    actual fun flush() {
        engine().flush()
    }

    actual fun isPaused(): Boolean = engine().isPaused

    actual fun setPaused(paused: Boolean) {
        engine().setPaused(paused)
    }

    actual fun unprotected() {
        engine().unprotected()
    }

    actual fun hasFeatureFlag(name: String): Boolean = engine().hasFeatureFlag(name)

    actual fun setFeatureFlag(name: String, value: Boolean): Boolean = engine().setFeatureFlag(name, value)

    actual fun getFeatureFlag(name: String): Boolean = engine().getFeatureFlag(name)

    actual fun getNativeObject(): Long = engine().nativeObject

    actual fun getNativeJobSystem(): Long = engine().nativeJobSystem

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

    actual class Config internal constructor(
        internal val androidConfig: AndroidEngine.Config,
    ) {
        constructor() : this(AndroidEngine.Config())

        actual var commandBufferSizeMB: Long
            get() = androidConfig.commandBufferSizeMB
            set(value) {
                androidConfig.commandBufferSizeMB = value
            }

        actual var perRenderPassArenaSizeMB: Long
            get() = androidConfig.perRenderPassArenaSizeMB
            set(value) {
                androidConfig.perRenderPassArenaSizeMB = value
            }

        actual var driverHandleArenaSizeMB: Long
            get() = androidConfig.driverHandleArenaSizeMB
            set(value) {
                androidConfig.driverHandleArenaSizeMB = value
            }

        actual var minCommandBufferSizeMB: Long
            get() = androidConfig.minCommandBufferSizeMB
            set(value) {
                androidConfig.minCommandBufferSizeMB = value
            }

        actual var perFrameCommandsSizeMB: Long
            get() = androidConfig.perFrameCommandsSizeMB
            set(value) {
                androidConfig.perFrameCommandsSizeMB = value
            }

        actual var jobSystemThreadCount: Long
            get() = androidConfig.jobSystemThreadCount
            set(value) {
                androidConfig.jobSystemThreadCount = value
            }

        actual var textureUseAfterFreePoolSize: Long
            get() = androidConfig.textureUseAfterFreePoolSize
            set(value) {
                androidConfig.textureUseAfterFreePoolSize = value
            }

        @Deprecated("Use feature flag \"backend.disable_parallel_shader_compile\" instead.")
        actual var disableParallelShaderCompile: Boolean
            get() = androidConfig.disableParallelShaderCompile
            set(value) {
                androidConfig.disableParallelShaderCompile = value
            }

        actual var stereoscopicType: StereoscopicType
            get() = androidConfig.stereoscopicType.toKmp()
            set(value) {
                androidConfig.stereoscopicType = value.toAndroid()
            }

        actual var stereoscopicEyeCount: Long
            get() = androidConfig.stereoscopicEyeCount
            set(value) {
                androidConfig.stereoscopicEyeCount = value
            }

        @Deprecated("This value is no longer used.")
        actual var resourceAllocatorCacheSizeMB: Long
            get() = androidConfig.resourceAllocatorCacheSizeMB
            set(value) {
                androidConfig.resourceAllocatorCacheSizeMB = value
            }

        actual var resourceAllocatorCacheMaxAge: Long
            get() = androidConfig.resourceAllocatorCacheMaxAge
            set(value) {
                androidConfig.resourceAllocatorCacheMaxAge = value
            }

        @Deprecated("Use feature flag \"backend.disable_handle_use_after_free_check\" instead.")
        actual var disableHandleUseAfterFreeCheck: Boolean
            get() = androidConfig.disableHandleUseAfterFreeCheck
            set(value) {
                androidConfig.disableHandleUseAfterFreeCheck = value
            }

        actual var preferredShaderLanguage: ShaderLanguage
            get() = androidConfig.preferredShaderLanguage.toKmp()
            set(value) {
                androidConfig.preferredShaderLanguage = value.toAndroid()
            }

        actual var forceGLES2Context: Boolean
            get() = androidConfig.forceGLES2Context
            set(value) {
                androidConfig.forceGLES2Context = value
            }

        @Deprecated("Use feature flag \"backend.opengl.assert_native_window_is_valid\" instead.")
        actual var assertNativeWindowIsValid: Boolean
            get() = androidConfig.assertNativeWindowIsValid
            set(value) {
                androidConfig.assertNativeWindowIsValid = value
            }

        actual var gpuContextPriority: GpuContextPriority
            get() = androidConfig.gpuContextPriority.toKmp()
            set(value) {
                androidConfig.gpuContextPriority = value.toAndroid()
            }

        actual var sharedUboInitialSizeInBytes: Long
            get() = androidConfig.sharedUboInitialSizeInBytes
            set(value) {
                androidConfig.sharedUboInitialSizeInBytes = value
            }

        actual enum class ShaderLanguage {
            DEFAULT,
            MSL,
            METAL_LIBRARY,
        }
    }

    actual class Builder {
        private val androidBuilder = AndroidEngine.Builder()

        actual fun backend(backend: Backend): Builder {
            androidBuilder.backend(backend.toAndroid())
            return this
        }

        actual fun sharedContext(sharedContext: Any): Builder {
            androidBuilder.sharedContext(sharedContext)
            return this
        }

        actual fun config(config: Config): Builder {
            androidBuilder.config(config.androidConfig)
            return this
        }

        actual fun featureLevel(featureLevel: FeatureLevel): Builder {
            androidBuilder.featureLevel(featureLevel.toAndroid())
            return this
        }

        actual fun paused(paused: Boolean): Builder {
            androidBuilder.paused(paused)
            return this
        }

        actual fun feature(name: String, value: Boolean): Builder {
            androidBuilder.feature(name, value)
            return this
        }

        actual fun build(): Engine = Engine(androidBuilder.build())
    }

    actual companion object {
        actual fun create(): Engine = Engine(AndroidEngine.create())

        actual fun create(backend: Backend): Engine = Engine(AndroidEngine.create(backend.toAndroid()))

        actual fun create(sharedContext: Any): Engine = Engine(AndroidEngine.create(sharedContext))

        actual fun getSteadyClockTimeNano(): Long = AndroidEngine.getSteadyClockTimeNano()
    }
}

private fun Engine.Backend.toAndroid(): AndroidEngine.Backend = AndroidEngine.Backend.valueOf(name)

private fun AndroidEngine.Backend.toKmp(): Engine.Backend = Engine.Backend.valueOf(name)

private fun Engine.FeatureLevel.toAndroid(): AndroidEngine.FeatureLevel = AndroidEngine.FeatureLevel.valueOf(name)

private fun AndroidEngine.FeatureLevel.toKmp(): Engine.FeatureLevel = Engine.FeatureLevel.valueOf(name)

private fun Engine.StereoscopicType.toAndroid(): AndroidEngine.StereoscopicType = AndroidEngine.StereoscopicType.valueOf(name)

private fun AndroidEngine.StereoscopicType.toKmp(): Engine.StereoscopicType = Engine.StereoscopicType.valueOf(name)

private fun Engine.GpuContextPriority.toAndroid(): AndroidEngine.GpuContextPriority = AndroidEngine.GpuContextPriority.valueOf(name)

private fun AndroidEngine.GpuContextPriority.toKmp(): Engine.GpuContextPriority = Engine.GpuContextPriority.valueOf(name)

private fun Engine.Config.ShaderLanguage.toAndroid(): AndroidEngine.Config.ShaderLanguage =
    AndroidEngine.Config.ShaderLanguage.valueOf(name)

private fun AndroidEngine.Config.ShaderLanguage.toKmp(): Engine.Config.ShaderLanguage =
    Engine.Config.ShaderLanguage.valueOf(name)


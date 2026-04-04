package dev.filament.kmp

actual class Engine {
    actual fun isValid(): Boolean = TODO("Not yet implemented")

    actual fun destroy() {
    }

    actual fun getBackend(): Backend = TODO("Not yet implemented")

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

    actual fun getConfig(): Config = TODO("Not yet implemented")

    actual fun getMaxStereoscopicEyes(): Long = TODO("Not yet implemented")

    actual fun createSwapChain(surface: Any): SwapChain = TODO("Not yet implemented")

    actual fun createSwapChain(surface: Any, flags: Long): SwapChain = TODO("Not yet implemented")

    actual fun createSwapChain(width: Int, height: Int, flags: Long): SwapChain = TODO("Not yet implemented")

    actual fun createSwapChainFromNativeSurface(surface: NativeSurface, flags: Long): SwapChain = TODO("Not yet implemented")

    actual fun destroySwapChain(swapChain: SwapChain) {
    }

    actual fun isValidRenderer(renderer: Renderer): Boolean = TODO("Not yet implemented")

    actual fun isValidView(view: View): Boolean = TODO("Not yet implemented")

    actual fun isValidScene(scene: Scene): Boolean = TODO("Not yet implemented")

    actual fun isValidFence(fence: Fence): Boolean = TODO("Not yet implemented")

    actual fun isValidStream(stream: Stream): Boolean = TODO("Not yet implemented")

    actual fun isValidIndexBuffer(indexBuffer: IndexBuffer): Boolean = TODO("Not yet implemented")

    actual fun isValidVertexBuffer(vertexBuffer: VertexBuffer): Boolean = TODO("Not yet implemented")

    actual fun isValidSkinningBuffer(skinningBuffer: SkinningBuffer): Boolean = TODO("Not yet implemented")

    actual fun isValidMorphTargetBuffer(morphTargetBuffer: MorphTargetBuffer): Boolean = TODO("Not yet implemented")

    actual fun isValidIndirectLight(indirectLight: IndirectLight): Boolean = TODO("Not yet implemented")

    actual fun isValidMaterial(material: Material): Boolean = TODO("Not yet implemented")

    actual fun isValidMaterialInstance(material: Material, materialInstance: MaterialInstance): Boolean =
        TODO("Not yet implemented")

    actual fun isValidExpensiveMaterialInstance(materialInstance: MaterialInstance): Boolean = TODO("Not yet implemented")

    actual fun isValidSkybox(skybox: Skybox): Boolean = TODO("Not yet implemented")

    actual fun isValidColorGrading(colorGrading: ColorGrading): Boolean = TODO("Not yet implemented")

    actual fun isValidTexture(texture: Texture): Boolean = TODO("Not yet implemented")

    actual fun isValidRenderTarget(renderTarget: RenderTarget): Boolean = TODO("Not yet implemented")

    actual fun isValidSwapChain(swapChain: SwapChain): Boolean = TODO("Not yet implemented")

    actual fun createView(): View = TODO("Not yet implemented")

    actual fun destroyView(view: View) {
    }

    actual fun createRenderer(): Renderer = TODO("Not yet implemented")

    actual fun destroyRenderer(renderer: Renderer) {
    }

    actual fun createCamera(@Entity entity: Int): Camera = TODO("Not yet implemented")

    actual fun getCameraComponent(@Entity entity: Int): Camera? = TODO("Not yet implemented")

    actual fun destroyCameraComponent(@Entity entity: Int) {
    }

    actual fun createScene(): Scene = TODO("Not yet implemented")

    actual fun destroyScene(scene: Scene) {
    }

    actual fun destroyStream(stream: Stream) {
    }

    actual fun createFence(): Fence = TODO("Not yet implemented")

    actual fun destroyFence(fence: Fence) {
    }

    actual fun destroyIndexBuffer(indexBuffer: IndexBuffer) {
    }

    actual fun destroyVertexBuffer(vertexBuffer: VertexBuffer) {
    }

    actual fun destroySkinningBuffer(skinningBuffer: SkinningBuffer) {
    }

    actual fun destroyMorphTargetBuffer(morphTargetBuffer: MorphTargetBuffer) {
    }

    actual fun destroyIndirectLight(indirectLight: IndirectLight) {
    }

    actual fun destroyMaterial(material: Material) {
    }

    actual fun destroyMaterialInstance(materialInstance: MaterialInstance) {
    }

    actual fun destroySkybox(skybox: Skybox) {
    }

    actual fun destroyColorGrading(colorGrading: ColorGrading) {
    }

    actual fun destroyTexture(texture: Texture) {
    }

    actual fun destroyRenderTarget(target: RenderTarget) {
    }

    actual fun destroyEntity(@Entity entity: Int) {
    }

    actual fun getTransformManager(): TransformManager = TODO("Not yet implemented")

    actual fun getLightManager(): LightManager = TODO("Not yet implemented")

    actual fun getRenderableManager(): RenderableManager = TODO("Not yet implemented")

    actual fun getEntityManager(): EntityManager = TODO("Not yet implemented")

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

    actual fun getNativeObject(): Long = TODO("Not yet implemented")

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
        actual var commandBufferSizeMB: Long = 0
        actual var perRenderPassArenaSizeMB: Long = 0
        actual var driverHandleArenaSizeMB: Long = 0
        actual var minCommandBufferSizeMB: Long = 0
        actual var perFrameCommandsSizeMB: Long = 0
        actual var jobSystemThreadCount: Long = 0
        actual var textureUseAfterFreePoolSize: Long = 0
        @Deprecated("Use feature flag \"backend.disable_parallel_shader_compile\" instead.")
        actual var disableParallelShaderCompile: Boolean = false
        actual var stereoscopicType: StereoscopicType = StereoscopicType.NONE
        actual var stereoscopicEyeCount: Long = 0
        @Deprecated("This value is no longer used.")
        actual var resourceAllocatorCacheSizeMB: Long = 0
        actual var resourceAllocatorCacheMaxAge: Long = 0
        @Deprecated("Use feature flag \"backend.disable_handle_use_after_free_check\" instead.")
        actual var disableHandleUseAfterFreeCheck: Boolean = false
        actual var preferredShaderLanguage: ShaderLanguage = ShaderLanguage.DEFAULT
        actual var forceGLES2Context: Boolean = false
        @Deprecated("Use feature flag \"backend.opengl.assert_native_window_is_valid\" instead.")
        actual var assertNativeWindowIsValid: Boolean = false
        actual var gpuContextPriority: GpuContextPriority = GpuContextPriority.DEFAULT
        actual var sharedUboInitialSizeInBytes: Long = 0

        actual enum class ShaderLanguage {
            DEFAULT,
            MSL,
            METAL_LIBRARY,
        }
    }

    actual class Builder {
        actual fun backend(backend: Backend): Builder = this

        actual fun sharedContext(sharedContext: Any): Builder = this

        actual fun config(config: Config): Builder = this

        actual fun featureLevel(featureLevel: FeatureLevel): Builder = this

        actual fun paused(paused: Boolean): Builder = this

        actual fun feature(name: String, value: Boolean): Builder = this

        actual fun build(): Engine = TODO("Not yet implemented")
    }

    actual companion object {
        actual fun create(): Engine = TODO("Not yet implemented")

        actual fun create(backend: Backend): Engine = TODO("Not yet implemented")

        actual fun create(sharedContext: Any): Engine = TODO("Not yet implemented")

        actual fun getSteadyClockTimeNano(): Long = TODO("Not yet implemented")
    }
}
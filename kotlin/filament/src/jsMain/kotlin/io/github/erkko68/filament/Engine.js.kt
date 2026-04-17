package io.github.erkko68.filament

actual class Engine {
    actual fun isValid(): Boolean {
        TODO("Not yet implemented")
    }

    actual fun destroy() {
    }

    actual fun getBackend(): Backend {
        TODO("Not yet implemented")
    }

    actual fun getSupportedFeatureLevel(): FeatureLevel {
        TODO("Not yet implemented")
    }

    actual fun setActiveFeatureLevel(featureLevel: FeatureLevel): FeatureLevel {
        TODO("Not yet implemented")
    }

    actual fun getActiveFeatureLevel(): FeatureLevel {
        TODO("Not yet implemented")
    }

    actual fun setAutomaticInstancingEnabled(enable: Boolean) {
    }

    actual fun isAutomaticInstancingEnabled(): Boolean {
        TODO("Not yet implemented")
    }

    actual fun getConfig(): Config {
        TODO("Not yet implemented")
    }

    actual fun getMaxStereoscopicEyes(): Long {
        TODO("Not yet implemented")
    }

    actual fun isValidRenderer(renderer: Renderer): Boolean {
        TODO("Not yet implemented")
    }

    actual fun isValidView(view: View): Boolean {
        TODO("Not yet implemented")
    }

    actual fun isValidScene(scene: Scene): Boolean {
        TODO("Not yet implemented")
    }

    actual fun isValidFence(fence: Fence): Boolean {
        TODO("Not yet implemented")
    }

    actual fun isValidRenderTarget(renderTarget: RenderTarget): Boolean {
        TODO("Not yet implemented")
    }

    actual fun isValidIndexBuffer(indexBuffer: IndexBuffer): Boolean {
        TODO("Not yet implemented")
    }

    actual fun isValidVertexBuffer(vertexBuffer: VertexBuffer): Boolean {
        TODO("Not yet implemented")
    }

    actual fun isValidSkinningBuffer(skinningBuffer: SkinningBuffer): Boolean {
        TODO("Not yet implemented")
    }

    actual fun isValidMorphTargetBuffer(morphTargetBuffer: MorphTargetBuffer): Boolean {
        TODO("Not yet implemented")
    }

    actual fun isValidIndirectLight(ibl: IndirectLight): Boolean {
        TODO("Not yet implemented")
    }

    actual fun isValidMaterial(material: Material): Boolean {
        TODO("Not yet implemented")
    }

    actual fun isValidMaterialInstance(
        material: Material,
        materialInstance: MaterialInstance
    ): Boolean {
        TODO("Not yet implemented")
    }

    actual fun isValidExpensiveMaterialInstance(materialInstance: MaterialInstance): Boolean {
        TODO("Not yet implemented")
    }

    actual fun isValidSkybox(skybox: Skybox): Boolean {
        TODO("Not yet implemented")
    }

    actual fun isValidColorGrading(colorGrading: ColorGrading): Boolean {
        TODO("Not yet implemented")
    }

    actual fun isValidTexture(texture: Texture): Boolean {
        TODO("Not yet implemented")
    }

    actual fun isValidStream(stream: Stream): Boolean {
        TODO("Not yet implemented")
    }

    actual fun isValidSwapChain(swapChain: SwapChain): Boolean {
        TODO("Not yet implemented")
    }

    actual fun createSwapChain(surface: NativeSurface): SwapChain {
        TODO("Not yet implemented")
    }

    actual fun createSwapChain(
        surface: NativeSurface,
        flags: Long
    ): SwapChain {
        TODO("Not yet implemented")
    }

    actual fun createSwapChain(
        width: Int,
        height: Int,
        flags: Long
    ): SwapChain {
        TODO("Not yet implemented")
    }

    actual fun destroySwapChain(swapChain: SwapChain) {
    }

    actual fun createView(): View {
        TODO("Not yet implemented")
    }

    actual fun destroyView(view: View) {
    }

    actual fun createRenderer(): Renderer {
        TODO("Not yet implemented")
    }

    actual fun destroyRenderer(renderer: Renderer) {
    }

    actual fun createCamera(): Camera {
        TODO("Not yet implemented")
    }

    actual fun createCamera(entity: Int): Camera {
        TODO("Not yet implemented")
    }

    actual fun getCameraComponent(entity: Int): Camera? {
        TODO("Not yet implemented")
    }

    actual fun destroyCamera(camera: Camera) {
    }

    actual fun destroyCameraComponent(entity: Int) {
    }

    actual fun createScene(): Scene {
        TODO("Not yet implemented")
    }

    actual fun destroyScene(scene: Scene) {
    }

    actual fun createFence(): Fence {
        TODO("Not yet implemented")
    }

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

    actual fun destroyIndirectLight(ibl: IndirectLight) {
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

    actual fun destroyStream(stream: Stream) {
    }

    actual fun destroyEntity(entity: Int) {
    }

    actual fun getTransformManager(): TransformManager {
        TODO("Not yet implemented")
    }

    actual fun getLightManager(): LightManager {
        TODO("Not yet implemented")
    }

    actual fun getRenderableManager(): RenderableManager {
        TODO("Not yet implemented")
    }

    actual fun getEntityManager(): EntityManager {
        TODO("Not yet implemented")
    }

    actual fun flushAndWait() {
    }

    actual fun flushAndWait(timeout: Long): Boolean {
        TODO("Not yet implemented")
    }

    actual fun flush() {
    }

    actual fun isPaused(): Boolean {
        TODO("Not yet implemented")
    }

    actual fun setPaused(paused: Boolean) {
    }

    actual fun unprotected() {
    }

    actual fun hasFeatureFlag(name: String): Boolean {
        TODO("Not yet implemented")
    }

    actual fun setFeatureFlag(name: String, value: Boolean): Boolean {
        TODO("Not yet implemented")
    }

    actual fun getFeatureFlag(name: String): Boolean {
        TODO("Not yet implemented")
    }

    actual enum class Backend { DEFAULT, OPENGL, VULKAN, METAL, WEBGPU, NOOP }
    actual enum class FeatureLevel { FEATURE_LEVEL_0, FEATURE_LEVEL_1, FEATURE_LEVEL_2, FEATURE_LEVEL_3 }
    actual enum class StereoscopicType { NONE, INSTANCED, MULTIVIEW }
    actual enum class GpuContextPriority { DEFAULT, LOW, MEDIUM, HIGH, REALTIME }
    actual class Config {
        actual var commandBufferSizeMB: Long
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var perRenderPassArenaSizeMB: Long
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var driverHandleArenaSizeMB: Long
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var minCommandBufferSizeMB: Long
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var perFrameCommandsSizeMB: Long
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var jobSystemThreadCount: Long
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var stereoscopicType: StereoscopicType
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var stereoscopicEyeCount: Long
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var resourceAllocatorCacheSizeMB: Long
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var resourceAllocatorCacheMaxAge: Long
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var preferredShaderLanguage: ShaderLanguage
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var forceGLES2Context: Boolean
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var gpuContextPriority: GpuContextPriority
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var sharedUboInitialSizeInBytes: Long
            get() = TODO("Not yet implemented")
            set(value) {}

        actual enum class ShaderLanguage { DEFAULT, MSL, METAL_LIBRARY }
    }

    actual class Builder {
        actual fun backend(backend: Backend): Builder {
            TODO("Not yet implemented")
        }

        actual fun sharedContext(sharedContext: Any): Builder {
            TODO("Not yet implemented")
        }

        actual fun config(config: Config): Builder {
            TODO("Not yet implemented")
        }

        actual fun featureLevel(featureLevel: FeatureLevel): Builder {
            TODO("Not yet implemented")
        }

        actual fun paused(paused: Boolean): Builder {
            TODO("Not yet implemented")
        }

        actual fun feature(name: String, value: Boolean): Builder {
            TODO("Not yet implemented")
        }

        actual fun build(): Engine {
            TODO("Not yet implemented")
        }
    }

    actual companion object {
        actual fun create(): Engine {
            TODO("Not yet implemented")
        }

        actual fun create(backend: Backend): Engine {
            TODO("Not yet implemented")
        }

        actual fun create(sharedContext: Any): Engine {
            TODO("Not yet implemented")
        }

        actual fun getSteadyClockTimeNano(): Long {
            TODO("Not yet implemented")
        }
    }
}
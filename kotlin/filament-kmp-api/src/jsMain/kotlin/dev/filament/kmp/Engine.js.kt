package dev.filament.kmp

actual class Engine {
    actual fun isValid(): Boolean = TODO("Not yet implemented")

    actual fun createFence(): Fence = TODO("Not yet implemented")

    actual fun createSwapChain(surface: Any): SwapChain = TODO("Not yet implemented")

    actual fun createSwapChainFromNativeSurface(nativeSurface: Long, flags: Long): SwapChain = TODO("Not yet implemented")

    actual fun destroy() {
    }

    actual fun destroyEntity(@Entity entity: Int) {
    }

    actual fun destroyFence(fence: Fence) {
    }

    actual fun enableAccurateTranslations() {
        TODO("Not yet implemented")
    }

    actual fun flush() {
        TODO("Not yet implemented")
    }

    actual fun flushAndWait() {
        TODO("Not yet implemented")
    }

    actual fun flushAndWait(timeout: Long) {
        TODO("Not yet implemented")
    }

    actual fun getActiveFeatureLevel(): Int = TODO("Not yet implemented")

    actual fun getBackend(): Int = TODO("Not yet implemented")

    actual fun getConfig(): EngineConfig = TODO("Not yet implemented")

    actual fun getEntityManager(): EntityManager = TODO("Not yet implemented")

    actual fun getFeatureFlag(flag: Int): Boolean = TODO("Not yet implemented")

    actual fun getMaxStereoscopicEyes(): Int = TODO("Not yet implemented")

    actual fun getNativeJobSystem(): Long = TODO("Not yet implemented")

    actual fun getNativeObject(): Long = TODO("Not yet implemented")

    actual fun getSteadyClockTimeNano(): Long = TODO("Not yet implemented")

    actual fun getSupportedFeatureLevel(): Int = TODO("Not yet implemented")

    actual fun hasFeatureFlag(flag: Int): Boolean = TODO("Not yet implemented")

    actual fun isAutomaticInstancingEnabled(): Boolean = TODO("Not yet implemented")

    actual fun isPaused(): Boolean = TODO("Not yet implemented")

    actual fun isValidColorGrading(colorGrading: ColorGrading): Boolean = TODO("Not yet implemented")

    actual fun isValidExpensiveMaterialInstance(materialInstance: MaterialInstance): Boolean = TODO("Not yet implemented")

    actual fun isValidFence(fence: Fence): Boolean = TODO("Not yet implemented")

    actual fun isValidIndexBuffer(indexBuffer: IndexBuffer): Boolean = TODO("Not yet implemented")

    actual fun isValidIndirectLight(indirectLight: IndirectLight): Boolean = TODO("Not yet implemented")

    actual fun isValidMaterial(material: Material): Boolean = TODO("Not yet implemented")

    actual fun isValidMaterialInstance(materialInstance: MaterialInstance, includingDefaultInstance: Boolean): Boolean =
        TODO("Not yet implemented")

    actual fun isValidMorphTargetBuffer(morphTargetBuffer: MorphTargetBuffer): Boolean = TODO("Not yet implemented")

    actual fun isValidRenderTarget(renderTarget: RenderTarget): Boolean = TODO("Not yet implemented")

    actual fun isValidRenderer(renderer: Renderer): Boolean = TODO("Not yet implemented")

    actual fun isValidScene(scene: Scene): Boolean = TODO("Not yet implemented")

    actual fun isValidSkinningBuffer(skinningBuffer: SkinningBuffer): Boolean = TODO("Not yet implemented")

    actual fun isValidSkybox(skybox: Skybox): Boolean = TODO("Not yet implemented")

    actual fun isValidStream(stream: Stream): Boolean = TODO("Not yet implemented")

    actual fun isValidSwapChain(swapChain: SwapChain): Boolean = TODO("Not yet implemented")

    actual fun isValidTexture(texture: Texture): Boolean = TODO("Not yet implemented")

    actual fun isValidVertexBuffer(vertexBuffer: VertexBuffer): Boolean = TODO("Not yet implemented")

    actual fun isValidView(view: View): Boolean = TODO("Not yet implemented")

    actual fun setActiveFeatureLevel(featureLevel: Int) {
        TODO("Not yet implemented")
    }

    actual fun setAutomaticInstancingEnabled(enabled: Boolean) {
        TODO("Not yet implemented")
    }

    actual fun setFeatureFlag(flag: Int, enabled: Boolean) {
        TODO("Not yet implemented")
    }

    actual fun setPaused(paused: Boolean) {
        TODO("Not yet implemented")
    }

    actual fun unprotected() {
        TODO("Not yet implemented")
    }

    actual fun createRenderer(): Renderer {
        TODO("Not yet implemented")
    }

    actual fun destroyRenderer(renderer: Renderer) {
    }

    actual fun createScene(): Scene {
        TODO("Not yet implemented")
    }

    actual fun destroyScene(scene: Scene) {
    }

    actual fun createView(): View {
        TODO("Not yet implemented")
    }

    actual fun destroyView(view: View) {
    }

    actual fun createCamera(@Entity entity: Int): Camera {
        TODO("Not yet implemented")
    }

    actual fun getCameraComponent(@Entity entity: Int): Camera? {
        TODO("Not yet implemented")
    }

    actual fun destroyCameraComponent(@Entity entity: Int) {
    }

    actual fun createSwapChain(surface: Any, flags: Long): SwapChain {
        TODO("Not yet implemented")
    }

    actual fun createSwapChain(width: Int, height: Int, flags: Long): SwapChain {
        TODO("Not yet implemented")
    }

    actual fun destroySwapChain(swapChain: SwapChain) {
    }

    actual fun destroyRenderTarget(target: RenderTarget) {
    }

    actual fun destroyTexture(texture: Texture) {
    }

    actual fun destroySkybox(skybox: Skybox) {
    }

    actual fun destroyStream(stream: Stream) {
    }

    actual fun destroySkinningBuffer(skinningBuffer: SkinningBuffer) {
    }

    actual fun destroyMorphTargetBuffer(morphTargetBuffer: MorphTargetBuffer) {
    }

    actual fun destroyIndirectLight(indirectLight: IndirectLight) {
    }

    actual fun destroyColorGrading(colorGrading: ColorGrading) {
    }


    actual fun destroyIndexBuffer(indexBuffer: IndexBuffer) {
    }

    actual fun destroyVertexBuffer(vertexBuffer: VertexBuffer) {
    }

    actual fun destroyMaterial(material: Material) {
    }

    actual fun destroyMaterialInstance(materialInstance: MaterialInstance) {
    }

    actual fun getRenderableManager(): RenderableManager {
        TODO("Not yet implemented")
    }

    actual fun getTransformManager(): TransformManager {
        TODO("Not yet implemented")
    }

    actual fun getLightManager(): LightManager {
        TODO("Not yet implemented")
    }

    actual fun close() {
    }

    actual companion object {
        actual fun create(): Engine {
            TODO("Not yet implemented")
        }

        actual fun create(config: EngineConfig): Engine {
            TODO("Not yet implemented")
        }
    }
}
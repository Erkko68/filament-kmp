package io.github.erkko68.filament

import io.github.erkko68.filament.js.Engine as JSEngine
import io.github.erkko68.filament.js.EntityManager as JSEntityManager
import io.github.erkko68.filament.js.Entity as JSEntity
import org.w3c.dom.HTMLCanvasElement

actual class Engine private constructor(internal val jsEngine: JSEngine) {
    actual fun isValid(): Boolean {
        return true
    }

    actual fun destroy() {
        // JS engine is typically managed by the GC
    }

    actual fun getBackend(): Backend {
        return Backend.WebGL
    }

    actual fun getActiveFeatureLevel(): FeatureLevel {
        return FeatureLevel.FEATURE_LEVEL_1
    }

    actual fun setAutomaticInstancingEnabled(enable: Boolean) {
    }

    actual fun isAutomaticInstancingEnabled(): Boolean {
        return false
    }

    actual fun getConfig(): Config {
        return Config()
    }

    actual fun getMaxStereoscopicEyes(): Long {
        return JSEngine.getMaxStereoscopicEyes().toLong()
    }

    actual fun isValidRenderer(renderer: Renderer): Boolean = true
    actual fun isValidView(view: View): Boolean = true
    actual fun isValidScene(scene: Scene): Boolean = true
    actual fun isValidFence(fence: Fence): Boolean = true
    actual fun isValidRenderTarget(renderTarget: RenderTarget): Boolean = true
    actual fun isValidIndexBuffer(indexBuffer: IndexBuffer): Boolean = true
    actual fun isValidVertexBuffer(vertexBuffer: VertexBuffer): Boolean = true
    actual fun isValidSkinningBuffer(skinningBuffer: SkinningBuffer): Boolean = true
    actual fun isValidMorphTargetBuffer(morphTargetBuffer: MorphTargetBuffer): Boolean = true
    actual fun isValidIndirectLight(ibl: IndirectLight): Boolean = true
    actual fun isValidMaterial(material: Material): Boolean = true
    actual fun isValidMaterialInstance(material: Material, materialInstance: MaterialInstance): Boolean = true
    actual fun isValidExpensiveMaterialInstance(materialInstance: MaterialInstance): Boolean = true
    actual fun isValidSkybox(skybox: Skybox): Boolean = true
    actual fun isValidColorGrading(colorGrading: ColorGrading): Boolean = true
    actual fun isValidTexture(texture: Texture): Boolean = true
    actual fun isValidStream(stream: Stream): Boolean = true
    actual fun isValidSwapChain(swapChain: SwapChain): Boolean = true

    actual fun createSwapChain(surface: NativeSurface): SwapChain {
        return SwapChain(jsEngine.createSwapChain())
    }

    actual fun createSwapChain(
        surface: NativeSurface,
        flags: Long
    ): SwapChain {
        return createSwapChain(surface)
    }

    actual fun createSwapChain(
        width: Int,
        height: Int,
        flags: Long
    ): SwapChain {
        return createSwapChain(Any())
    }

    actual fun destroySwapChain(swapChain: SwapChain) {
        jsEngine.destroySwapChain(swapChain.jsSwapChain)
    }

    actual fun createView(): View {
        return View(jsEngine.createView())
    }

    actual fun destroyView(view: View) {
        jsEngine.destroyView(view.jsView)
    }

    actual fun createRenderer(): Renderer {
        return Renderer(jsEngine.createRenderer())
    }

    actual fun destroyRenderer(renderer: Renderer) {
        jsEngine.destroyRenderer(renderer.jsRenderer)
    }

    actual fun createCamera(): Camera {
        val entity = EntityManager.get().create()
        return Camera(jsEngine.createCamera(entity.unsafeCast<JSEntity>()))
    }

    actual fun createCamera(entity: Int): Camera {
        return Camera(jsEngine.createCamera(entity.unsafeCast<JSEntity>()))
    }

    actual fun getCameraComponent(entity: Int): Camera? {
        return Camera(jsEngine.getCameraComponent(entity.unsafeCast<JSEntity>()))
    }

    actual fun destroyCamera(camera: Camera) {
        // Destroyed via its entity component in JS
    }

    actual fun destroyCameraComponent(entity: Int) {
        jsEngine.destroyCameraComponent(entity.unsafeCast<JSEntity>())
    }

    actual fun createScene(): Scene {
        return Scene(jsEngine.createScene())
    }

    actual fun destroyScene(scene: Scene) {
        jsEngine.destroyScene(scene.jsScene)
    }

    actual fun createFence(): Fence {
        return Fence()
    }

    actual fun destroyFence(fence: Fence) {
    }

    actual fun destroyIndexBuffer(indexBuffer: IndexBuffer) {
        jsEngine.destroyIndexBuffer(indexBuffer.jsIndexBuffer)
    }

    actual fun destroyVertexBuffer(vertexBuffer: VertexBuffer) {
        jsEngine.destroyVertexBuffer(vertexBuffer.jsVertexBuffer)
    }

    actual fun destroySkinningBuffer(skinningBuffer: SkinningBuffer) {
    }

    actual fun destroyMorphTargetBuffer(morphTargetBuffer: MorphTargetBuffer) {
    }

    actual fun destroyIndirectLight(ibl: IndirectLight) {
        jsEngine.destroyIndirectLight(ibl.jsIndirectLight)
    }

    actual fun destroyMaterial(material: Material) {
        jsEngine.destroyMaterial(material.jsMaterial)
    }

    actual fun destroyMaterialInstance(materialInstance: MaterialInstance) {
        jsEngine.destroyMaterialInstance(materialInstance.jsMaterialInstance)
    }

    actual fun destroySkybox(skybox: Skybox) {
        jsEngine.destroySkybox(skybox.jsSkybox)
    }

    actual fun destroyColorGrading(colorGrading: ColorGrading) {
        jsEngine.destroyColorGrading(colorGrading.jsColorGrading)
    }

    actual fun destroyTexture(texture: Texture) {
        jsEngine.destroyTexture(texture.jsTexture)
    }

    actual fun destroyRenderTarget(target: RenderTarget) {
        jsEngine.destroyRenderTarget(target.jsRenderTarget)
    }

    actual fun destroyStream(stream: Stream) {
    }

    actual fun destroyEntity(entity: Int) {
        jsEngine.destroyEntity(entity.unsafeCast<JSEntity>())
    }

    actual fun getTransformManager(): TransformManager {
        return TransformManager(jsEngine.getTransformManager())
    }

    actual fun getLightManager(): LightManager {
        return LightManager(jsEngine.getLightManager())
    }

    actual fun getRenderableManager(): RenderableManager {
        return RenderableManager(jsEngine.getRenderableManager())
    }

    actual fun getEntityManager(): EntityManager {
        return EntityManager(JSEntityManager.get())
    }

    actual fun flushAndWait() {
        jsEngine.execute()
    }

    actual fun flushAndWait(timeout: Long): Boolean {
        jsEngine.execute()
        return true
    }

    actual fun flush() {
        jsEngine.execute()
    }

    actual fun isPaused(): Boolean {
        return false
    }

    actual fun setPaused(paused: Boolean) {
    }

    actual fun unprotected() {
    }

    actual fun hasFeatureFlag(name: String): Boolean {
        return false
    }

    actual fun setFeatureFlag(name: String, value: Boolean): Boolean {
        return false
    }

    actual fun getFeatureFlag(name: String): Boolean {
        return false
    }

    actual enum class Backend { DEFAULT, OPENGL, VULKAN, METAL, WEBGPU, NOOP }
    actual enum class FeatureLevel { FEATURE_LEVEL_0, FEATURE_LEVEL_1, FEATURE_LEVEL_2, FEATURE_LEVEL_3 }
    actual enum class StereoscopicType { NONE, INSTANCED, MULTIVIEW }
    actual enum class GpuContextPriority { DEFAULT, LOW, MEDIUM, HIGH, REALTIME }
    actual class Config {
        actual var commandBufferSizeMB: Long = 64
        actual var perRenderPassArenaSizeMB: Long = 32
        actual var driverHandleArenaSizeMB: Long = 16
        actual var minCommandBufferSizeMB: Long = 16
        actual var perFrameCommandsSizeMB: Long = 8
        actual var jobSystemThreadCount: Long = 1
        actual var stereoscopicType: StereoscopicType = StereoscopicType.NONE
        actual var stereoscopicEyeCount: Long = 1
        actual var resourceAllocatorCacheSizeMB: Long = 64
        actual var resourceAllocatorCacheMaxAge: Long = 60
        actual var preferredShaderLanguage: ShaderLanguage = ShaderLanguage.DEFAULT
        actual var forceGLES2Context: Boolean = false
        actual var gpuContextPriority: GpuContextPriority = GpuContextPriority.DEFAULT
        actual var sharedUboInitialSizeInBytes: Long = 1024

        actual enum class ShaderLanguage { DEFAULT, MSL, METAL_LIBRARY }
    }

    actual class Builder {
        actual fun backend(backend: Backend): Builder = this
        actual fun sharedContext(sharedContext: Any): Builder = this
        actual fun config(config: Config): Builder = this
        actual fun featureLevel(featureLevel: FeatureLevel): Builder = this
        actual fun paused(paused: Boolean): Builder = this
        actual fun feature(name: String, value: Boolean): Builder = this
        actual fun build(): Engine = create()
    }

    actual companion object {
        actual fun create(): Engine {
            throw UnsupportedOperationException("On JS, Engine must be created with a canvas or initialized via Filament.init()")
        }

        actual fun create(backend: Backend): Engine {
            return create()
        }

        actual fun create(sharedContext: Any): Engine {
            if (sharedContext is HTMLCanvasElement) {
                return Engine(JSEngine.create(sharedContext))
            }
            return create()
        }

        actual fun getSteadyClockTimeNano(): Long {
            return (kotlin.js.Date.now() * 1_000_000.0).toLong()
        }
    }
}
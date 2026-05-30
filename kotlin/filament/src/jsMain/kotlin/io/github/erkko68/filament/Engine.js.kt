package io.github.erkko68.filament

import io.github.erkko68.filament.js.Engine as JSEngine
import io.github.erkko68.filament.js.EntityManager as JSEntityManager
import io.github.erkko68.filament.js.Entity as JSEntity
import org.w3c.dom.HTMLCanvasElement

actual class Engine private constructor(val jsEngine: JSEngine, val jsCanvas: HTMLCanvasElement? = null) {
    actual fun isValid(): Boolean {
        return true
    }

    actual fun destroy() {
        // JS engine is typically managed by the GC
    }

    actual fun getBackend(): Backend = fromJsBackend(jsEngine.getBackend())

    actual fun getSupportedFeatureLevel(): FeatureLevel = fromJsFeatureLevel(jsEngine.getSupportedFeatureLevel())

    actual fun setActiveFeatureLevel(featureLevel: FeatureLevel): FeatureLevel =
        fromJsFeatureLevel(jsEngine.setActiveFeatureLevel(toJsFeatureLevel(featureLevel)))

    actual fun getActiveFeatureLevel(): FeatureLevel = fromJsFeatureLevel(jsEngine.getActiveFeatureLevel())

    actual fun setAutomaticInstancingEnabled(enable: Boolean) {
        jsEngine.setAutomaticInstancingEnabled(enable)
    }

    actual fun isAutomaticInstancingEnabled(): Boolean = jsEngine.isAutomaticInstancingEnabled()

    actual fun getConfig(): Config {
        // The JS binding's getConfig() returns a JS object with the same shape
        // as Engine.Config. Map it back into our actual class. Fields the JS
        // binding doesn't expose (stereoscopicType, gpuContextPriority,
        // preferredShaderLanguage as enum) keep their defaults.
        val jsCfg = jsEngine.getConfig()
        return Config().apply {
            jsCfg.commandBufferSizeMB?.let { commandBufferSizeMB = it.toLong() }
            jsCfg.perRenderPassArenaSizeMB?.let { perRenderPassArenaSizeMB = it.toLong() }
            jsCfg.driverHandleArenaSizeMB?.let { driverHandleArenaSizeMB = it.toLong() }
            jsCfg.minCommandBufferSizeMB?.let { minCommandBufferSizeMB = it.toLong() }
            jsCfg.perFrameCommandsSizeMB?.let { perFrameCommandsSizeMB = it.toLong() }
            jsCfg.jobSystemThreadCount?.let { jobSystemThreadCount = it.toLong() }
            jsCfg.stereoscopicEyeCount?.let { stereoscopicEyeCount = it.toLong() }
            jsCfg.resourceAllocatorCacheSizeMB?.let { resourceAllocatorCacheSizeMB = it.toLong() }
            jsCfg.resourceAllocatorCacheMaxAge?.let { resourceAllocatorCacheMaxAge = it.toLong() }
            jsCfg.sharedUboInitialSizeInBytes?.let { sharedUboInitialSizeInBytes = it.toLong() }
            jsCfg.forceGLES2Context?.let { forceGLES2Context = it }
        }
    }

    actual fun getMaxStereoscopicEyes(): Long {
        return JSEngine.getMaxStereoscopicEyes().toLong()
    }

    actual fun isValidRenderer(renderer: Renderer): Boolean = jsEngine.isValidRenderer(renderer.jsRenderer)
    actual fun isValidView(view: View): Boolean = jsEngine.isValidView(view.jsView)
    actual fun isValidScene(scene: Scene): Boolean = jsEngine.isValidScene(scene.jsScene)
    actual fun isValidFence(fence: Fence): Boolean = true // TODO(js): Fence not bound in jsbindings.cpp
    actual fun isValidRenderTarget(renderTarget: RenderTarget): Boolean = jsEngine.isValidRenderTarget(renderTarget.jsRenderTarget)
    actual fun isValidIndexBuffer(indexBuffer: IndexBuffer): Boolean = jsEngine.isValidIndexBuffer(indexBuffer.jsIndexBuffer)
    actual fun isValidVertexBuffer(vertexBuffer: VertexBuffer): Boolean = jsEngine.isValidVertexBuffer(vertexBuffer.jsVertexBuffer)
    actual fun isValidSkinningBuffer(skinningBuffer: SkinningBuffer): Boolean = true // TODO(js): SkinningBuffer not bound in jsbindings.cpp
    actual fun isValidMorphTargetBuffer(morphTargetBuffer: MorphTargetBuffer): Boolean = true // TODO(js): MorphTargetBuffer not bound in jsbindings.cpp
    actual fun isValidIndirectLight(ibl: IndirectLight): Boolean = jsEngine.isValidIndirectLight(ibl.jsIndirectLight)
    actual fun isValidMaterial(material: Material): Boolean = jsEngine.isValidMaterial(material.jsMaterial)
    actual fun isValidMaterialInstance(material: Material, materialInstance: MaterialInstance): Boolean =
        jsEngine.isValidMaterialInstance(material.jsMaterial, materialInstance.jsMaterialInstance)
    actual fun isValidExpensiveMaterialInstance(materialInstance: MaterialInstance): Boolean =
        jsEngine.isValidExpensiveMaterialInstance(materialInstance.jsMaterialInstance)
    actual fun isValidSkybox(skybox: Skybox): Boolean = jsEngine.isValidSkybox(skybox.jsSkybox)
    actual fun isValidColorGrading(colorGrading: ColorGrading): Boolean = jsEngine.isValidColorGrading(colorGrading.jsColorGrading)
    actual fun isValidTexture(texture: Texture): Boolean = jsEngine.isValidTexture(texture.jsTexture)
    actual fun isValidStream(stream: Stream): Boolean = true // TODO(js): Stream not bound in jsbindings.cpp
    actual fun isValidSwapChain(swapChain: SwapChain): Boolean = jsEngine.isValidSwapChain(swapChain.jsSwapChain)

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
        return SwapChain(jsEngine.createSwapChain())
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
        return Renderer(jsEngine.createRenderer(), this)
    }

    actual fun destroyRenderer(renderer: Renderer) {
        jsEngine.destroyRenderer(renderer.jsRenderer)
    }

    actual fun createCamera(): Camera {
        val entity = EntityManager.get().create()
        return Camera(jsEngine.createCamera(EntityManager.jsEntityOf(entity)), entity)
    }

    actual fun createCamera(entity: Entity): Camera {
        return Camera(jsEngine.createCamera(EntityManager.jsEntityOf(entity)), entity)
    }

    actual fun getCameraComponent(entity: Entity): Camera? {
        return Camera(jsEngine.getCameraComponent(EntityManager.jsEntityOf(entity)), entity)
    }

    actual fun destroyCamera(camera: Camera) {
        // Destroyed via its entity component in JS
    }

    actual fun destroyCameraComponent(entity: Entity) {
        jsEngine.destroyCameraComponent(EntityManager.jsEntityOf(entity))
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

    actual fun destroyEntity(entity: Entity) {
        jsEngine.destroyEntity(EntityManager.jsEntityOf(entity))
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

    // TODO(js): paused state not bound in upstream jsbindings.cpp — track locally
    // so the common getter/setter round-trip works.
    private var paused: Boolean = false

    actual fun isPaused(): Boolean = paused

    actual fun setPaused(paused: Boolean) {
        this.paused = paused
    }

    actual fun unprotected() {
        // jsEngine.unprotected() returns Boolean upstream; common API returns Unit.
        // Call and discard so the underlying state transition still happens.
        jsEngine.unprotected()
    }

    actual fun enableAccurateTranslations() {
        jsEngine.enableAccurateTranslations()
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

    actual fun compile(
        priority: CompilerPriorityQueue,
        material: Material,
        view: View,
        shadowReceiver: FeatureState,
        skinning: FeatureState,
        callback: (() -> Unit)?
    ) {
        // Material compilation happens offline in JS
        callback?.invoke()
    }

    actual enum class CompilerPriorityQueue { CRITICAL, HIGH, LOW }
    actual enum class FeatureState { FALSE, TRUE, INDETERMINATE }

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
            // On JS, Filament needs a WebGL-backed canvas. If no shared context is
            // provided, allocate a hidden offscreen <canvas> that the consumer can
            // read back from (see Engine.jsCanvas).
            val doc = kotlinx.browser.document
            val canvas = doc.createElement("canvas") as HTMLCanvasElement
            canvas.width = 1
            canvas.height = 1
            // Parked on body until a consumer (e.g. FilamentView via HtmlElementView)
            // adopts it into the Compose-managed DOM subtree.
            canvas.style.position = "absolute"
            canvas.style.left = "-9999px"
            canvas.style.top = "0"
            doc.body?.appendChild(canvas)
            return Engine(JSEngine.create(canvas), canvas)
        }

        actual fun create(backend: Backend): Engine {
            return create()
        }

        actual fun create(sharedContext: Any): Engine {
            if (sharedContext is HTMLCanvasElement) {
                return Engine(JSEngine.create(sharedContext), sharedContext)
            }
            return create()
        }

        actual fun getSteadyClockTimeNano(): Long {
            val jsVal = JSEngine.getSteadyClockTimeNano().asDynamic()
            val num = js("Number")(jsVal) as Double
            return num.toLong()
        }
    }
}

// ──────────────────────────────────────────────────────────────────────────────
// Enum bridges. The common Engine.Backend / Engine.FeatureLevel mirror the
// Android API; the external JS enums live in filament.js.kt under different
// names. These keep the mapping in one place.
//
// Note: common Engine.FeatureLevel includes FEATURE_LEVEL_0 (ES2-class
// hardware) which the JS binding does not expose — Filament's WebGL build
// targets GLES3/WebGL2 only. Mapping for FEATURE_LEVEL_0 falls back to
// FEATURE_LEVEL_1 on the JS side.
// ──────────────────────────────────────────────────────────────────────────────

private fun fromJsBackend(b: io.github.erkko68.filament.js.Backend): Engine.Backend = when (b) {
    io.github.erkko68.filament.js.Backend.DEFAULT -> Engine.Backend.DEFAULT
    io.github.erkko68.filament.js.Backend.OPENGL  -> Engine.Backend.OPENGL
    io.github.erkko68.filament.js.Backend.VULKAN  -> Engine.Backend.VULKAN
    io.github.erkko68.filament.js.Backend.METAL   -> Engine.Backend.METAL
    io.github.erkko68.filament.js.Backend.WEBGPU  -> Engine.Backend.WEBGPU
    io.github.erkko68.filament.js.Backend.NOOP    -> Engine.Backend.NOOP
}

private fun fromJsFeatureLevel(fl: io.github.erkko68.filament.js.FeatureLevel): Engine.FeatureLevel = when (fl) {
    io.github.erkko68.filament.js.FeatureLevel.FEATURE_LEVEL_1 -> Engine.FeatureLevel.FEATURE_LEVEL_1
    io.github.erkko68.filament.js.FeatureLevel.FEATURE_LEVEL_2 -> Engine.FeatureLevel.FEATURE_LEVEL_2
    io.github.erkko68.filament.js.FeatureLevel.FEATURE_LEVEL_3 -> Engine.FeatureLevel.FEATURE_LEVEL_3
}

private fun toJsFeatureLevel(fl: Engine.FeatureLevel): io.github.erkko68.filament.js.FeatureLevel = when (fl) {
    Engine.FeatureLevel.FEATURE_LEVEL_0,
    Engine.FeatureLevel.FEATURE_LEVEL_1 -> io.github.erkko68.filament.js.FeatureLevel.FEATURE_LEVEL_1
    Engine.FeatureLevel.FEATURE_LEVEL_2 -> io.github.erkko68.filament.js.FeatureLevel.FEATURE_LEVEL_2
    Engine.FeatureLevel.FEATURE_LEVEL_3 -> io.github.erkko68.filament.js.FeatureLevel.FEATURE_LEVEL_3
}
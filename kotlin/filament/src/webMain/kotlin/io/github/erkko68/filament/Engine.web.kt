package io.github.erkko68.filament


import io.github.erkko68.filament.web.interop.emptyJsObject
import io.github.erkko68.filament.web.interop.loseWebGlContext
import io.github.erkko68.filament.web.Engine as JSEngine
import io.github.erkko68.filament.web.Engine_Config as JSEngineConfig
import io.github.erkko68.filament.web.GpuContextPriority as JSGpuContextPriority
import io.github.erkko68.filament.web.ShaderLanguage as JSShaderLanguage
import io.github.erkko68.filament.web.StereoscopicType as JSStereoscopicType
import io.github.erkko68.filament.web.EngineCreateOptions as JSEngineCreateOptions
import io.github.erkko68.filament.web.EntityManager as JSEntityManager
import io.github.erkko68.filament.web.Entity as JSEntity
import org.w3c.dom.HTMLCanvasElement

actual class Engine private constructor(
    val jsEngine: JSEngine,
    val jsCanvas: HTMLCanvasElement? = null,
    // Only the hidden canvas we allocated ourselves is ours to tear down; a caller's shared
    // canvas outlives the engine and may back another one later.
    private val ownsCanvas: Boolean = false,
) {
    actual fun isValid(): Boolean {
        return true
    }

    actual fun destroy() {
        JSEngine.destroy(jsEngine)
        // The canvas and its GL context outlive the engine, and browsers cap how many
        // contexts are live at once, so release both here.
        if (ownsCanvas) jsCanvas?.let { canvas ->
            loseWebGlContext(canvas)
            canvas.remove()
        }
    }

    actual val backend: Backend get() = fromJsBackend(jsEngine.getBackend())

    actual val supportedFeatureLevel: FeatureLevel get() = fromJsFeatureLevel(jsEngine.getSupportedFeatureLevel())

    actual fun setActiveFeatureLevel(featureLevel: FeatureLevel): FeatureLevel =
        fromJsFeatureLevel(jsEngine.setActiveFeatureLevel(toJsFeatureLevel(featureLevel)))

    actual fun getActiveFeatureLevel(): FeatureLevel = fromJsFeatureLevel(jsEngine.getActiveFeatureLevel())

    actual fun setAutomaticInstancingEnabled(enable: Boolean) {
        jsEngine.setAutomaticInstancingEnabled(enable)
    }

    actual fun isAutomaticInstancingEnabled(): Boolean = jsEngine.isAutomaticInstancingEnabled()

    actual val config: Config get() {
        // getConfig() returns a JS object shaped like Engine.Config. The three enum fields
        // come back as embind enum objects, which map back only by identity — not worth it,
        // they keep the Config defaults.
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
            jsCfg.disableParallelShaderCompile?.let { disableParallelShaderCompile = it }
            jsCfg.disableHandleUseAfterFreeCheck?.let { disableHandleUseAfterFreeCheck = it }
            jsCfg.assertNativeWindowIsValid?.let { assertNativeWindowIsValid = it }
            jsCfg.enableMultipleDirectionalLights?.let { enableMultipleDirectionalLights = it }
        }
    }

    actual fun getMaxStereoscopicEyes(): Long {
        return JSEngine.getMaxStereoscopicEyes().toLong()
    }

    actual fun isValidRenderer(renderer: Renderer): Boolean = jsEngine.isValidRenderer(renderer.jsRenderer)
    actual fun isValidView(view: View): Boolean = jsEngine.isValidView(view.jsView)
    actual fun isValidScene(scene: Scene): Boolean = jsEngine.isValidScene(scene.jsScene)
    actual fun isValidFence(fence: Fence): Boolean = jsEngine.isValidFence(fence.jsFence)
    actual fun isValidRenderTarget(renderTarget: RenderTarget): Boolean = jsEngine.isValidRenderTarget(renderTarget.jsRenderTarget)
    actual fun isValidIndexBuffer(indexBuffer: IndexBuffer): Boolean = jsEngine.isValidIndexBuffer(indexBuffer.jsIndexBuffer)
    actual fun isValidVertexBuffer(vertexBuffer: VertexBuffer): Boolean = jsEngine.isValidVertexBuffer(vertexBuffer.jsVertexBuffer)
    actual fun isValidSkinningBuffer(skinningBuffer: SkinningBuffer): Boolean =
        jsEngine.isValidSkinningBuffer(skinningBuffer.jsSkinningBuffer)
    actual fun isValidMorphTargetBuffer(morphTargetBuffer: MorphTargetBuffer): Boolean =
        jsEngine.isValidMorphTargetBuffer(morphTargetBuffer.jsMorphTargetBuffer)
    actual fun isValidIndirectLight(ibl: IndirectLight): Boolean = jsEngine.isValidIndirectLight(ibl.jsIndirectLight)
    actual fun isValidMaterial(material: Material): Boolean = jsEngine.isValidMaterial(material.jsMaterial)
    actual fun isValidMaterialInstance(material: Material, materialInstance: MaterialInstance): Boolean =
        jsEngine.isValidMaterialInstance(material.jsMaterial, materialInstance.jsMaterialInstance)
    actual fun isValidExpensiveMaterialInstance(materialInstance: MaterialInstance): Boolean =
        jsEngine.isValidExpensiveMaterialInstance(materialInstance.jsMaterialInstance)
    actual fun isValidSkybox(skybox: Skybox): Boolean = jsEngine.isValidSkybox(skybox.jsSkybox)
    actual fun isValidColorGrading(colorGrading: ColorGrading): Boolean = jsEngine.isValidColorGrading(colorGrading.jsColorGrading)
    actual fun isValidTexture(texture: Texture): Boolean = jsEngine.isValidTexture(texture.jsTexture)
    actual fun isValidStream(stream: Stream): Boolean = jsUnsupported("Engine.isValidStream")
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

    actual fun createFence(): Fence = Fence(jsEngine.createFence())

    actual fun destroyFence(fence: Fence) {
        jsEngine.destroyFence(fence.jsFence)
    }

    actual fun destroyIndexBuffer(indexBuffer: IndexBuffer) {
        jsEngine.destroyIndexBuffer(indexBuffer.jsIndexBuffer)
    }

    actual fun destroyVertexBuffer(vertexBuffer: VertexBuffer) {
        jsEngine.destroyVertexBuffer(vertexBuffer.jsVertexBuffer)
    }

    actual fun destroySkinningBuffer(skinningBuffer: SkinningBuffer) {
        jsEngine.destroySkinningBuffer(skinningBuffer.jsSkinningBuffer)
    }

    actual fun destroyMorphTargetBuffer(morphTargetBuffer: MorphTargetBuffer) {
        jsEngine.destroyMorphTargetBuffer(morphTargetBuffer.jsMorphTargetBuffer)
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

    actual fun hasUnrecoverableFailure(): Boolean = jsEngine.hasUnrecoverableFailure()

    // TODO(js): paused state not bound in upstream jsbindings.cpp — track locally
    // so the common getter/setter round-trip works.
    private var _paused: Boolean = false

    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "state is only tracked locally — filament.js does not bind pause, so it has no effect on rendering.")
    actual var paused: Boolean
        get() = _paused
        set(value) { _paused = value }

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
    /** Defaults mirror Filament's own `Engine::Config` — they now reach the engine via `create`. */
    actual class Config {
        actual var commandBufferSizeMB: Long = 3
        actual var perRenderPassArenaSizeMB: Long = 3
        actual var driverHandleArenaSizeMB: Long = 0
        actual var minCommandBufferSizeMB: Long = 1
        actual var perFrameCommandsSizeMB: Long = 2
        actual var jobSystemThreadCount: Long = 0
        actual var disableParallelShaderCompile: Boolean = false
        actual var stereoscopicType: StereoscopicType = StereoscopicType.NONE
        actual var stereoscopicEyeCount: Long = 2
        actual var resourceAllocatorCacheSizeMB: Long = 64
        actual var resourceAllocatorCacheMaxAge: Long = 1
        actual var disableHandleUseAfterFreeCheck: Boolean = false
        actual var preferredShaderLanguage: ShaderLanguage = ShaderLanguage.DEFAULT
        actual var forceGLES2Context: Boolean = false
        actual var assertNativeWindowIsValid: Boolean = false
        actual var gpuContextPriority: GpuContextPriority = GpuContextPriority.DEFAULT
        actual var sharedUboInitialSizeInBytes: Long = 256 * 64
        actual var enableMultipleDirectionalLights: Boolean = false

        actual enum class ShaderLanguage { DEFAULT, MSL, METAL_LIBRARY }

        /** Filament.js merges this over `createDefaultConfig()`, so every field must be set. */
        internal fun toJs(): JSEngineConfig =
            emptyJsObject().unsafeCast<JSEngineConfig>().apply {
                commandBufferSizeMB = this@Config.commandBufferSizeMB.toDouble()
                perRenderPassArenaSizeMB = this@Config.perRenderPassArenaSizeMB.toDouble()
                driverHandleArenaSizeMB = this@Config.driverHandleArenaSizeMB.toDouble()
                minCommandBufferSizeMB = this@Config.minCommandBufferSizeMB.toDouble()
                perFrameCommandsSizeMB = this@Config.perFrameCommandsSizeMB.toDouble()
                jobSystemThreadCount = this@Config.jobSystemThreadCount.toDouble()
                disableParallelShaderCompile = this@Config.disableParallelShaderCompile
                stereoscopicType = when (this@Config.stereoscopicType) {
                    StereoscopicType.NONE -> JSStereoscopicType.NONE
                    StereoscopicType.INSTANCED -> JSStereoscopicType.INSTANCED
                    StereoscopicType.MULTIVIEW -> JSStereoscopicType.MULTIVIEW
                }
                stereoscopicEyeCount = this@Config.stereoscopicEyeCount.toDouble()
                resourceAllocatorCacheSizeMB = this@Config.resourceAllocatorCacheSizeMB.toDouble()
                resourceAllocatorCacheMaxAge = this@Config.resourceAllocatorCacheMaxAge.toDouble()
                disableHandleUseAfterFreeCheck = this@Config.disableHandleUseAfterFreeCheck
                preferredShaderLanguage = when (this@Config.preferredShaderLanguage) {
                    ShaderLanguage.DEFAULT -> JSShaderLanguage.DEFAULT
                    ShaderLanguage.MSL -> JSShaderLanguage.MSL
                    ShaderLanguage.METAL_LIBRARY -> JSShaderLanguage.METAL_LIBRARY
                }
                forceGLES2Context = this@Config.forceGLES2Context
                assertNativeWindowIsValid = this@Config.assertNativeWindowIsValid
                gpuContextPriority = when (this@Config.gpuContextPriority) {
                    GpuContextPriority.DEFAULT -> JSGpuContextPriority.DEFAULT
                    GpuContextPriority.LOW -> JSGpuContextPriority.LOW
                    GpuContextPriority.MEDIUM -> JSGpuContextPriority.MEDIUM
                    GpuContextPriority.HIGH -> JSGpuContextPriority.HIGH
                    GpuContextPriority.REALTIME -> JSGpuContextPriority.REALTIME
                }
                sharedUboInitialSizeInBytes = this@Config.sharedUboInitialSizeInBytes.toDouble()
                enableMultipleDirectionalLights = this@Config.enableMultipleDirectionalLights
            }
    }

    actual class Builder {
        private var config: Config? = null

        actual fun backend(backend: Backend): Builder = this
        actual fun sharedContext(sharedContext: Any): Builder = this
        actual fun config(config: Config): Builder = apply { this.config = config }
        actual fun featureLevel(featureLevel: FeatureLevel): Builder = this
        actual fun paused(paused: Boolean): Builder = this
        actual fun feature(name: String, value: Boolean): Builder = this
        // TODO(js): Engine.Builder.colorGrading is not registered in jsbindings.cpp (filament.js has no Engine.Builder).
        actual fun colorGrading(colorGrading: ColorGrading.Builder): Builder = this
        actual fun build(): Engine = create(config)
    }

    actual companion object {
        /**
         * `Engine.create` builds the WebGL context with `alpha: false` by default, which makes
         * every frame opaque no matter what the View blend mode is. Always request an alpha
         * channel — an opaque render still writes alpha 1, so this only adds the option of
         * transparency.
         */
        private fun glOptions(): JSEngineCreateOptions =
            emptyJsObject().unsafeCast<JSEngineCreateOptions>().apply { alpha = true }

        actual fun create(): Engine = create(null)

        private fun create(config: Config?): Engine {
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
            return Engine(createJs(canvas, config), canvas, ownsCanvas = true)
        }

        actual fun create(backend: Backend): Engine {
            return create()
        }

        actual fun create(sharedContext: Any): Engine {
            if (sharedContext is HTMLCanvasElement) {
                return Engine(createJs(sharedContext, null), sharedContext)
            }
            return create()
        }

        private fun createJs(canvas: HTMLCanvasElement, config: Config?): JSEngine =
            if (config == null) JSEngine.create(canvas, glOptions())
            else JSEngine.create(canvas, glOptions(), config.toJs())

        actual fun getSteadyClockTimeNano(): Long {
            // Filament.js returns a BigInt here. Coerce it to a JS number *before* it
            // crosses the interop boundary: wasmJs's adapter throws marshaling a BigInt
            // into the externally-typed Double (js tolerates it, wasm doesn't).
            return steadyClockTimeNanoJs().toLong()
        }
    }
}

// Reads `Filament.Engine.getSteadyClockTimeNano()` and coerces its BigInt result to a
// number in JS, so neither target marshals a BigInt as a Double.
private fun steadyClockTimeNanoJs(): Double = js("Number(Filament.Engine.getSteadyClockTimeNano())")

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

private fun fromJsBackend(b: io.github.erkko68.filament.web.Backend): Engine.Backend = when (b) {
    io.github.erkko68.filament.web.Backend.DEFAULT -> Engine.Backend.DEFAULT
    io.github.erkko68.filament.web.Backend.OPENGL  -> Engine.Backend.OPENGL
    io.github.erkko68.filament.web.Backend.VULKAN  -> Engine.Backend.VULKAN
    io.github.erkko68.filament.web.Backend.METAL   -> Engine.Backend.METAL
    io.github.erkko68.filament.web.Backend.WEBGPU  -> Engine.Backend.WEBGPU
    io.github.erkko68.filament.web.Backend.NOOP    -> Engine.Backend.NOOP
    else -> error("unreachable")
}

private fun fromJsFeatureLevel(fl: io.github.erkko68.filament.web.FeatureLevel): Engine.FeatureLevel = when (fl) {
    io.github.erkko68.filament.web.FeatureLevel.FEATURE_LEVEL_1 -> Engine.FeatureLevel.FEATURE_LEVEL_1
    io.github.erkko68.filament.web.FeatureLevel.FEATURE_LEVEL_2 -> Engine.FeatureLevel.FEATURE_LEVEL_2
    io.github.erkko68.filament.web.FeatureLevel.FEATURE_LEVEL_3 -> Engine.FeatureLevel.FEATURE_LEVEL_3
    else -> error("unreachable")
}

private fun toJsFeatureLevel(fl: Engine.FeatureLevel): io.github.erkko68.filament.web.FeatureLevel = when (fl) {
    Engine.FeatureLevel.FEATURE_LEVEL_0,
    Engine.FeatureLevel.FEATURE_LEVEL_1 -> io.github.erkko68.filament.web.FeatureLevel.FEATURE_LEVEL_1
    Engine.FeatureLevel.FEATURE_LEVEL_2 -> io.github.erkko68.filament.web.FeatureLevel.FEATURE_LEVEL_2
    Engine.FeatureLevel.FEATURE_LEVEL_3 -> io.github.erkko68.filament.web.FeatureLevel.FEATURE_LEVEL_3
}
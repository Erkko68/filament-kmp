package io.github.erkko68.filament


import io.github.erkko68.filament.web.interop.emptyJsObject
import io.github.erkko68.filament.web.interop.loseWebGlContext
import io.github.erkko68.filament.web.Engine as JSEngine
import io.github.erkko68.filament.web.EntityManager as JSEntityManager
import io.github.erkko68.filament.web.Entity as JSEntity
import io.github.erkko68.filament.web.EngineCreateOptions
import io.github.erkko68.filament.web.interop.jsSetBoolean
import org.w3c.dom.HTMLCanvasElement

actual class Engine @InternalFilamentApi constructor(
    internal val jsEngine: JSEngine,
    internal val jsCanvas: HTMLCanvasElement? = null,
    // Only the hidden canvas we allocated ourselves is ours to tear down; a caller's
    // shared canvas outlives the engine and may back another one later.
    private val ownsCanvas: Boolean = false,
) : AutoCloseable {
    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "returns true unconditionally — filament.js binds no engine-level validity check, only the isValidX family for resources.")
    actual val isValid: Boolean get() {
        return true
    }

    actual override fun close() = destroy()


    actual fun destroy() {
        JSEngine.destroy(jsEngine)
        // The canvas and its GL context outlive the engine, and browsers cap how
        // many contexts are live at once, so release both here.
        if (ownsCanvas) jsCanvas?.let { canvas ->
            loseWebGlContext(canvas)
            canvas.remove()
        }
    }

    actual val backend: Backend get() = fromJsBackend(jsEngine.getBackend())

    actual val supportedFeatureLevel: FeatureLevel get() = fromJsFeatureLevel(jsEngine.getSupportedFeatureLevel())

    actual var activeFeatureLevel: FeatureLevel
        get() = fromJsFeatureLevel(jsEngine.getActiveFeatureLevel())
        set(value) { jsEngine.setActiveFeatureLevel(toJsFeatureLevel(value)) }

    actual var isAutomaticInstancingEnabled: Boolean
        get() = jsEngine.isAutomaticInstancingEnabled()
        set(value) { jsEngine.setAutomaticInstancingEnabled(value) }

    actual val config: Config get() {
        // The JS binding's getConfig() returns a JS object with the same shape
        // as Engine.Config. Map it back into our actual class.
        val jsCfg = jsEngine.getConfig()
        return Config().apply {
            jsCfg.commandBufferSizeMB?.let { commandBufferSizeMB = it.toLong() }
            jsCfg.perRenderPassArenaSizeMB?.let { perRenderPassArenaSizeMB = it.toLong() }
            jsCfg.driverHandleArenaSizeMB?.let { driverHandleArenaSizeMB = it.toLong() }
            jsCfg.minCommandBufferSizeMB?.let { minCommandBufferSizeMB = it.toLong() }
            jsCfg.perFrameCommandsSizeMB?.let { perFrameCommandsSizeMB = it.toLong() }
            jsCfg.jobSystemThreadCount?.let { jobSystemThreadCount = it.toLong() }
            jsCfg.disableParallelShaderCompile?.let { disableParallelShaderCompile = it }
            jsCfg.stereoscopicType?.let { stereoscopicType = fromJsStereoscopicType(it) }
            jsCfg.stereoscopicEyeCount?.let { stereoscopicEyeCount = it.toLong() }
            jsCfg.resourceAllocatorCacheSizeMB?.let { resourceAllocatorCacheSizeMB = it.toLong() }
            jsCfg.resourceAllocatorCacheMaxAge?.let { resourceAllocatorCacheMaxAge = it.toLong() }
            jsCfg.disableHandleUseAfterFreeCheck?.let { disableHandleUseAfterFreeCheck = it }
            jsCfg.preferredShaderLanguage?.let { preferredShaderLanguage = fromJsShaderLanguage(it) }
            jsCfg.gpuContextPriority?.let { gpuContextPriority = fromJsGpuContextPriority(it) }
            jsCfg.sharedUboInitialSizeInBytes?.let { sharedUboInitialSizeInBytes = it.toLong() }
            jsCfg.forceGLES2Context?.let { forceGLES2Context = it }
            jsCfg.assertNativeWindowIsValid?.let { assertNativeWindowIsValid = it }
        }
    }

    actual val maxStereoscopicEyes: Long get() {
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
    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "throws UnsupportedOperationException — Stream cannot be constructed on web, so there is never a stream to validate.")
    actual fun isValidStream(stream: Stream): Boolean = jsUnsupported("Engine.isValidStream")
    actual fun isValidSwapChain(swapChain: SwapChain): Boolean = jsEngine.isValidSwapChain(swapChain.jsSwapChain)

    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "ignores the surface — PlatformWebGL::createSwapChain discards nativeWindow, so the swap chain always targets the canvas the engine's GL context was created with.")
    actual fun createSwapChain(surface: NativeSurface): SwapChain {
        return SwapChain(jsEngine.createSwapChain())
    }

    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "ignores the surface and the flags — PlatformWebGL::createSwapChain discards nativeWindow, so the swap chain always targets the canvas the engine's GL context was created with.")
    actual fun createSwapChain(
        surface: NativeSurface,
        flags: Long
    ): SwapChain {
        return createSwapChain(surface)
    }

    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "ignores width/height/flags — PlatformWebGL has no headless swap chain (it returns nullptr); the swap chain always targets the engine's canvas, at that canvas's size.")
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
        return Camera(jsEngine.createCamera(EntityManager.jsEntityOf(entity)))
    }

    actual fun createCamera(entity: Entity): Camera {
        return Camera(jsEngine.createCamera(EntityManager.jsEntityOf(entity)))
    }

    actual fun getCameraComponent(entity: Entity): Camera? {
        return Camera(jsEngine.getCameraComponent(EntityManager.jsEntityOf(entity)))
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

    actual val transformManager: TransformManager get() {
        return TransformManager(jsEngine.getTransformManager())
    }

    actual val lightManager: LightManager get() {
        return LightManager(jsEngine.getLightManager())
    }

    actual val renderableManager: RenderableManager get() {
        return RenderableManager(jsEngine.getRenderableManager())
    }

    actual val entityManager: EntityManager get() {
        return EntityManager(JSEntityManager.get())
    }

    actual fun flushAndWait() {
        jsEngine.flushAndWait()
    }

    actual fun flushAndWait(timeout: Long): Boolean {
        // The web build is single-threaded, so the wait always completes — no timeout to honour.
        jsEngine.flushAndWait()
        return true
    }

    actual fun flush() {
        jsEngine.flush()
    }

    actual val hasUnrecoverableFailure: Boolean get() = jsEngine.hasUnrecoverableFailure()

    // Engine::setPaused panics on single-threaded builds ("Pause is meant for multi-threaded
    // platforms"), so it stays unbound on web — track locally for the getter/setter round-trip.
    private var _paused: Boolean = false

    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "state is only tracked locally — pausing requires a multi-threaded engine, which the web build is not.")
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

    actual fun hasFeatureFlag(name: String): Boolean = jsEngine.hasFeatureFlag(name)

    actual fun setFeatureFlag(name: String, value: Boolean): Boolean = jsEngine.setFeatureFlag(name, value)

    // Undefined (→ null) when no such flag exists; the other targets return false there too.
    actual fun getFeatureFlag(name: String): Boolean = jsEngine.getFeatureFlag(name) ?: false

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
    // Defaults mirror filament's Engine::Config (Engine.h), same as the other platforms.
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

        actual enum class ShaderLanguage { DEFAULT, MSL, METAL_LIBRARY }
    }

    // filament.js has no Engine::Builder — `Filament.Engine.create(canvas, options, config)`
    // is the only entry point, and it carries the Builder-only settings on its options object.
    // featureLevel is applied right after construction (clamped, as Builder does). `paused`
    // stays a no-op: it pauses the render thread, and the web build is single-threaded.
    actual class Builder {
        private var backend: Backend? = null
        private var canvas: HTMLCanvasElement? = null
        private var config: Config? = null
        private var featureLevel: FeatureLevel? = null
        private val features = mutableMapOf<String, Boolean>()
        private var colorGrading: ColorGrading.Builder? = null

        actual fun backend(backend: Backend): Builder { this.backend = backend; return this }
        actual fun sharedContext(sharedContext: Any): Builder {
            canvas = sharedContext as? HTMLCanvasElement
            return this
        }
        actual fun config(config: Config): Builder { this.config = config; return this }
        actual fun featureLevel(featureLevel: FeatureLevel): Builder { this.featureLevel = featureLevel; return this }
        actual fun paused(paused: Boolean): Builder = this
        actual fun feature(name: String, value: Boolean): Builder { features[name] = value; return this }
        actual fun colorGrading(colorGrading: ColorGrading.Builder): Builder { this.colorGrading = colorGrading; return this }

        actual fun build(): Engine {
            val engine = create(
                canvas ?: offscreenCanvas(), backend, config, features, colorGrading,
                ownsCanvas = canvas == null,
            )
            featureLevel?.let {
                // Builder::featureLevel takes the min of the request and what the backend
                // supports; setActiveFeatureLevel throws instead, so clamp first.
                engine.activeFeatureLevel = minOf(it, engine.supportedFeatureLevel)
            }
            return engine
        }
    }

    actual companion object {
        actual fun create(): Engine = create(offscreenCanvas(), null, null, ownsCanvas = true)

        actual fun create(backend: Backend): Engine =
            create(offscreenCanvas(), backend, null, ownsCanvas = true)

        actual fun create(sharedContext: Any): Engine {
            val shared = sharedContext as? HTMLCanvasElement
            return create(shared ?: offscreenCanvas(), null, null, ownsCanvas = shared == null)
        }

        /**
         * `Engine.create` builds the WebGL context with `alpha: false` by default, which makes
         * every frame opaque no matter what the View blend mode is. Always request an alpha
         * channel — an opaque render still writes alpha 1, so this only adds the option of
         * transparency.
         */
        private fun create(
            canvas: HTMLCanvasElement,
            backend: Backend?,
            config: Config?,
            features: Map<String, Boolean> = emptyMap(),
            colorGrading: ColorGrading.Builder? = null,
            ownsCanvas: Boolean = false,
        ): Engine {
            val options = emptyJsObject().unsafeCast<EngineCreateOptions>().apply { alpha = true }
            backend?.let { options.backend = toJsBackend(it) }
            colorGrading?.let { options.colorGrading = it.jsBuilder }
            if (features.isNotEmpty()) {
                options.features = emptyJsObject().also { obj ->
                    features.forEach { (name, value) -> jsSetBoolean(obj, name, value) }
                }
            }
            // No config → let the JS binding keep its own createDefaultConfig() values.
            val jsEngine = if (config == null) JSEngine.create(canvas, options)
                           else JSEngine.create(canvas, options, config.toJs())
            return Engine(jsEngine, canvas, ownsCanvas)
        }

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

// Filament needs a WebGL-backed canvas. Without a shared one, park a hidden 1x1 canvas
// on <body> until a consumer (e.g. FilamentView via HtmlElementView) adopts it into the
// Compose-managed DOM subtree.
private fun offscreenCanvas(): HTMLCanvasElement {
    val doc = kotlinx.browser.document
    val canvas = doc.createElement("canvas") as HTMLCanvasElement
    canvas.width = 1
    canvas.height = 1
    canvas.style.position = "absolute"
    canvas.style.left = "-9999px"
    canvas.style.top = "0"
    doc.body?.appendChild(canvas)
    return canvas
}

// extensions.js does `Object.assign(createDefaultConfig(), config)`, so a plain object
// carrying only the fields we model is enough.
private fun Engine.Config.toJs(): io.github.erkko68.filament.web.Engine_Config =
    emptyJsObject().unsafeCast<io.github.erkko68.filament.web.Engine_Config>().apply {
        commandBufferSizeMB = this@toJs.commandBufferSizeMB.toDouble()
        perRenderPassArenaSizeMB = this@toJs.perRenderPassArenaSizeMB.toDouble()
        driverHandleArenaSizeMB = this@toJs.driverHandleArenaSizeMB.toDouble()
        minCommandBufferSizeMB = this@toJs.minCommandBufferSizeMB.toDouble()
        perFrameCommandsSizeMB = this@toJs.perFrameCommandsSizeMB.toDouble()
        jobSystemThreadCount = this@toJs.jobSystemThreadCount.toDouble()
        disableParallelShaderCompile = this@toJs.disableParallelShaderCompile
        stereoscopicType = toJsStereoscopicType(this@toJs.stereoscopicType)
        stereoscopicEyeCount = this@toJs.stereoscopicEyeCount.toDouble()
        resourceAllocatorCacheSizeMB = this@toJs.resourceAllocatorCacheSizeMB.toDouble()
        resourceAllocatorCacheMaxAge = this@toJs.resourceAllocatorCacheMaxAge.toDouble()
        disableHandleUseAfterFreeCheck = this@toJs.disableHandleUseAfterFreeCheck
        preferredShaderLanguage = toJsShaderLanguage(this@toJs.preferredShaderLanguage)
        forceGLES2Context = this@toJs.forceGLES2Context
        assertNativeWindowIsValid = this@toJs.assertNativeWindowIsValid
        gpuContextPriority = toJsGpuContextPriority(this@toJs.gpuContextPriority)
        sharedUboInitialSizeInBytes = this@toJs.sharedUboInitialSizeInBytes.toDouble()
    }

// Bridges between the common enums (mirroring the Android API) and the externals in
// io.github.erkko68.filament.web, which are named differently.
//
// FEATURE_LEVEL_0 (ES2-class hardware) has no external: jsenums.cpp only registers
// levels 1-3, since the WebGL build targets GLES3/WebGL2. It maps to FEATURE_LEVEL_1.

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

private fun fromJsStereoscopicType(t: io.github.erkko68.filament.web.StereoscopicType): Engine.StereoscopicType = when (t) {
    io.github.erkko68.filament.web.StereoscopicType.NONE -> Engine.StereoscopicType.NONE
    io.github.erkko68.filament.web.StereoscopicType.INSTANCED -> Engine.StereoscopicType.INSTANCED
    io.github.erkko68.filament.web.StereoscopicType.MULTIVIEW -> Engine.StereoscopicType.MULTIVIEW
    else -> error("unreachable")
}

private fun fromJsGpuContextPriority(p: io.github.erkko68.filament.web.GpuContextPriority): Engine.GpuContextPriority = when (p) {
    io.github.erkko68.filament.web.GpuContextPriority.DEFAULT -> Engine.GpuContextPriority.DEFAULT
    io.github.erkko68.filament.web.GpuContextPriority.LOW -> Engine.GpuContextPriority.LOW
    io.github.erkko68.filament.web.GpuContextPriority.MEDIUM -> Engine.GpuContextPriority.MEDIUM
    io.github.erkko68.filament.web.GpuContextPriority.HIGH -> Engine.GpuContextPriority.HIGH
    io.github.erkko68.filament.web.GpuContextPriority.REALTIME -> Engine.GpuContextPriority.REALTIME
    else -> error("unreachable")
}

private fun fromJsShaderLanguage(sl: io.github.erkko68.filament.web.ShaderLanguage): Engine.Config.ShaderLanguage = when (sl) {
    io.github.erkko68.filament.web.ShaderLanguage.DEFAULT -> Engine.Config.ShaderLanguage.DEFAULT
    io.github.erkko68.filament.web.ShaderLanguage.MSL -> Engine.Config.ShaderLanguage.MSL
    io.github.erkko68.filament.web.ShaderLanguage.METAL_LIBRARY -> Engine.Config.ShaderLanguage.METAL_LIBRARY
    else -> error("unreachable")
}

private fun toJsBackend(b: Engine.Backend): io.github.erkko68.filament.web.Backend = when (b) {
    Engine.Backend.DEFAULT -> io.github.erkko68.filament.web.Backend.DEFAULT
    Engine.Backend.OPENGL  -> io.github.erkko68.filament.web.Backend.OPENGL
    Engine.Backend.VULKAN  -> io.github.erkko68.filament.web.Backend.VULKAN
    Engine.Backend.METAL   -> io.github.erkko68.filament.web.Backend.METAL
    Engine.Backend.WEBGPU  -> io.github.erkko68.filament.web.Backend.WEBGPU
    Engine.Backend.NOOP    -> io.github.erkko68.filament.web.Backend.NOOP
}

private fun toJsStereoscopicType(t: Engine.StereoscopicType): io.github.erkko68.filament.web.StereoscopicType = when (t) {
    Engine.StereoscopicType.NONE -> io.github.erkko68.filament.web.StereoscopicType.NONE
    Engine.StereoscopicType.INSTANCED -> io.github.erkko68.filament.web.StereoscopicType.INSTANCED
    Engine.StereoscopicType.MULTIVIEW -> io.github.erkko68.filament.web.StereoscopicType.MULTIVIEW
}

private fun toJsGpuContextPriority(p: Engine.GpuContextPriority): io.github.erkko68.filament.web.GpuContextPriority = when (p) {
    Engine.GpuContextPriority.DEFAULT -> io.github.erkko68.filament.web.GpuContextPriority.DEFAULT
    Engine.GpuContextPriority.LOW -> io.github.erkko68.filament.web.GpuContextPriority.LOW
    Engine.GpuContextPriority.MEDIUM -> io.github.erkko68.filament.web.GpuContextPriority.MEDIUM
    Engine.GpuContextPriority.HIGH -> io.github.erkko68.filament.web.GpuContextPriority.HIGH
    Engine.GpuContextPriority.REALTIME -> io.github.erkko68.filament.web.GpuContextPriority.REALTIME
}

private fun toJsShaderLanguage(sl: Engine.Config.ShaderLanguage): io.github.erkko68.filament.web.ShaderLanguage = when (sl) {
    Engine.Config.ShaderLanguage.DEFAULT -> io.github.erkko68.filament.web.ShaderLanguage.DEFAULT
    Engine.Config.ShaderLanguage.MSL -> io.github.erkko68.filament.web.ShaderLanguage.MSL
    Engine.Config.ShaderLanguage.METAL_LIBRARY -> io.github.erkko68.filament.web.ShaderLanguage.METAL_LIBRARY
}

package dev.filament.kmp

import com.google.android.filament.Material as AndroidMaterial
import com.google.android.filament.Engine as AndroidFilamentEngine
import com.google.android.filament.SwapChain as AndroidSwapChain

actual class Engine private constructor(
    internal var androidEngine: AndroidFilamentEngine?,
) {
    private fun engine(): AndroidFilamentEngine = requireNotNull(androidEngine) { "Engine is closed." }

    actual fun isValid(): Boolean = androidEngine != null

    actual fun createFence(): Fence = Fence(engine().createFence())

    actual fun createSwapChain(surface: Any): SwapChain = createSwapChain(surface, SwapChainFlags.CONFIG_DEFAULT)

    actual fun createSwapChainFromNativeSurface(nativeSurface: Long, flags: Long): SwapChain {
        val eng = engine()
        val nativeSurfaceClass = Class.forName("com.google.android.filament.NativeSurface")
        val ctor = nativeSurfaceClass.getDeclaredConstructor(Long::class.javaPrimitiveType)
        val nativeSurfaceObj = ctor.newInstance(nativeSurface)
        val method = eng.javaClass.methods.firstOrNull {
            it.name == "createSwapChainFromNativeSurface" && it.parameterTypes.size == 2
        } ?: throw IllegalStateException("createSwapChainFromNativeSurface is unavailable in this Filament Android runtime.")
        val swapChain = method.invoke(eng, nativeSurfaceObj, flags) as AndroidSwapChain
        return SwapChain(swapChain)
    }

    actual fun destroy() {
        engine().destroy()
        androidEngine = null
    }

    actual fun destroyEntity(@Entity entity: Int) {
        engine().destroyEntity(entity)
    }

    actual fun destroyFence(fence: Fence) {
        val handle = fence.androidFence ?: return
        engine().destroyFence(handle)
        fence.clearNativeObject()
    }

    actual fun enableAccurateTranslations() {
        engine().enableAccurateTranslations()
    }

    actual fun flush() {
        engine().flush()
    }

    actual fun flushAndWait() {
        engine().flushAndWait()
    }

    actual fun flushAndWait(timeout: Long) {
        engine().flushAndWait(timeout)
    }

    actual fun getActiveFeatureLevel(): Int = engine().activeFeatureLevel.ordinal

    actual fun getBackend(): Int = engine().backend.ordinal

    actual fun getConfig(): EngineConfig {
        val config = engine().config
        val stereoscopicType = when (config.stereoscopicType.name) {
            "NONE" -> EngineStereoscopicType.NONE
            "INSTANCED" -> EngineStereoscopicType.INSTANCED
            "MULTIVIEW" -> EngineStereoscopicType.MULTIVIEW
            else -> EngineStereoscopicType.NONE
        }
        return EngineConfig(stereoscopicType, config.stereoscopicEyeCount.toUByte())
    }

    actual fun getEntityManager(): EntityManager = EntityManager(engine().entityManager)

    actual fun getFeatureFlag(flag: Int): Boolean = engine().getFeatureFlag(flag.toString())

    actual fun getMaxStereoscopicEyes(): Int = engine().maxStereoscopicEyes.toInt()

    actual fun getNativeJobSystem(): Long = engine().nativeJobSystem

    actual fun getNativeObject(): Long = engine().nativeObject

    actual fun getSteadyClockTimeNano(): Long = AndroidFilamentEngine.getSteadyClockTimeNano()

    actual fun getSupportedFeatureLevel(): Int = engine().supportedFeatureLevel.ordinal

    actual fun hasFeatureFlag(flag: Int): Boolean = engine().hasFeatureFlag(flag.toString())

    actual fun isAutomaticInstancingEnabled(): Boolean = engine().isAutomaticInstancingEnabled

    actual fun isPaused(): Boolean = engine().isPaused

    actual fun isValidColorGrading(colorGrading: ColorGrading): Boolean {
        val handle = colorGrading.androidColorGrading ?: return false
        return engine().isValidColorGrading(handle)
    }

    actual fun isValidExpensiveMaterialInstance(materialInstance: MaterialInstance): Boolean {
        val handle = materialInstance.androidMaterialInstance ?: return false
        return engine().isValidExpensiveMaterialInstance(handle)
    }

    actual fun isValidFence(fence: Fence): Boolean {
        val handle = fence.androidFence ?: return false
        return engine().isValidFence(handle)
    }

    actual fun isValidIndexBuffer(indexBuffer: IndexBuffer): Boolean {
        val handle = indexBuffer.androidIndexBuffer ?: return false
        return engine().isValidIndexBuffer(handle)
    }

    actual fun isValidIndirectLight(indirectLight: IndirectLight): Boolean {
        val handle = indirectLight.androidIndirectLight ?: return false
        return engine().isValidIndirectLight(handle)
    }

    actual fun isValidMaterial(material: Material): Boolean {
        val handle = material.androidMaterial ?: return false
        return engine().isValidMaterial(handle)
    }

    actual fun isValidMaterialInstance(materialInstance: MaterialInstance, includingDefaultInstance: Boolean): Boolean {
        val handle = materialInstance.androidMaterialInstance ?: return false
        val material: AndroidMaterial = handle.material
        return engine().isValidMaterialInstance(material, handle)
    }

    actual fun isValidMorphTargetBuffer(morphTargetBuffer: MorphTargetBuffer): Boolean {
        val handle = morphTargetBuffer.androidMorphTargetBuffer ?: return false
        return engine().isValidMorphTargetBuffer(handle)
    }

    actual fun isValidRenderTarget(renderTarget: RenderTarget): Boolean {
        val handle = renderTarget.androidRenderTarget ?: return false
        return engine().isValidRenderTarget(handle)
    }

    actual fun isValidRenderer(renderer: Renderer): Boolean {
        val handle = renderer.androidRenderer ?: return false
        return engine().isValidRenderer(handle)
    }

    actual fun isValidScene(scene: Scene): Boolean {
        val handle = scene.androidScene ?: return false
        return engine().isValidScene(handle)
    }

    actual fun isValidSkinningBuffer(skinningBuffer: SkinningBuffer): Boolean {
        val handle = skinningBuffer.androidSkinningBuffer ?: return false
        return engine().isValidSkinningBuffer(handle)
    }

    actual fun isValidSkybox(skybox: Skybox): Boolean {
        val handle = skybox.androidSkybox ?: return false
        return engine().isValidSkybox(handle)
    }

    actual fun isValidStream(stream: Stream): Boolean {
        val handle = stream.androidStream ?: return false
        return engine().isValidStream(handle)
    }

    actual fun isValidSwapChain(swapChain: SwapChain): Boolean {
        val handle = swapChain.androidSwapChain ?: return false
        return engine().isValidSwapChain(handle)
    }

    actual fun isValidTexture(texture: Texture): Boolean {
        val handle = texture.androidTexture ?: return false
        return engine().isValidTexture(handle)
    }

    actual fun isValidVertexBuffer(vertexBuffer: VertexBuffer): Boolean {
        val handle = vertexBuffer.androidVertexBuffer ?: return false
        return engine().isValidVertexBuffer(handle)
    }

    actual fun isValidView(view: View): Boolean {
        val handle = view.androidView ?: return false
        return engine().isValidView(handle)
    }

    actual fun setActiveFeatureLevel(featureLevel: Int) {
        val values = AndroidFilamentEngine.FeatureLevel.values()
        val clamped = featureLevel.coerceIn(0, values.lastIndex)
        engine().setActiveFeatureLevel(values[clamped])
    }

    actual fun setAutomaticInstancingEnabled(enabled: Boolean) {
        engine().setAutomaticInstancingEnabled(enabled)
    }

    actual fun setFeatureFlag(flag: Int, enabled: Boolean) {
        engine().setFeatureFlag(flag.toString(), enabled)
    }

    actual fun setPaused(paused: Boolean) {
        engine().setPaused(paused)
    }

    actual fun unprotected() {
        engine().unprotected()
    }

    actual fun createRenderer(): Renderer {
        val engine = requireNotNull(androidEngine) { "Engine is closed." }
        return Renderer(engine.createRenderer(), this)
    }

    actual fun destroyRenderer(renderer: Renderer) {
        val engine = requireNotNull(androidEngine) { "Engine is closed." }
        val handle = renderer.androidRenderer ?: return
        engine.destroyRenderer(handle)
        renderer.invalidate()
    }

    actual fun createScene(): Scene {
        val engine = requireNotNull(androidEngine) { "Engine is closed." }
        return Scene(engine.createScene())
    }

    actual fun destroyScene(scene: Scene) {
        val engine = requireNotNull(androidEngine) { "Engine is closed." }
        val handle = scene.androidScene ?: return
        engine.destroyScene(handle)
        scene.invalidate()
    }

    actual fun createView(): View {
        val engine = requireNotNull(androidEngine) { "Engine is closed." }
        return View(engine.createView())
    }

    actual fun destroyView(view: View) {
        val engine = requireNotNull(androidEngine) { "Engine is closed." }
        val handle = view.androidView ?: return
        engine.destroyView(handle)
        view.invalidate()
    }

    actual fun createCamera(@Entity entity: Int): Camera {
        val engine = requireNotNull(androidEngine) { "Engine is closed." }
        return Camera(engine.createCamera(entity))
    }

    actual fun getCameraComponent(@Entity entity: Int): Camera? {
        val engine = requireNotNull(androidEngine) { "Engine is closed." }
        val camera = engine.getCameraComponent(entity) ?: return null
        return Camera(camera)
    }

    actual fun destroyCameraComponent(@Entity entity: Int) {
        val engine = requireNotNull(androidEngine) { "Engine is closed." }
        engine.destroyCameraComponent(entity)
    }

    actual fun createSwapChain(surface: Any, flags: Long): SwapChain {
        val engine = requireNotNull(androidEngine) { "Engine is closed." }
        return SwapChain(engine.createSwapChain(surface, flags))
    }

    actual fun createSwapChain(width: Int, height: Int, flags: Long): SwapChain {
        val engine = requireNotNull(androidEngine) { "Engine is closed." }
        return SwapChain(engine.createSwapChain(width, height, flags))
    }

    actual fun destroySwapChain(swapChain: SwapChain) {
        val engine = requireNotNull(androidEngine) { "Engine is closed." }
        val handle = swapChain.androidSwapChain ?: return
        engine.destroySwapChain(handle)
        swapChain.invalidate()
    }

    actual fun destroyRenderTarget(target: RenderTarget) {
        val engine = requireNotNull(androidEngine) { "Engine is closed." }
        val handle = target.androidRenderTarget ?: return
        engine.destroyRenderTarget(handle)
        target.invalidate()
    }

    actual fun destroyTexture(texture: Texture) {
        val engine = requireNotNull(androidEngine) { "Engine is closed." }
        val handle = texture.androidTexture ?: return
        engine.destroyTexture(handle)
        texture.invalidate()
    }

    actual fun destroySkybox(skybox: Skybox) {
        val engine = requireNotNull(androidEngine) { "Engine is closed." }
        val handle = skybox.androidSkybox ?: return
        engine.destroySkybox(handle)
        skybox.invalidate()
    }

    actual fun destroyStream(stream: Stream) {
        val engine = requireNotNull(androidEngine) { "Engine is closed." }
        val handle = stream.androidStream ?: return
        engine.destroyStream(handle)
        stream.invalidate()
    }

    actual fun destroySkinningBuffer(skinningBuffer: SkinningBuffer) {
        val engine = requireNotNull(androidEngine) { "Engine is closed." }
        val handle = skinningBuffer.androidSkinningBuffer ?: return
        engine.destroySkinningBuffer(handle)
        skinningBuffer.invalidate()
    }

    actual fun destroyMorphTargetBuffer(morphTargetBuffer: MorphTargetBuffer) {
        val engine = requireNotNull(androidEngine) { "Engine is closed." }
        val handle = morphTargetBuffer.androidMorphTargetBuffer ?: return
        engine.destroyMorphTargetBuffer(handle)
        morphTargetBuffer.invalidate()
    }

    actual fun destroyIndirectLight(indirectLight: IndirectLight) {
        val engine = requireNotNull(androidEngine) { "Engine is closed." }
        val handle = indirectLight.androidIndirectLight ?: return
        engine.destroyIndirectLight(handle)
        indirectLight.invalidate()
    }

    actual fun destroyColorGrading(colorGrading: ColorGrading) {
        val engine = requireNotNull(androidEngine) { "Engine is closed." }
        val handle = colorGrading.androidColorGrading ?: return
        engine.destroyColorGrading(handle)
        colorGrading.invalidate()
    }


    actual fun destroyIndexBuffer(indexBuffer: IndexBuffer) {
        val engine = requireNotNull(androidEngine) { "Engine is closed." }
        val handle = indexBuffer.androidIndexBuffer ?: return
        engine.destroyIndexBuffer(handle)
        indexBuffer.invalidate()
    }

    actual fun destroyVertexBuffer(vertexBuffer: VertexBuffer) {
        val engine = requireNotNull(androidEngine) { "Engine is closed." }
        val handle = vertexBuffer.androidVertexBuffer ?: return
        engine.destroyVertexBuffer(handle)
        vertexBuffer.invalidate()
    }

    actual fun destroyMaterial(material: Material) {
        val engine = requireNotNull(androidEngine) { "Engine is closed." }
        val handle = material.androidMaterial ?: return
        engine.destroyMaterial(handle)
        material.invalidate()
    }

    actual fun destroyMaterialInstance(materialInstance: MaterialInstance) {
        val engine = requireNotNull(androidEngine) { "Engine is closed." }
        val handle = materialInstance.androidMaterialInstance ?: return
        engine.destroyMaterialInstance(handle)
        materialInstance.invalidate()
    }

    actual fun getRenderableManager(): RenderableManager {
        val engine = requireNotNull(androidEngine) { "Engine is closed." }
        return RenderableManager(engine.renderableManager)
    }

    actual fun getLightManager(): LightManager {
        val engine = requireNotNull(androidEngine) { "Engine is closed." }
        return LightManager(engine.lightManager)
    }

    actual fun getTransformManager(): TransformManager {
        val engine = requireNotNull(androidEngine) { "Engine is closed." }
        return TransformManager(engine.transformManager)
    }

    actual fun close() {
        engine().destroy()
        androidEngine = null
    }

    actual companion object {
        actual fun create(): Engine = Engine(AndroidFilamentEngine.create())

        actual fun create(config: EngineConfig): Engine {
            require(config == EngineConfig()) {
                "Android Engine.create currently supports default EngineConfig only."
            }
            return Engine(AndroidFilamentEngine.create())
        }
    }
}
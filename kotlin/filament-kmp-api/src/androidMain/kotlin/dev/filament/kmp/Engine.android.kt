package dev.filament.kmp

import com.google.android.filament.Engine as AndroidFilamentEngine

actual class Engine private constructor(
    internal var androidEngine: AndroidFilamentEngine?,
) {
    actual val isValid: Boolean
        get() = androidEngine != null

    actual fun createRenderer(): Renderer {
        val engine = requireNotNull(androidEngine) { "Engine is closed." }
        return Renderer(engine.createRenderer())
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
        androidEngine?.destroy()
        androidEngine = null
    }

    actual companion object {
        actual fun create(config: EngineConfig): Engine {
            require(config == EngineConfig()) {
                "Android Engine.create currently supports default EngineConfig only."
            }
            return Engine(AndroidFilamentEngine.create())
        }
    }
}
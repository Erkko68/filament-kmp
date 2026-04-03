package dev.filament.kmp

import cnames.structs.FilaEngine
import dev.filament.kmp.cinterop.FILA_ENGINE_STEREOSCOPIC_TYPE_INSTANCED
import dev.filament.kmp.cinterop.FILA_ENGINE_STEREOSCOPIC_TYPE_MULTIVIEW
import dev.filament.kmp.cinterop.FILA_ENGINE_STEREOSCOPIC_TYPE_NONE
import dev.filament.kmp.cinterop.FilaEngineConfig
import dev.filament.kmp.cinterop.FilaEngine_createWithConfig
import dev.filament.kmp.cinterop.FilaEngine_destroy
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CPointerVar
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value

@OptIn(ExperimentalForeignApi::class)
actual class Engine private constructor(
    private var nativeHandle: CPointer<FilaEngine>?,
) {
    actual fun isValid(): Boolean = nativeHandle != null

    actual fun createFence(): Fence {
        TODO("Not yet implemented")
    }

    actual fun createSwapChain(surface: Any): SwapChain {
        TODO("Not yet implemented")
    }

    actual fun createSwapChainFromNativeSurface(nativeSurface: Long, flags: Long): SwapChain {
        TODO("Not yet implemented")
    }

    actual fun destroy() {
        close()
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

    actual fun getConfig(): EngineConfig = EngineConfig()

    actual fun getEntityManager(): EntityManager = EntityManager.get()

    actual fun getFeatureFlag(flag: Int): Boolean = TODO("Not yet implemented")

    actual fun getMaxStereoscopicEyes(): Int = TODO("Not yet implemented")

    actual fun getNativeJobSystem(): Long = TODO("Not yet implemented")

    actual fun getNativeObject(): Long = nativeHandle?.rawValue?.toLong() ?: 0L

    actual fun getSteadyClockTimeNano(): Long = TODO("Not yet implemented")

    actual fun getSupportedFeatureLevel(): Int = TODO("Not yet implemented")

    actual fun hasFeatureFlag(flag: Int): Boolean = TODO("Not yet implemented")

    actual fun isAutomaticInstancingEnabled(): Boolean =
        TODO("Not yet implemented")

    actual fun isPaused(): Boolean = TODO("Not yet implemented")

    actual fun isValidColorGrading(colorGrading: ColorGrading): Boolean = true

    actual fun isValidExpensiveMaterialInstance(materialInstance: MaterialInstance): Boolean = true

    actual fun isValidFence(fence: Fence): Boolean = true

    actual fun isValidIndexBuffer(indexBuffer: IndexBuffer): Boolean = true

    actual fun isValidIndirectLight(indirectLight: IndirectLight): Boolean = true

    actual fun isValidMaterial(material: Material): Boolean = true

    actual fun isValidMaterialInstance(materialInstance: MaterialInstance, includingDefaultInstance: Boolean): Boolean = true

    actual fun isValidMorphTargetBuffer(morphTargetBuffer: MorphTargetBuffer): Boolean = true

    actual fun isValidRenderTarget(renderTarget: RenderTarget): Boolean = true

    actual fun isValidRenderer(renderer: Renderer): Boolean = true

    actual fun isValidScene(scene: Scene): Boolean = true

    actual fun isValidSkinningBuffer(skinningBuffer: SkinningBuffer): Boolean = true

    actual fun isValidSkybox(skybox: Skybox): Boolean = true

    actual fun isValidStream(stream: Stream): Boolean = true

    actual fun isValidSwapChain(swapChain: SwapChain): Boolean = true

    actual fun isValidTexture(texture: Texture): Boolean = true

    actual fun isValidVertexBuffer(vertexBuffer: VertexBuffer): Boolean = true

    actual fun isValidView(view: View): Boolean = true

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
        renderer.invalidate()
    }

    actual fun createScene(): Scene {
        TODO("Not yet implemented")
    }

    actual fun destroyScene(scene: Scene) {
        scene.invalidate()
    }

    actual fun createView(): View {
        TODO("Not yet implemented")
    }

    actual fun destroyView(view: View) {
        view.invalidate()
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
        swapChain.invalidate()
    }

    actual fun destroyRenderTarget(target: RenderTarget) {
        target.invalidate()
    }

    actual fun destroyTexture(texture: Texture) {
        texture.invalidate()
    }

    actual fun destroySkybox(skybox: Skybox) {
        skybox.invalidate()
    }

    actual fun destroyStream(stream: Stream) {
        stream.invalidate()
    }

    actual fun destroySkinningBuffer(skinningBuffer: SkinningBuffer) {
        skinningBuffer.invalidate()
    }

    actual fun destroyMorphTargetBuffer(morphTargetBuffer: MorphTargetBuffer) {
        morphTargetBuffer.invalidate()
    }

    actual fun destroyIndirectLight(indirectLight: IndirectLight) {
        indirectLight.invalidate()
    }

    actual fun destroyColorGrading(colorGrading: ColorGrading) {
        colorGrading.invalidate()
    }

    actual fun destroyIndexBuffer(indexBuffer: IndexBuffer) {
        indexBuffer.invalidate()
    }

    actual fun destroyVertexBuffer(vertexBuffer: VertexBuffer) {
        vertexBuffer.invalidate()
    }

    actual fun destroyMaterial(material: Material) {
        material.invalidate()
    }

    actual fun destroyMaterialInstance(materialInstance: MaterialInstance) {
        materialInstance.invalidate()
    }

    actual fun getRenderableManager(): RenderableManager {
        TODO("Not yet implemented")
    }

    actual fun getLightManager(): LightManager {
        TODO("Not yet implemented")
    }

    actual fun getTransformManager(): TransformManager {
        TODO("Not yet implemented")
    }

    actual fun close() {
        val handle = nativeHandle ?: return
        memScoped {
            val handleVar = alloc<CPointerVar<FilaEngine>>()
            handleVar.value = handle
            FilaEngine_destroy(handleVar.ptr)
        }
        nativeHandle = null
    }

    actual companion object {
        actual fun create(): Engine = create(EngineConfig())

        actual fun create(config: EngineConfig): Engine {
            val handle = memScoped {
                val nativeConfig = alloc<FilaEngineConfig>()
                nativeConfig.stereoscopicType = when (config.stereoscopicType) {
                    EngineStereoscopicType.NONE -> FILA_ENGINE_STEREOSCOPIC_TYPE_NONE
                    EngineStereoscopicType.INSTANCED -> FILA_ENGINE_STEREOSCOPIC_TYPE_INSTANCED
                    EngineStereoscopicType.MULTIVIEW -> FILA_ENGINE_STEREOSCOPIC_TYPE_MULTIVIEW
                }.convert()
                nativeConfig.stereoscopicEyeCount = config.stereoscopicEyeCount
                FilaEngine_createWithConfig(nativeConfig.ptr)
            }

            requireNotNull(handle) { "Failed to create Filament Engine." }
            return Engine(handle)
        }
    }
}

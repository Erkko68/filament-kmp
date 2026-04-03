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
    actual val isValid: Boolean
        get() = nativeHandle != null

    actual fun createRenderer(): Renderer {
        error("Native Renderer wiring is not implemented yet.")
    }

    actual fun destroyRenderer(renderer: Renderer) {
        renderer.invalidate()
    }

    actual fun createScene(): Scene {
        error("Native Scene wiring is not implemented yet.")
    }

    actual fun destroyScene(scene: Scene) {
        scene.invalidate()
    }

    actual fun createView(): View {
        error("Native View wiring is not implemented yet.")
    }

    actual fun destroyView(view: View) {
        view.invalidate()
    }

    actual fun createCamera(@Entity entity: Int): Camera {
        error("Native Camera wiring is not implemented yet.")
    }

    actual fun getCameraComponent(@Entity entity: Int): Camera? {
        error("Native Camera component wiring is not implemented yet.")
    }

    actual fun destroyCameraComponent(@Entity entity: Int) {
    }

    actual fun createSwapChain(surface: Any, flags: Long): SwapChain {
        error("Native SwapChain wiring is not implemented yet.")
    }

    actual fun createSwapChain(width: Int, height: Int, flags: Long): SwapChain {
        error("Native headless SwapChain wiring is not implemented yet.")
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
        error("Native RenderableManager wiring is not implemented yet.")
    }

    actual fun getLightManager(): LightManager {
        error("Native LightManager wiring is not implemented yet.")
    }

    actual fun getTransformManager(): TransformManager {
        error("Native TransformManager wiring is not implemented yet.")
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

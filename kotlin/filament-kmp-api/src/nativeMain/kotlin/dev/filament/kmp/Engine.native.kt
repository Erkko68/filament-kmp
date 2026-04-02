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

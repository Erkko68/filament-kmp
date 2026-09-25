package io.github.erkko68.filament.wasm

import org.khronos.webgl.Float64Array
import org.khronos.webgl.Uint8Array
import kotlin.js.Promise

/**
 * The Emscripten module instance of filament-kmp.wasm. Everything crossing the boundary is a
 * number: pointers are wasm32 addresses (`Int`), C `bool` is `Int`, 64-bit ints are `JsBigInt`
 * (see [toI64]). Heap views go stale when memory grows, so re-read them after any allocating call.
 */
external interface FilamentModule : JsAny {
    val HEAPU8: Uint8Array
    val HEAPF64: Float64Array
    val GL: EmscriptenGL

    fun _malloc(size: Int): Int
    fun _free(ptr: Int)

    // ponytail: hand-written spike subset; the header generator emits the full Fila* surface (design §6).
    fun _FilaEngineBuilder_create(): Int
    fun _FilaEngineBuilder_backend(builder: Int, backend: Int)
    fun _FilaEngineBuilder_build(builder: Int): Int
    fun _FilaEngineBuilder_destroy(builder: Int)
    fun _FilaEngine_destroy(engine: Int)
    fun _FilaEngine_createSwapChain(engine: Int, nativeWindow: Int, flags: JsBigInt): Int
    fun _FilaEngine_destroySwapChain(engine: Int, swapChain: Int): Int
    fun _FilaEngine_createRenderer(engine: Int): Int
    fun _FilaEngine_destroyRenderer(engine: Int, renderer: Int): Int
    fun _FilaEngine_createView(engine: Int): Int
    fun _FilaEngine_destroyView(engine: Int, view: Int): Int
    fun _FilaEngine_createScene(engine: Int): Int
    fun _FilaEngine_destroyScene(engine: Int, scene: Int): Int
    fun _FilaEngine_createCamera(engine: Int, entity: Int): Int
    fun _FilaEngine_destroyCameraComponent(engine: Int, entity: Int)
    fun _FilaEntityManager_get(): Int
    fun _FilaEntityManager_create(em: Int): Int
    fun _FilaEntityManager_destroy(em: Int, entity: Int)
    fun _FilaRenderer_beginFrame(renderer: Int, swapChain: Int, frameTimeNanos: JsBigInt): Int
    fun _FilaRenderer_render(renderer: Int, view: Int)
    fun _FilaRenderer_endFrame(renderer: Int)
    fun _FilaRenderer_setClearOptions(renderer: Int, options: Int)
    fun _FilaView_setScene(view: Int, scene: Int)
    fun _FilaView_setCamera(view: Int, camera: Int)
    fun _FilaView_setViewport(view: Int, left: Int, bottom: Int, width: Int, height: Int)
    fun _FilaView_setPostProcessingEnabled(view: Int, enabled: Int)
}

/** Emscripten's `GL` library object: the registry that maps WebGL contexts to handles. */
external interface EmscriptenGL : JsAny {
    fun registerContext(context: JsAny, attributes: JsAny): Int
    fun makeContextCurrent(handle: Int): Boolean
}

/** Global factory defined by filament-kmp.js (`-sMODULARIZE -sEXPORT_NAME=createFilamentModule`). */
private external fun createFilamentModule(): Promise<FilamentModule>

/** Resolves once filament-kmp.wasm is instantiated; later calls reuse the same instance. */
val filamentModule: Promise<FilamentModule> by lazy { createFilamentModule() }

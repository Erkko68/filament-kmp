package io.github.erkko68.filament.wasm

import org.khronos.webgl.Float32Array
import org.khronos.webgl.Float64Array
import org.khronos.webgl.Int16Array
import org.khronos.webgl.Int32Array
import org.khronos.webgl.Int8Array
import org.khronos.webgl.Uint16Array
import org.khronos.webgl.Uint32Array
import org.khronos.webgl.Uint8Array
import kotlin.js.Promise

/**
 * Runtime surface of an Emscripten module instance, shared by filament-kmp.wasm and filamat's
 * wasm. The Fila* functions come from the generated interfaces ([FilamentC], [GltfioC],
 * [FilamentUtilsC]). Everything crossing the boundary is a number: pointers are wasm32 addresses
 * (`Int`), C `bool` is `Int`, 64-bit ints are `JsBigInt` (see [toI64]). Heap views go stale when
 * memory grows, so re-read them after any allocating call.
 */
external interface FilamentModule : JsAny {
    val HEAP8: Int8Array
    val HEAPU8: Uint8Array
    val HEAP16: Int16Array
    val HEAPU16: Uint16Array
    val HEAP32: Int32Array
    val HEAPU32: Uint32Array
    val HEAPF32: Float32Array
    val HEAPF64: Float64Array
    val GL: EmscriptenGL

    fun _malloc(size: Int): Int
    fun _free(ptr: Int)
    fun addFunction(function: JsAny, signature: String): Int
    fun removeFunction(index: Int)
    fun lengthBytesUTF8(string: String): Int
    fun stringToUTF8(string: String, ptr: Int, maxBytes: Int)
    fun UTF8ToString(ptr: Int): String
}

/** filament-kmp.wasm: filament + filament-utils + gltfio in one instance (they share Engine pointers). */
external interface FilamentWasm : FilamentC, FilamentUtilsC, GltfioC

/** Emscripten's `GL` library object: the registry that maps WebGL contexts to handles. */
external interface EmscriptenGL : JsAny {
    fun registerContext(context: JsAny, attributes: JsAny): Int
    fun makeContextCurrent(handle: Int): Boolean
}

/** The loaded module. Only valid once [loadFilament] has resolved. */
lateinit var fila: FilamentWasm
    private set

private val loading: Promise<FilamentWasm> by lazy {
    createFilamentModule().then { module -> fila = module; module }
}

/** Instantiates filament-kmp.wasm (once) and sets [fila]. */
fun loadFilament(): Promise<FilamentWasm> = loading

/** Global factory defined by filament-kmp.js (`-sMODULARIZE -sEXPORT_NAME=createFilamentModule`). */
private external fun createFilamentModule(): Promise<FilamentWasm>

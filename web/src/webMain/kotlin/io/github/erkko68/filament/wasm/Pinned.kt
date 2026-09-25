package io.github.erkko68.filament.wasm

import org.khronos.webgl.get
import org.khronos.webgl.set

// Stand-ins for cinterop's usePinned/pin on the wasm heap, so actuals port from nativeMain
// unchanged: the array is copied in, [block] gets its heap address, and the heap contents are
// copied back (C may have written to it) before the copy is freed.

inline fun <R> FloatArray.usePinned(block: (ptr: Int) -> R): R = fila.heapScoped {
    val ptr = floats(this@usePinned)
    block(ptr).also { fila.readFloats(ptr, size, this@usePinned) }
}

inline fun <R> DoubleArray.usePinned(block: (ptr: Int) -> R): R = fila.heapScoped {
    val ptr = doubles(this@usePinned)
    block(ptr).also { fila.readDoubles(ptr, size, this@usePinned) }
}

inline fun <R> IntArray.usePinned(block: (ptr: Int) -> R): R = fila.heapScoped {
    val ptr = ints(this@usePinned)
    block(ptr).also { fila.readInts(ptr, size, this@usePinned) }
}

/** A heap copy handed to an asynchronous Filament upload, released through [callback]. */
class Upload(val ptr: Int, val size: Int, val callback: Int, val userData: Int)

/**
 * Copies [size] bytes of [data] into the heap for a `set*Buffer`/`setImage` call. Pass [Upload.callback]
 * and [Upload.userData] as the C release callback: Filament frees the copy once consumed, then
 * [onRelease] runs.
 */
fun FilamentModule.upload(data: ByteArray, size: Int = data.size, onRelease: (() -> Unit)? = null): Upload {
    val ptr = _malloc(maxOf(size, 1))
    check(ptr != 0) { "wasm malloc($size) failed" }
    writeBytes(ptr, data, 0, size)
    val userData = if (onRelease != null) Callbacks.register(once = true) { _, _ -> onRelease() } else 0
    return Upload(ptr, size, Callbacks.freeBuffer, userData)
}

inline fun <R> ShortArray.usePinned(block: (ptr: Int) -> R): R = fila.heapScoped {
    val ptr = alloc(size * 2)
    val heap = fila.HEAP16; val base = ptr ushr 1
    for (i in indices) heap[base + i] = this@usePinned[i]
    block(ptr).also { val h = fila.HEAP16; for (i in indices) this@usePinned[i] = h[base + i] }
}

inline fun <R> ByteArray.usePinned(block: (ptr: Int) -> R): R = fila.heapScoped {
    val ptr = bytes(this@usePinned)
    block(ptr).also { fila.readBytes(ptr, size).copyInto(this@usePinned) }
}

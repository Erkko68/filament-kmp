package io.github.erkko68.filament.jni

import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Hand-written half of the JNI layer (native side: src/main/cpp/FilaJni.cpp). [FilamentJni] holds the
 * generated Fila* functions and struct views; this object loads libfilament-c and covers what they can't express.
 */
object FilaJni {
    init { System.loadLibrary("filament-c") }

    /** Forces the library load; [FilamentJni] calls it from its initializer. */
    fun load() {}

    /** Zero-filled native memory (calloc): hand-built C structs must start fully initialised. */
    @JvmStatic external fun alloc(size: Long): Long
    @JvmStatic external fun free(ptr: Long)

    /** Native address of a direct [buffer], to pass where the C API takes a pointer. */
    @JvmStatic external fun address(buffer: ByteBuffer): Long

    /** A direct ByteBuffer over [size] bytes of native memory at [ptr], in big-endian (JNI's default). */
    @JvmStatic external fun view(ptr: Long, size: Long): ByteBuffer

    /** [view] in native byte order, which struct views and C arrays need. */
    fun buffer(ptr: Long, size: Int): ByteBuffer = view(ptr, size.toLong()).order(ByteOrder.nativeOrder())

    /** `ANativeWindow*` for an `android.view.Surface`, for FilaEngine_createSwapChain; release with [releaseWindow]. */
    @JvmStatic external fun windowFromSurface(surface: Any): Long
    @JvmStatic external fun releaseWindow(window: Long)

    /**
     * userData for a C callback built by [bufferCallback], [userDataCallback] or [pointerCallback]. A [once]
     * callback frees itself after its first call; otherwise free it with [releaseCallback] once C can't call it.
     */
    @JvmStatic external fun newCallback(callback: FilaCallback, once: Boolean): Long
    @JvmStatic external fun releaseCallback(userData: Long)

    /** `void (*)(void* buffer, size_t size, void* userData)` (FilaBufferCallback); `arg` is `buffer`. */
    @JvmStatic external fun bufferCallback(): Long

    /** `void (*)(void* userData)` (engine compile, frame scheduled); `arg` is 0. */
    @JvmStatic external fun userDataCallback(): Long

    /** `void (*)(T* arg, void* userData)` (material compile, stream, frame completed, picking). */
    @JvmStatic external fun pointerCallback(): Long
}

/** A C callback's Kotlin side; [arg] is the callback's first pointer argument, or 0. Runs on the calling native thread. */
fun interface FilaCallback {
    fun invoke(arg: Long)
}

/** Scratch native memory freed when [nativeScoped] returns, like web's `heapScoped`. */
class NativeScope {
    private val allocations = ArrayList<Long>()

    fun alloc(size: Int): Long = FilaJni.alloc(size.toLong()).also { allocations += it }

    @PublishedApi internal fun freeAll() = allocations.forEach(FilaJni::free)
}

inline fun <R> nativeScoped(block: NativeScope.() -> R): R {
    val scope = NativeScope()
    try {
        return scope.block()
    } finally {
        scope.freeAll()
    }
}

// Struct field access by the field's compiled size (1/2/4/8 bytes differ per ABI for size_t and pointers).
fun ByteBuffer.readInt(at: Int, size: Int, signed: Boolean): Int = when (size) {
    1 -> get(at).toInt().let { if (signed) it else it and 0xff }
    2 -> getShort(at).toInt().let { if (signed) it else it and 0xffff }
    else -> getInt(at)
}

fun ByteBuffer.writeInt(at: Int, size: Int, value: Int) {
    when (size) {
        1 -> put(at, value.toByte())
        2 -> putShort(at, value.toShort())
        else -> putInt(at, value)
    }
}

// ponytail: 4-byte reads zero-extend (pointers, size_t on 32-bit ABIs); no struct has a signed 32-bit `long`.
fun ByteBuffer.readLong(at: Int, size: Int): Long = if (size == 8) getLong(at) else getInt(at).toLong() and 0xffffffffL

fun ByteBuffer.writeLong(at: Int, size: Int, value: Long) {
    if (size == 8) putLong(at, value) else putInt(at, value.toInt())
}

/** Views over fixed-size C array fields, like the wasm F32Array & co. */
class F32Array(private val b: ByteBuffer, private val at: Int) {
    operator fun get(i: Int): Float = b.getFloat(at + i * 4)
    operator fun set(i: Int, value: Float) { b.putFloat(at + i * 4, value) }
}

class F64Array(private val b: ByteBuffer, private val at: Int) {
    operator fun get(i: Int): Double = b.getDouble(at + i * 8)
    operator fun set(i: Int, value: Double) { b.putDouble(at + i * 8, value) }
}

class I32Array(private val b: ByteBuffer, private val at: Int) {
    operator fun get(i: Int): Int = b.getInt(at + i * 4)
    operator fun set(i: Int, value: Int) { b.putInt(at + i * 4, value) }
}

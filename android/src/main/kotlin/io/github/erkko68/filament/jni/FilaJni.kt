package io.github.erkko68.filament.jni

import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Hand-written half of the JNI layer (native side: src/main/cpp/FilaJni.cpp). The generated FilamentC.kt & co.
 * hold the Fila* functions and struct views; this object loads libfilament-c and covers what they can't express.
 * The helpers below mirror :web's (heapScoped, usePinned, upload, Callbacks) so actuals port between them as-is.
 */
object FilaJni {
    init { System.loadLibrary("filament-c") }

    /** Forces the library load; each generated file calls it from its initializer. */
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

    /** Reads a NUL-terminated UTF-8 string, or null for a null pointer. */
    @JvmStatic external fun readString(ptr: Long): String?

    /** `ANativeWindow*` for an `android.view.Surface`, for FilaEngine_createSwapChain; release with [releaseWindow]. */
    @JvmStatic external fun windowFromSurface(surface: Any): Long
    @JvmStatic external fun releaseWindow(window: Long)

    @JvmStatic external fun newCallback(callback: FilaCallback, once: Boolean): Long
    @JvmStatic external fun releaseCallback(userData: Long)
    @JvmStatic external fun userOnly(): Long
    @JvmStatic external fun argUser(): Long
    @JvmStatic external fun keepBuffer(): Long
    @JvmStatic external fun freeBuffer(): Long
}

/** JNI target behind [Callbacks.register]; `a`/`b` are the callback's leading C arguments (or 0). */
fun interface FilaCallback {
    fun invoke(a: Long, b: Long)
}

/**
 * Kotlin lambdas behind C callbacks, shaped like :web's. Pass [register]'s result as the C `userData` and
 * one of the trampolines as the function pointer. Callbacks run on the calling native thread (usually
 * Filament's driver thread); an exception is reported and swallowed rather than unwinding through C++.
 */
object Callbacks {
    /** Registers [fn] and returns its userData. A [once] callback frees itself after its first call; release others. */
    fun register(once: Boolean, fn: (a: Long, b: Long) -> Unit): Long = FilaJni.newCallback(FilaCallback(fn), once)

    fun release(userData: Long) = FilaJni.releaseCallback(userData)

    /** `void (*)(void* userData)` — e.g. FilaEngineCompileCallback, frame-scheduled. */
    val userOnly: Long by lazy { FilaJni.userOnly() }

    /** `void (*)(T* arg, void* userData)` — e.g. picking, frame-completed, material compile. */
    val argUser: Long by lazy { FilaJni.argUser() }

    /** FilaBufferCallback that frees an [upload] copy, then runs the registered lambda if userData isn't 0. */
    val freeBuffer: Long by lazy { FilaJni.freeBuffer() }

    /** FilaBufferCallback that leaves the buffer alone (e.g. readPixels, which reads it in the lambda). */
    val keepBuffer: Long by lazy { FilaJni.keepBuffer() }
}

/**
 * The analogue of cinterop's `memScoped` (and :web's `heapScoped`): everything allocated in [block] is freed
 * when it returns. Allocations are zeroed, so structs only need the fields they actually set.
 */
inline fun <R> heapScoped(block: HeapScope.() -> R): R {
    val scope = HeapScope()
    try {
        return scope.block()
    } finally {
        scope.freeAll()
    }
}

class HeapScope {
    private val allocations = ArrayList<Long>(4)

    fun alloc(size: Int): Long = allocZeroed(size).also { allocations += it }

    fun bytes(values: ByteArray): Long = alloc(values.size).also { writeBytes(it, values) }
    fun floats(values: FloatArray): Long = alloc(values.size * 4).also { FilaJni.buffer(it, values.size * 4).asFloatBuffer().put(values) }
    fun doubles(values: DoubleArray): Long = alloc(values.size * 8).also { FilaJni.buffer(it, values.size * 8).asDoubleBuffer().put(values) }
    fun ints(values: IntArray): Long = alloc(values.size * 4).also { FilaJni.buffer(it, values.size * 4).asIntBuffer().put(values) }
    fun shorts(values: ShortArray): Long = alloc(values.size * 2).also { FilaJni.buffer(it, values.size * 2).asShortBuffer().put(values) }

    @PublishedApi internal fun freeAll() {
        allocations.forEach(FilaJni::free)
        allocations.clear()
    }
}

fun allocZeroed(size: Int): Long = FilaJni.alloc(maxOf(size, 1).toLong()).also { check(it != 0L) { "calloc($size) failed" } }

/** Copies [count] bytes of [bytes] (from [offset]) into native memory at [ptr]. */
fun writeBytes(ptr: Long, bytes: ByteArray, offset: Int = 0, count: Int = bytes.size - offset) {
    if (count > 0) FilaJni.buffer(ptr, count).put(bytes, offset, count)
}

/** Copies [count] bytes from native memory at [ptr] into a new ByteArray. */
fun readBytes(ptr: Long, count: Int): ByteArray = ByteArray(count).also { if (count > 0) FilaJni.buffer(ptr, count).get(it) }

fun readString(ptr: Long): String? = FilaJni.readString(ptr)

// Stand-ins for cinterop's usePinned: the array is copied in, [block] gets its native address, and the
// contents are copied back (C may have written to it) before the copy is freed.

inline fun <R> FloatArray.usePinned(block: (ptr: Long) -> R): R = heapScoped {
    val ptr = floats(this@usePinned)
    block(ptr).also { FilaJni.buffer(ptr, size * 4).asFloatBuffer().get(this@usePinned) }
}

inline fun <R> DoubleArray.usePinned(block: (ptr: Long) -> R): R = heapScoped {
    val ptr = doubles(this@usePinned)
    block(ptr).also { FilaJni.buffer(ptr, size * 8).asDoubleBuffer().get(this@usePinned) }
}

inline fun <R> IntArray.usePinned(block: (ptr: Long) -> R): R = heapScoped {
    val ptr = ints(this@usePinned)
    block(ptr).also { FilaJni.buffer(ptr, size * 4).asIntBuffer().get(this@usePinned) }
}

inline fun <R> ShortArray.usePinned(block: (ptr: Long) -> R): R = heapScoped {
    val ptr = shorts(this@usePinned)
    block(ptr).also { FilaJni.buffer(ptr, size * 2).asShortBuffer().get(this@usePinned) }
}

inline fun <R> ByteArray.usePinned(block: (ptr: Long) -> R): R = heapScoped {
    val ptr = bytes(this@usePinned)
    block(ptr).also { if (size > 0) FilaJni.buffer(ptr, size).get(this@usePinned) }
}

/** A native copy handed to an asynchronous Filament upload, released through [callback]. */
class Upload(val ptr: Long, val size: Int, val callback: Long, val userData: Long)

/**
 * Copies [size] bytes of [data] into native memory for a `set*Buffer`/`setImage` call. Pass [Upload.callback]
 * and [Upload.userData] as the C release callback: Filament frees the copy once consumed, then [onRelease] runs.
 */
fun upload(data: ByteArray, size: Int = data.size, onRelease: (() -> Unit)? = null): Upload {
    val ptr = allocZeroed(size)
    writeBytes(ptr, data, 0, size)
    val userData = if (onRelease != null) Callbacks.register(once = true) { _, _ -> onRelease() } else 0L
    return Upload(ptr, size, Callbacks.freeBuffer, userData)
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

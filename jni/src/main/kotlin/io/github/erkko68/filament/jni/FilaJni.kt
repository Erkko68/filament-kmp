package io.github.erkko68.filament.jni

import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Hand-written half of the JNI layer (native side: src/main/cpp/FilaJni.cpp): native memory and callbacks,
 * what the generated forwarders can't express. libfilament-c is loaded by `Filament.init()` on each platform.
 */
object FilaJni {
    /** Zero-filled native memory (calloc): hand-built C structs must start fully initialised. */
    @JvmStatic external fun alloc(size: Long): Long
    @JvmStatic external fun free(ptr: Long)

    /** A direct ByteBuffer over [size] bytes of native memory at [ptr], in big-endian (JNI's default). */
    @JvmStatic external fun view(ptr: Long, size: Long): ByteBuffer

    /** [view] in native byte order, as C arrays need. */
    fun buffer(ptr: Long, size: Int): ByteBuffer = view(ptr, size.toLong()).order(ByteOrder.nativeOrder())

    /** `sizeof(void*)`: 4 on Android's 32-bit ABIs. */
    val pointerSize: Int by lazy { nativePointerSize() }
    @JvmStatic private external fun nativePointerSize(): Int

    /** Reads a NUL-terminated UTF-8 string, or null for a null pointer. */
    @JvmStatic external fun readString(ptr: Long): String?

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

    fun release(userData: Long) { if (userData != 0L) FilaJni.releaseCallback(userData) }

    /** `void (*)(void* userData)` — e.g. FilaEngineCompileCallback, frame-scheduled. */
    val userOnly: Long by lazy { FilaJni.userOnly() }

    /** `void (*)(T* arg, void* userData)` — e.g. picking, frame-completed, material compile. */
    val argUser: Long by lazy { FilaJni.argUser() }

    /** FilaBufferCallback that frees an [upload] copy, then runs the registered lambda if userData isn't 0. */
    val freeBuffer: Long by lazy { FilaJni.freeBuffer() }

    /** FilaBufferCallback that leaves the buffer alone (e.g. readPixels, which reads it in the lambda). */
    val keepBuffer: Long by lazy { FilaJni.keepBuffer() }
}

private fun allocZeroed(size: Int): Long = FilaJni.alloc(maxOf(size, 1).toLong()).also { check(it != 0L) { "calloc($size) failed" } }

/** A native copy handed to an asynchronous Filament upload, released through [callback]. */
class Upload(val ptr: Long, val size: Int, val callback: Long, val userData: Long)

/**
 * Copies [size] bytes of [data] into native memory for a `set*Buffer`/`setImage` call. Pass [Upload.callback]
 * and [Upload.userData] as the C release callback: Filament frees the copy once consumed, then [onRelease] runs.
 */
fun upload(data: ByteArray, size: Int = data.size, onRelease: (() -> Unit)? = null): Upload {
    val ptr = allocZeroed(size)
    if (size > 0) FilaJni.buffer(ptr, size).put(data, 0, size)
    val userData = if (onRelease != null) Callbacks.register(once = true) { _, _ -> onRelease() } else 0L
    return Upload(ptr, size, Callbacks.freeBuffer, userData)
}

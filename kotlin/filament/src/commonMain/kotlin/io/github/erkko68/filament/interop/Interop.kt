package io.github.erkko68.filament.interop

import io.github.erkko68.filament.InternalFilamentApi

// Common half of the skiko-style interop: API classes live in commonMain and call the Fila* C API
// through `external fun`s declared next to them, named like the C symbol. JVM/Android bind them
// through generated JNI glue, Kotlin/Native through [ExternalSymbolName], web by global name.
// See docs/internals/bindings.md.

/** Address of a native object: `Long` on JVM, Android and Native; a wasm32 address (`Int`) on web. */
@InternalFilamentApi
expect class NativePointer

@InternalFilamentApi
expect val NullPointer: NativePointer

/** True on web: wasm has no threads, so nothing may block waiting on Filament's driver. */
@InternalFilamentApi
expect val singleThreaded: Boolean

/** The C symbol a common `external fun` binds to (`@SymbolName` on Kotlin/Native, absent elsewhere). */
@OptIn(ExperimentalMultiplatform::class)
@OptionalExpectation
expect annotation class ExternalSymbolName(val name: String)

/**
 * Hands Kotlin arrays to C for the duration of one call: pinned on Native, copied into native memory
 * (and back with [fromInterop]) on JVM, Android and web. Empty and null arrays map to [NullPointer].
 * Use through [interopScope] with the length-checked `toInterop(array, size)`, or the `usePinned` helpers.
 */
@InternalFilamentApi
expect class InteropScope() {
    fun copyToInterop(array: ByteArray?): NativePointer
    fun copyToInterop(array: ShortArray?): NativePointer
    fun copyToInterop(array: IntArray?): NativePointer
    fun copyToInterop(array: LongArray?): NativePointer
    fun copyToInterop(array: FloatArray?): NativePointer
    fun copyToInterop(array: DoubleArray?): NativePointer

    /** An array of pointers (`T* const*`, or a `T**` C writes through), as wide as the platform's; read it back with [readPointers]. */
    fun toInterop(pointers: List<NativePointer>): NativePointer

    /** Copies what C wrote at this pointer back into [result] (a no-op where the array was pinned). */
    fun NativePointer.fromInterop(result: ByteArray)
    fun NativePointer.fromInterop(result: ShortArray)
    fun NativePointer.fromInterop(result: IntArray)
    fun NativePointer.fromInterop(result: LongArray)
    fun NativePointer.fromInterop(result: FloatArray)
    fun NativePointer.fromInterop(result: DoubleArray)

    fun release()
}

/** A NUL-terminated UTF-8 copy of [string] for a `const char*` parameter; null stays [NullPointer]. */
@InternalFilamentApi
fun InteropScope.toInterop(string: String?): NativePointer =
    if (string == null) NullPointer else copyToInterop(string.encodeToByteArray() + 0)

/**
 * Fails unless an array of [length] elements holds the [size] C reads or writes through it: a shorter one
 * would be read or written out of bounds in native memory.
 */
@PublishedApi
internal fun requireElements(length: Int, size: Int) =
    require(size in 0..length) { "the array holds $length elements but $size are needed" }

/** [array] for a C parameter that reads or writes [size] elements of it. */
@InternalFilamentApi
fun InteropScope.toInterop(array: ByteArray?, size: Int): NativePointer = copyToInterop(array?.also { requireElements(it.size, size) })

/** [array] for a C parameter that reads or writes [size] elements of it. */
@InternalFilamentApi
fun InteropScope.toInterop(array: ShortArray?, size: Int): NativePointer = copyToInterop(array?.also { requireElements(it.size, size) })

/** [array] for a C parameter that reads or writes [size] elements of it. */
@InternalFilamentApi
fun InteropScope.toInterop(array: IntArray?, size: Int): NativePointer = copyToInterop(array?.also { requireElements(it.size, size) })

/** [array] for a C parameter that reads or writes [size] elements of it. */
@InternalFilamentApi
fun InteropScope.toInterop(array: LongArray?, size: Int): NativePointer = copyToInterop(array?.also { requireElements(it.size, size) })

/** [array] for a C parameter that reads or writes [size] elements of it. */
@InternalFilamentApi
fun InteropScope.toInterop(array: FloatArray?, size: Int): NativePointer = copyToInterop(array?.also { requireElements(it.size, size) })

/** [array] for a C parameter that reads or writes [size] elements of it. */
@InternalFilamentApi
fun InteropScope.toInterop(array: DoubleArray?, size: Int): NativePointer = copyToInterop(array?.also { requireElements(it.size, size) })

/**
 * Element [index] of a float array C filled in. On js a FloatArray keeps the raw f32 value (0.7f reads back
 * as 0.699999988), so this returns the shortest decimal with the same f32 value; elsewhere it's just `get`.
 */
@InternalFilamentApi
expect fun FloatArray.readF32(index: Int): Float

/** A copy of [count] bytes C owns (a buffer a callback lends). */
@InternalFilamentApi
expect fun readBytes(ptr: NativePointer, count: Int): ByteArray

/** A copy of [count] ints of an array C owns (a borrowed `const FilaEntity*`). */
@InternalFilamentApi
expect fun readInts(ptr: NativePointer, count: Int): IntArray

/** A copy of [count] floats of an array C owns (math mirrors: a `const FilaMat4f*` is 16 per matrix). */
@InternalFilamentApi
expect fun readFloats(ptr: NativePointer, count: Int): FloatArray

/** A copy of [count] pointers of an array C owns (`T* const*`, `const char* const*`). */
@InternalFilamentApi
expect fun readPointers(ptr: NativePointer, count: Int): List<NativePointer>

/** Calls [block] with a `const char*` copy of this string, valid for the call. */
@InternalFilamentApi
inline fun <R> String?.useCString(block: (NativePointer) -> R): R = interopScope { block(toInterop(this@useCString)) }

/** Reads a NUL-terminated UTF-8 string returned by C, or null for [NullPointer]. */
@InternalFilamentApi
expect fun stringFromInterop(ptr: NativePointer): String?

/**
 * Kotlin lambdas behind C callbacks. Pass [register]'s result as the C `userData` and [userOnly] or
 * [argUser] as the function pointer; the lambda receives the callback's leading pointer argument
 * (or [NullPointer]). A `once` callback frees itself after firing; [release] the others.
 */
@InternalFilamentApi
expect object Callbacks {
    fun register(once: Boolean, fn: (arg: NativePointer) -> Unit): NativePointer
    /** [register] for [userStatus] and [argUserStatus]: the lambda also gets the `AsyncCallStatus` ordinal. */
    fun registerStatus(once: Boolean, fn: (arg: NativePointer, status: Int) -> Unit): NativePointer
    fun release(userData: NativePointer)

    /** `void (*)(void* userData)`. */
    val userOnly: NativePointer

    /** `void (*)(T* arg, void* userData)`. */
    val argUser: NativePointer

    /** `void (*)(void* userData, AsyncCallStatus status)`. */
    val userStatus: NativePointer

    /** `void (*)(T* arg, void* userData, AsyncCallStatus status)`. */
    val argUserStatus: NativePointer

    /** FilaBufferCallback, `void (*)(void* buffer, size_t size, void* userData)`: the lambda gets the buffer. */
    val keepBuffer: NativePointer
}

/**
 * [size] bytes of an array handed to an asynchronous `set*Buffer`/`setImage`: pass all four fields to the
 * C call. Filament's release callback drops the copy (unpins on Native), then runs the upload's onRelease.
 */
@InternalFilamentApi
class Upload(val ptr: NativePointer, val size: Int, val callback: NativePointer, val userData: NativePointer)

@InternalFilamentApi
fun upload(data: ByteArray, size: Int, onRelease: (() -> Unit)?): Upload {
    requireElements(data.size, size)
    return platformUpload(data, size, onRelease)
}

internal expect fun platformUpload(data: ByteArray, size: Int, onRelease: (() -> Unit)?): Upload

@InternalFilamentApi
inline fun <T> interopScope(block: InteropScope.() -> T): T {
    val scope = InteropScope()
    try {
        return scope.block()
    } finally {
        scope.release()
    }
}

// A native object from [create] for the duration of [block], destroyed afterwards.
internal inline fun <T> withHandle(create: () -> NativePointer, destroy: (NativePointer) -> Unit, block: (NativePointer) -> T): T {
    val handle = create()
    try {
        return block(handle)
    } finally {
        destroy(handle)
    }
}

// The array's address for the duration of a C call that reads or writes [size] elements of it;
// whatever C wrote there is in the array afterwards.

@InternalFilamentApi
inline fun <R> ByteArray.usePinned(size: Int, block: (NativePointer) -> R): R {
    requireElements(this.size, size)
    return interopScope { val p = copyToInterop(this@usePinned); block(p).also { p.fromInterop(this@usePinned) } }
}

@InternalFilamentApi
inline fun <R> ShortArray.usePinned(size: Int, block: (NativePointer) -> R): R {
    requireElements(this.size, size)
    return interopScope { val p = copyToInterop(this@usePinned); block(p).also { p.fromInterop(this@usePinned) } }
}

@InternalFilamentApi
inline fun <R> IntArray.usePinned(size: Int, block: (NativePointer) -> R): R {
    requireElements(this.size, size)
    return interopScope { val p = copyToInterop(this@usePinned); block(p).also { p.fromInterop(this@usePinned) } }
}

@InternalFilamentApi
inline fun <R> LongArray.usePinned(size: Int, block: (NativePointer) -> R): R {
    requireElements(this.size, size)
    return interopScope { val p = copyToInterop(this@usePinned); block(p).also { p.fromInterop(this@usePinned) } }
}

@InternalFilamentApi
inline fun <R> FloatArray.usePinned(size: Int, block: (NativePointer) -> R): R {
    requireElements(this.size, size)
    return interopScope { val p = copyToInterop(this@usePinned); block(p).also { p.fromInterop(this@usePinned) } }
}

@InternalFilamentApi
inline fun <R> DoubleArray.usePinned(size: Int, block: (NativePointer) -> R): R {
    requireElements(this.size, size)
    return interopScope { val p = copyToInterop(this@usePinned); block(p).also { p.fromInterop(this@usePinned) } }
}

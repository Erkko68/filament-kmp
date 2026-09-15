@file:Suppress("NOTHING_TO_INLINE")

package io.github.erkko68.filament

import io.github.erkko68.filament.ffm.FilaBufferCallback
import io.github.erkko68.filament.ffm.FilaEngineCompileCallback
import io.github.erkko68.filament.ffm.FilaMaterialCompileCallback
import io.github.erkko68.filament.ffm.FilamentLoader
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.SegmentAllocator
import java.lang.foreign.ValueLayout
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

/**
 * Small shared helpers for the Project Panama (FFM) JVM bindings.
 *
 * The low-level C ABI lives in the generated [io.github.erkko68.filament.ffm.FilamentC] class;
 * these helpers cover the few cross-cutting concerns: loading the native image, scoping
 * off-heap allocations, and converting strings / null pointers.
 */

/** Ensures the combined libfilament-c image is loaded before the first downcall. Idempotent. */
public fun ensureFilamentLoaded() = FilamentLoader.load()

/** The C `NULL` pointer. */
public val NULL: MemorySegment = MemorySegment.NULL

/** Runs [block] with a confined [Arena] whose allocations are freed when it returns. */
public inline fun <T> confined(block: (Arena) -> T): T = Arena.ofConfined().use(block)

/** Allocates a NUL-terminated UTF-8 C string in this allocator's memory. */
public fun SegmentAllocator.cstr(s: String): MemorySegment = allocateFrom(s)

/** True for a null/destroyed handle. */
public fun MemorySegment?.isNullPtr(): Boolean = this == null || address() == 0L

/** Reads a NUL-terminated UTF-8 C string from a `const char*` returned by native code. */
public fun MemorySegment.cString(): String = reinterpret(Long.MAX_VALUE).getString(0)

// ── Primitive array <-> off-heap segment helpers ─────────────────────────────
public fun SegmentAllocator.ints(a: IntArray): MemorySegment = allocateFrom(ValueLayout.JAVA_INT, *a)
public fun SegmentAllocator.floats(a: FloatArray): MemorySegment = allocateFrom(ValueLayout.JAVA_FLOAT, *a)
public fun SegmentAllocator.shorts(a: ShortArray): MemorySegment = allocateFrom(ValueLayout.JAVA_SHORT, *a)
public fun SegmentAllocator.bytes(a: ByteArray): MemorySegment =
    byteBuffer(a.size).also { MemorySegment.copy(a, 0, it, ValueLayout.JAVA_BYTE, 0L, a.size) }

/**
 * Allocates [size] bytes for a payload the backend reads or writes element-wise — vertex/index
 * data, a pixel buffer. `allocate(size)` and `allocateFrom(JAVA_BYTE, …)` promise only byte
 * alignment; [BUFFER_ALIGNMENT] covers the natural alignment of every element type Filament
 * puts in one of these.
 */
public fun SegmentAllocator.byteBuffer(size: Int): MemorySegment = allocate(size.toLong(), BUFFER_ALIGNMENT)

/** Alignment [byteBuffer] and [bytes] guarantee. */
public const val BUFFER_ALIGNMENT: Long = 8L
public fun SegmentAllocator.booleans(a: BooleanArray): MemorySegment {
    // FFM has no allocateFrom(OfBoolean, boolean...) vararg overload; set elements individually.
    val seg = allocate(ValueLayout.JAVA_BOOLEAN, a.size.toLong())
    for (i in a.indices) seg.set(ValueLayout.JAVA_BOOLEAN, i.toLong(), a[i])
    return seg
}

public fun SegmentAllocator.doubles(a: DoubleArray): MemorySegment = allocateFrom(ValueLayout.JAVA_DOUBLE, *a)

public fun SegmentAllocator.intArr(n: Int): MemorySegment = allocate(ValueLayout.JAVA_INT, n.toLong())
public fun SegmentAllocator.floatArr(n: Int): MemorySegment = allocate(ValueLayout.JAVA_FLOAT, n.toLong())
public fun SegmentAllocator.shortArr(n: Int): MemorySegment = allocate(ValueLayout.JAVA_SHORT, n.toLong())
public fun SegmentAllocator.doubleArr(n: Int): MemorySegment = allocate(ValueLayout.JAVA_DOUBLE, n.toLong())

// Element access on a struct array-field slice. jextract 22's indexed array-field accessors
// (e.g. `field(struct, index)`) have a bug: their VarHandle omits the field's base offset, so
// they read/write at the struct base. Always go through the single-arg slice accessor — which
// applies the correct offset — and index into it with these helpers.
public fun MemorySegment.getFloatAt(i: Int): Float = getAtIndex(ValueLayout.JAVA_FLOAT, i.toLong())
public fun MemorySegment.setFloatAt(i: Int, v: Float) = setAtIndex(ValueLayout.JAVA_FLOAT, i.toLong(), v)

/** Copies [size] bytes out of a native `void*`/`const char*` buffer into a fresh ByteArray. */
public fun MemorySegment.readBytes(size: Int): ByteArray =
    if (isNullPtr() || size <= 0) ByteArray(0) else reinterpret(size.toLong()).toArray(ValueLayout.JAVA_BYTE)

public fun MemorySegment.toInts(): IntArray = toArray(ValueLayout.JAVA_INT)
public fun MemorySegment.toFloats(): FloatArray = toArray(ValueLayout.JAVA_FLOAT)
public fun MemorySegment.toShorts(): ShortArray = toArray(ValueLayout.JAVA_SHORT)
public fun MemorySegment.toDoubles(): DoubleArray = toArray(ValueLayout.JAVA_DOUBLE)

/**
 * Runs [block] on a native callback thread. An exception escaping an FFM upcall terminates the
 * VM, so anything the user's callback throws is reported as an uncaught exception instead.
 */
public inline fun upcall(block: () -> Unit) {
    try {
        block()
    } catch (t: Throwable) {
        val thread = Thread.currentThread()
        thread.uncaughtExceptionHandler.uncaughtException(thread, t)
    }
}

// ── One-shot completion callbacks ────────────────────────────────────────────
//
// FFM upcall stubs cannot be freed from inside their own invocation (unlike Kotlin/Native's
// static C functions). So instead of allocating a stub per call, we keep ONE persistent stub
// per C callback signature and pass the registry key as the `userData` pointer's address. When
// the native side fires the stub, we look the action up by key, run it once, and drop it — no
// stub is ever freed, and per-call buffers live in their own shared arena closed by the action.

/** Keys one-shot callbacks by a synthetic `userData` address for the scheme described above. */
public class CallbackRegistry<T : Any> {
    private val actions = ConcurrentHashMap<Long, T>()
    private val counter = AtomicLong(1L)

    /** Registers a one-shot [action] and returns the `userData` pointer encoding its key. */
    public fun register(action: T): MemorySegment {
        val id = counter.getAndIncrement()
        actions[id] = action
        return MemorySegment.ofAddress(id)
    }

    /** Removes and returns the action [userData] keys, or null if it already fired. */
    public fun take(userData: MemorySegment): T? = actions.remove(userData.address())

    /** Actions registered but not yet fired. Returns to its baseline once the engine drains. */
    public val pending: Int get() = actions.size
}

public object Completions {
    private val arena: Arena = Arena.ofShared()
    private val registry = CallbackRegistry<() -> Unit>()

    /** Registers a one-shot [action] and returns the `userData` pointer encoding its key. */
    fun register(action: () -> Unit): MemorySegment = registry.register(action)

    /** Callbacks handed to Filament that have not fired yet. */
    val pending: Int get() = registry.pending

    private fun fire(userData: MemorySegment) {
        val action = registry.take(userData) ?: return
        upcall(action)
    }

    /** Persistent stub for `FilaBufferCallback` (buffer uploads, readPixels, setImage). */
    val bufferStub: MemorySegment by lazy {
        FilaBufferCallback.allocate({ _, _, userData -> fire(userData) }, arena)
    }

    /** Persistent stub for `FilaEngineCompileCallback`. */
    val compileStub: MemorySegment by lazy {
        FilaEngineCompileCallback.allocate({ userData -> fire(userData) }, arena)
    }

    /** Persistent stub for `FilaMaterialCompileCallback` (apply(material, userData)). */
    val materialCompileStub: MemorySegment by lazy {
        FilaMaterialCompileCallback.allocate({ _, userData -> fire(userData) }, arena)
    }
}

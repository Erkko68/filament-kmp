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
internal fun ensureFilamentLoaded() = FilamentLoader.load()

/** The C `NULL` pointer. */
internal val NULL: MemorySegment = MemorySegment.NULL

/** Runs [block] with a confined [Arena] whose allocations are freed when it returns. */
internal inline fun <T> confined(block: (Arena) -> T): T = Arena.ofConfined().use(block)

/** Allocates a NUL-terminated UTF-8 C string in this allocator's memory. */
internal fun SegmentAllocator.cstr(s: String): MemorySegment = allocateFrom(s)

/** True for a null/destroyed handle. */
internal fun MemorySegment?.isNullPtr(): Boolean = this == null || address() == 0L

/** Reads a NUL-terminated UTF-8 C string from a `const char*` returned by native code. */
internal fun MemorySegment.cString(): String = reinterpret(Long.MAX_VALUE).getString(0)

// ── Primitive array <-> off-heap segment helpers ─────────────────────────────
internal fun SegmentAllocator.ints(a: IntArray): MemorySegment = allocateFrom(ValueLayout.JAVA_INT, *a)
internal fun SegmentAllocator.floats(a: FloatArray): MemorySegment = allocateFrom(ValueLayout.JAVA_FLOAT, *a)
internal fun SegmentAllocator.shorts(a: ShortArray): MemorySegment = allocateFrom(ValueLayout.JAVA_SHORT, *a)
internal fun SegmentAllocator.bytes(a: ByteArray): MemorySegment = allocateFrom(ValueLayout.JAVA_BYTE, *a)
internal fun SegmentAllocator.booleans(a: BooleanArray): MemorySegment {
    // FFM has no allocateFrom(OfBoolean, boolean...) vararg overload; set elements individually.
    val seg = allocate(ValueLayout.JAVA_BOOLEAN, a.size.toLong())
    for (i in a.indices) seg.set(ValueLayout.JAVA_BOOLEAN, i.toLong(), a[i])
    return seg
}

internal fun SegmentAllocator.doubles(a: DoubleArray): MemorySegment = allocateFrom(ValueLayout.JAVA_DOUBLE, *a)

internal fun SegmentAllocator.intArr(n: Int): MemorySegment = allocate(ValueLayout.JAVA_INT, n.toLong())
internal fun SegmentAllocator.floatArr(n: Int): MemorySegment = allocate(ValueLayout.JAVA_FLOAT, n.toLong())
internal fun SegmentAllocator.shortArr(n: Int): MemorySegment = allocate(ValueLayout.JAVA_SHORT, n.toLong())
internal fun SegmentAllocator.doubleArr(n: Int): MemorySegment = allocate(ValueLayout.JAVA_DOUBLE, n.toLong())

// Element access on a struct array-field slice. jextract 22's indexed array-field accessors
// (e.g. `field(struct, index)`) have a bug: their VarHandle omits the field's base offset, so
// they read/write at the struct base. Always go through the single-arg slice accessor — which
// applies the correct offset — and index into it with these helpers.
internal fun MemorySegment.getFloatAt(i: Int): Float = getAtIndex(ValueLayout.JAVA_FLOAT, i.toLong())
internal fun MemorySegment.setFloatAt(i: Int, v: Float) = setAtIndex(ValueLayout.JAVA_FLOAT, i.toLong(), v)

internal fun MemorySegment.toInts(): IntArray = toArray(ValueLayout.JAVA_INT)
internal fun MemorySegment.toFloats(): FloatArray = toArray(ValueLayout.JAVA_FLOAT)
internal fun MemorySegment.toShorts(): ShortArray = toArray(ValueLayout.JAVA_SHORT)
internal fun MemorySegment.toDoubles(): DoubleArray = toArray(ValueLayout.JAVA_DOUBLE)

// ── One-shot completion callbacks ────────────────────────────────────────────
//
// FFM upcall stubs cannot be freed from inside their own invocation (unlike Kotlin/Native's
// static C functions). So instead of allocating a stub per call, we keep ONE persistent stub
// per C callback signature and pass the registry key as the `userData` pointer's address. When
// the native side fires the stub, we look the action up by key, run it once, and drop it — no
// stub is ever freed, and per-call buffers live in their own shared arena closed by the action.
internal object Completions {
    private val arena: Arena = Arena.ofShared()
    private val actions = ConcurrentHashMap<Long, () -> Unit>()
    private val counter = AtomicLong(1L)

    /** Registers a one-shot [action] and returns the `userData` pointer encoding its key. */
    fun register(action: () -> Unit): MemorySegment {
        val id = counter.getAndIncrement()
        actions[id] = action
        return MemorySegment.ofAddress(id)
    }

    private fun fire(userData: MemorySegment) {
        actions.remove(userData.address())?.invoke()
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

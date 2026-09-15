package io.github.erkko68.filament

import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Contracts of the shared FFM helpers the JVM bindings lean on. */
class FfmHelpersTest {
    @Test
    fun byteBufferIsAlignedForAnyElementType() {
        Arena.ofConfined().use { arena ->
            for (size in listOf(1, 3, 7, 17, 1024)) {
                val seg = arena.byteBuffer(size)
                assertEquals(size.toLong(), seg.byteSize())
                assertEquals(0L, seg.address() % BUFFER_ALIGNMENT, "byteBuffer($size) was not aligned")
            }
        }
    }

    @Test
    fun bytesCopiesContentAndKeepsAlignment() {
        Arena.ofConfined().use { arena ->
            val data = ByteArray(19) { (it * 7 + 1).toByte() }
            val seg = arena.bytes(data)
            assertEquals(0L, seg.address() % BUFFER_ALIGNMENT, "bytes() was not aligned")
            assertContentEquals(data, seg.toArray(ValueLayout.JAVA_BYTE))
        }
    }

    @Test
    fun registryHandsEachActionOutExactlyOnce() {
        val registry = CallbackRegistry<() -> Unit>()
        assertEquals(0, registry.pending)

        val fired = mutableListOf<Int>()
        val keys = (0 until 5).map { i -> registry.register { fired += i } }
        assertEquals(5, registry.pending)

        // Distinct keys, and none of them is the C NULL pointer — Filament treats a null
        // userData as "no callback".
        assertEquals(5, keys.map { it.address() }.toSet().size)
        assertTrue(keys.none { it.address() == 0L })

        keys.forEach { registry.take(it)?.invoke() }
        assertContentEquals(listOf(0, 1, 2, 3, 4), fired)
        assertEquals(0, registry.pending, "registry did not drain")

        // A second delivery of the same userData must not re-run the action.
        assertNull(registry.take(keys[0]))
        assertNull(registry.take(MemorySegment.ofAddress(9999L)))
        assertEquals(0, registry.pending)
    }

    @Test
    fun upcallContainsExceptionsInsteadOfKillingTheVm() {
        val thread = Thread.currentThread()
        val previous = thread.uncaughtExceptionHandler
        var seen: Throwable? = null
        thread.uncaughtExceptionHandler = Thread.UncaughtExceptionHandler { _, t -> seen = t }
        try {
            upcall { throw IllegalStateException("boom") }
        } finally {
            thread.uncaughtExceptionHandler = previous
        }
        assertEquals("boom", seen?.message)
    }
}

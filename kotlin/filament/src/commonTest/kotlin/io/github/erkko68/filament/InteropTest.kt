package io.github.erkko68.filament

import io.github.erkko68.filament.interop.NullPointer
import io.github.erkko68.filament.interop.interopScope
import io.github.erkko68.filament.interop.readF32
import io.github.erkko68.filament.interop.readFloats
import io.github.erkko68.filament.interop.readInts
import io.github.erkko68.filament.interop.readPointers
import io.github.erkko68.filament.interop.toInterop
import io.github.erkko68.filament.interop.upload
import io.github.erkko68.filament.interop.usePinned
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals

/** The array marshalling every binding call goes through. */
class InteropTest {
    @BeforeTest
    fun setUp() = Filament.init()

    @Test
    fun nullAndEmptyArraysAreNullPointers() = interopScope {
        assertEquals(NullPointer, toInterop(null as ByteArray?, 0))
        assertEquals(NullPointer, toInterop(null as ShortArray?, 0))
        assertEquals(NullPointer, toInterop(null as IntArray?, 0))
        assertEquals(NullPointer, toInterop(null as LongArray?, 0))
        assertEquals(NullPointer, toInterop(null as FloatArray?, 0))
        assertEquals(NullPointer, toInterop(null as DoubleArray?, 0))
        assertEquals(NullPointer, toInterop(null as String?))
        assertEquals(NullPointer, toInterop(IntArray(0), 0))
        assertEquals(NullPointer, toInterop(emptyList()))
    }

    @Test
    fun readersCopyWhatWasWritten() = interopScope {
        val ints = intArrayOf(1, -2, Int.MAX_VALUE)
        assertContentEquals(ints, readInts(toInterop(ints, ints.size), ints.size))
        val floats = floatArrayOf(0.5f, -2f, 1e10f)
        assertContentEquals(floats, readFloats(toInterop(floats, floats.size), floats.size))
        assertEquals(-2f, floats.readF32(1))
        val pointers = listOf(toInterop(ints, ints.size), toInterop(floats, floats.size))
        assertEquals(pointers, readPointers(toInterop(pointers), pointers.size))
    }

    // C would read or write past the end of a shorter array.
    @Test
    fun arraysShorterThanCNeedsAreRejected() {
        interopScope {
            assertFailsWith<IllegalArgumentException> { toInterop(IntArray(2), 3) }
            assertFailsWith<IllegalArgumentException> { toInterop(FloatArray(2), -1) }
            assertEquals(NullPointer, toInterop(null as FloatArray?, 16)) // null stays NULL: C doesn't read it
        }
        assertFailsWith<IllegalArgumentException> { DoubleArray(15).usePinned(16) { } }
        assertFailsWith<IllegalArgumentException> { upload(ByteArray(4), 8, null) }
    }

    @Test
    fun readersOfNothingAreEmpty() {
        assertEquals(0, readInts(NullPointer, 0).size)
        assertEquals(0, readFloats(NullPointer, 0).size)
        assertEquals(emptyList(), readPointers(NullPointer, 0))
    }

    // Nothing wrote to the memory, so each array must come back as it went in.
    @Test
    fun pinnedArraysSurviveTheRoundTrip() {
        val bytes = byteArrayOf(1, -2, 3)
        bytes.usePinned(bytes.size) { assertNotEquals(NullPointer, it) }
        assertContentEquals(byteArrayOf(1, -2, 3), bytes)
        val shorts = shortArrayOf(1, -2, Short.MAX_VALUE)
        shorts.usePinned(shorts.size) { assertNotEquals(NullPointer, it) }
        assertContentEquals(shortArrayOf(1, -2, Short.MAX_VALUE), shorts)
        val ints = intArrayOf(1, -2, Int.MIN_VALUE)
        ints.usePinned(ints.size) { assertNotEquals(NullPointer, it) }
        assertContentEquals(intArrayOf(1, -2, Int.MIN_VALUE), ints)
        val longs = longArrayOf(1L, -2L, Long.MAX_VALUE)
        longs.usePinned(longs.size) { assertNotEquals(NullPointer, it) }
        assertContentEquals(longArrayOf(1L, -2L, Long.MAX_VALUE), longs)
        val floats = floatArrayOf(0.5f, -2f)
        floats.usePinned(floats.size) { assertNotEquals(NullPointer, it) }
        assertContentEquals(floatArrayOf(0.5f, -2f), floats)
        val doubles = doubleArrayOf(0.1, -2.0)
        doubles.usePinned(doubles.size) { assertNotEquals(NullPointer, it) }
        assertContentEquals(doubleArrayOf(0.1, -2.0), doubles)
        // An empty array has no address, and reading "back" from it is a no-op.
        FloatArray(0).usePinned(0) { assertEquals(NullPointer, it) }
    }
}

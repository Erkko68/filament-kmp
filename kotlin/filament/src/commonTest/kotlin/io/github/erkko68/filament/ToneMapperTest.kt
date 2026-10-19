package io.github.erkko68.filament

import io.github.erkko68.filament.testutils.FilamentTestFixture
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ToneMapperTest : FilamentTestFixture() {
    /**
     * Each subclass reaches its own native tone mapper: black stays black and, except for the false-colour
     * DisplayRange debug mapper, brighter input never maps darker.
     */
    @Test
    fun everyToneMapperIsMonotonic() {
        val mappers = listOf(
            ToneMapper.Linear(), ToneMapper.ACES(), ToneMapper.ACESLegacy(), ToneMapper.Filmic(),
            ToneMapper.PBRNeutralToneMapper(), ToneMapper.GT7ToneMapper(), ToneMapper.Agx(ToneMapper.Agx.AgxLook.NONE),
            ToneMapper.Generic(1.5f, 0.18f, 0.22f, 10f), ToneMapper.DisplayRange(),
        )
        for (mapper in mappers) {
            val name = mapper::class.simpleName
            mapper(floatArrayOf(0f, 0f, 0f)).forEach { assertEquals(0f, it, 1e-3f, "$name: black") }
            if (mapper !is ToneMapper.DisplayRange) {
                val ramp = listOf(0.01f, 0.1f, 0.5f, 1f, 4f).map { mapper(floatArrayOf(it, it, it))[1] }
                assertTrue(ramp.zipWithNext().all { (a, b) -> b >= a }, "$name is not monotonic: $ramp")
            }
            mapper.close()
        }
    }

    @Test
    fun parametersRoundTrip() {
        ToneMapper.Linear().use { linear ->
            assertContentEquals(floatArrayOf(0.5f, 1f, 0f), linear(floatArrayOf(0.5f, 2f, -1f)))
            assertTrue(linear.isOneDimensional)
            assertTrue(linear.isLDR)
        }
        ToneMapper.Agx().use { agx ->
            assertEquals(ToneMapper.Agx.AgxLook.NONE, agx.look)
            agx.look = ToneMapper.Agx.AgxLook.GOLDEN
            assertEquals(ToneMapper.Agx.AgxLook.GOLDEN, agx.look)
        }
        ToneMapper.Generic().use { generic ->
            assertEquals(1.55f, generic.contrast)
            assertEquals(10f, generic.hdrMax)
            generic.contrast = 2f
            generic.midGrayIn = 0.25f
            generic.midGrayOut = 0.35f
            generic.hdrMax = 12f
            assertEquals(2f, generic.contrast)
            assertEquals(0.25f, generic.midGrayIn)
            assertEquals(0.35f, generic.midGrayOut)
            assertEquals(12f, generic.hdrMax)
        }
    }
}

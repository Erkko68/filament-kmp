package io.github.erkko68.filament

import io.github.erkko68.filament.testutils.FilamentTestFixture
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ToneMapperTest : FilamentTestFixture() {
    @Test
    fun testToneMapperInstantiationAndProperties() {
        val t1 = ToneMapper.Linear()
        assertNotNull(t1)

        val t2 = ToneMapper.ACES()
        assertNotNull(t2)

        val t3 = ToneMapper.ACESLegacy()
        assertNotNull(t3)

        val t4 = ToneMapper.Filmic()
        assertNotNull(t4)

        val t5 = ToneMapper.PBRNeutralToneMapper()
        assertNotNull(t5)

        val t6 = ToneMapper.GT7ToneMapper()
        assertNotNull(t6)

        val t7 = ToneMapper.Agx(ToneMapper.Agx.AgxLook.NONE)
        assertNotNull(t7)

        val t8 = ToneMapper.Generic(1.5f, 0.18f, 0.22f, 10f)
        assertNotNull(t8)

        t8.contrast = 2.0f
        assertEquals(2.0f, t8.contrast)

        t8.midGrayIn = 0.25f
        assertEquals(0.25f, t8.midGrayIn)

        t8.midGrayOut = 0.35f
        assertEquals(0.35f, t8.midGrayOut)

        t8.hdrMax = 12f
        assertEquals(12f, t8.hdrMax)

        val t9 = ToneMapper.DisplayRange()
        assertNotNull(t9)
    }
}

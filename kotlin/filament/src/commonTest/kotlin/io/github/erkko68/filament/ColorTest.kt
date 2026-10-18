package io.github.erkko68.filament

import io.github.erkko68.filament.testutils.FilamentTestFixture
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ColorTest : FilamentTestFixture() {
    @Test
    fun srgbAndLinearConvert() {
        assertContentEquals(floatArrayOf(1f, 1f, 1f), Color.toLinear(RgbType.sRGB, floatArrayOf(1f, 1f, 1f)))
        assertEquals(0.5f, Color.toLinear(RgbType.LINEAR, floatArrayOf(0.5f, 0.5f, 0.5f))[0])
        // Alpha passes through untouched.
        assertEquals(0.5f, Color.toLinear(RgbaType.sRGB, floatArrayOf(1f, 1f, 1f, 0.5f))[3])
        assertEquals(0.75f, Color.toLinear(RgbaType.LINEAR, floatArrayOf(0.5f, 0.5f, 0.5f, 0.75f))[3])
        assertEquals(0.3f, Color.toLinear(floatArrayOf(0.5f, 0.5f, 0.5f, 0.3f))[3], 1e-6f)

        // Accurate sRGB round trip; the input isn't modified.
        val srgb = floatArrayOf(0.5f, 0.25f, 0.75f)
        val linear = Color.toLinear(srgb)
        assertEquals(0.5f, srgb[0])
        assertEquals(0.2140f, linear[0], 1e-3f)
        Color.toSRGB(linear).forEachIndexed { i, c -> assertEquals(srgb[i], c, 1e-4f) }
    }

    @Test
    fun colorTemperatureShiftsTheHue() {
        // Low temperatures are warm (red over blue), high ones cool.
        for (k in listOf(Color::cct, Color::illuminantD)) {
            val warm = k(4000f)
            val cool = k(10000f)
            assertTrue(warm[0] > warm[2], "4000K should be warm: ${warm.toList()}")
            assertTrue(cool[2] > cool[0], "10000K should be cool: ${cool.toList()}")
        }
    }

    @Test
    fun absorptionFollowsTheTransmittedColor() {
        // White transmits everything: nothing is absorbed.
        Color.absorptionAtDistance(floatArrayOf(1f, 1f, 1f), 1f).forEach { assertEquals(0f, it, 1e-6f) }
        val absorption = Color.absorptionAtDistance(floatArrayOf(0.5f, 1f, 1f), 1f)
        assertTrue(absorption[0] > 0f && absorption[1] == 0f, absorption.toList().toString())
    }
}

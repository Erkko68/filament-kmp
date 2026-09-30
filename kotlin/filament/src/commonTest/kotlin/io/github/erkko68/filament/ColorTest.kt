package io.github.erkko68.filament

import io.github.erkko68.filament.testutils.FilamentTestFixture
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ColorTest : FilamentTestFixture() {
    @Test
    fun testToLinear() {
        val white = floatArrayOf(1f, 1f, 1f)
        // 1.0f in sRGB is exactly 1.0f in linear
        assertEquals(white.toList(), Color.toLinear(RgbType.sRGB, white).toList())
        assertEquals(0.5f, Color.toLinear(RgbType.LINEAR, floatArrayOf(0.5f, 0.5f, 0.5f))[0])

        assertEquals(0.5f, Color.toLinear(RgbaType.sRGB, floatArrayOf(1f, 1f, 1f, 0.5f))[3])
        assertEquals(0.75f, Color.toLinear(RgbaType.LINEAR, floatArrayOf(0.5f, 0.5f, 0.5f, 0.75f))[3])

        // Accurate sRGB round trip; the input isn't modified.
        val srgb = floatArrayOf(0.5f, 0.25f, 0.75f)
        val linear = Color.toLinear(srgb)
        assertEquals(0.5f, srgb[0])
        assertEquals(0.2140f, linear[0], 1e-3f)
        Color.toSRGB(linear).forEachIndexed { i, c -> assertEquals(srgb[i], c, 1e-4f) }
        assertEquals(0.3f, Color.toLinear(floatArrayOf(0.5f, 0.5f, 0.5f, 0.3f))[3], 1e-6f)
        assertEquals(4, Color.toSRGB(floatArrayOf(0.5f, 0.5f, 0.5f, 0.3f)).size)

        assertEquals(3, Color.cct(6500f).size)
        assertEquals(3, Color.illuminantD(6500f).size)
        // White light stays white: no absorption.
        Color.absorptionAtDistance(white, 1f).forEach { assertEquals(0f, it, 1e-6f) }
        assertTrue(Color.absorptionAtDistance(floatArrayOf(0.5f, 1f, 1f), 1f)[0] > 0f)
    }
}

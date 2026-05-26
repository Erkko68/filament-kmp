package io.github.erkko68.filament

import io.github.erkko68.filament.testutils.FilamentTestFixture
import kotlin.test.Test
import kotlin.test.assertEquals

class ColorsTest : FilamentTestFixture() {
    @Test
    fun testToLinear() {
        val r1 = Colors.toLinear(Colors.RgbType.SRGB, 1f, 1f, 1f)
        assertEquals(3, r1.size)
        // 1.0f in sRGB is exactly 1.0f in linear
        assertEquals(1f, r1[0])
        assertEquals(1f, r1[1])
        assertEquals(1f, r1[2])

        val r2 = Colors.toLinear(Colors.RgbType.LINEAR, floatArrayOf(0.5f, 0.5f, 0.5f))
        assertEquals(3, r2.size)
        assertEquals(0.5f, r2[0])

        val r3 = Colors.toLinear(Colors.RgbaType.SRGB, 1f, 1f, 1f, 0.5f)
        assertEquals(4, r3.size)
        assertEquals(0.5f, r3[3])

        val r4 = Colors.toLinear(Colors.RgbaType.LINEAR, floatArrayOf(0.5f, 0.5f, 0.5f, 0.75f))
        assertEquals(4, r4.size)
        assertEquals(0.75f, r4[3])

        val r5 = Colors.toLinear(Colors.Conversion.ACCURATE, floatArrayOf(1f, 1f, 1f))
        assertEquals(3, r5.size)

        val cct = Colors.cct(6500f)
        assertEquals(3, cct.size)

        val ill = Colors.illuminantD(6500f)
        assertEquals(3, ill.size)
    }
}

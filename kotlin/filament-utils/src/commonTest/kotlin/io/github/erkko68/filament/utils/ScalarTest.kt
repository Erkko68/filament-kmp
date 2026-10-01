package io.github.erkko68.filament.utils

import kotlin.test.Test
import kotlin.test.assertEquals

class ScalarTest {
    @Test
    fun clampSaturateMix() {
        assertEquals(1f, clamp(2f, 0f, 1f)); assertEquals(0f, clamp(-1f, 0f, 1f)); assertEquals(0.5f, clamp(0.5f, 0f, 1f))
        assertEquals(3, clamp(5, 0, 3)); assertEquals(0, clamp(-5, 0, 3))
        assertEquals(1f, clamp(2f.h, 0f.h, 1f.h).toFloat())
        assertEquals(1f, saturate(2f)); assertEquals(0f, saturate(-2f)); assertEquals(0f, saturate((-2f).h).toFloat())
        assertEquals(2.5f, mix(2f, 3f, 0.5f)); assertEquals(2.5f, mix(2f.h, 3f.h, 0.5f.h).toFloat())
    }

    @Test
    fun anglesAndPowers() {
        assertEquals(180f, degrees(FPI), 1e-4f); assertEquals(FPI, radians(180f), 1e-6f)
        assertEquals(9f, sqr(3f)); assertEquals(9f, sqr(3f.h).toFloat()); assertEquals(8f, pow(2f, 3f))
    }

    @Test
    fun fractIsInUnitRangeLikeGlsl() {
        assertEquals(0.25f, fract(1.25f))
        assertEquals(0.75f, fract(-1.25f))
        assertEquals(0f, fract(3f))
    }
}

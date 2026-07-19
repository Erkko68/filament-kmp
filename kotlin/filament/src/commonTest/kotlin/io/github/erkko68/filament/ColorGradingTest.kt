package io.github.erkko68.filament

import io.github.erkko68.filament.testutils.FilamentTestFixture
import io.github.erkko68.filament.testsupport.TestEnv
import io.github.erkko68.filament.testsupport.TestTarget
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ColorGradingTest : FilamentTestFixture() {
    @Test
    fun testColorGradingBuilder() {
        val tm = ToneMapper.Linear()
        val grading = ColorGrading.Builder()
            .quality(ColorGrading.QualityLevel.HIGH)
            .format(ColorGrading.LutFormat.INTEGER)
            .dimensions(32)
            .toneMapper(tm)
            .luminanceScaling(true)
            .gamutMapping(true)
            .exposure(1.0f)
            .nightAdaptation(0.5f)
            .whiteBalance(6500f, 0f)
            .channelMixer(floatArrayOf(1f, 0f, 0f), floatArrayOf(0f, 1f, 0f), floatArrayOf(0f, 0f, 1f))
            .shadowsMidtonesHighlights(floatArrayOf(1f, 1f, 1f, 0f), floatArrayOf(1f, 1f, 1f, 0f), floatArrayOf(1f, 1f, 1f, 0f), floatArrayOf(1f, 1f, 1f, 0f))
            .slopeOffsetPower(floatArrayOf(1f, 1f, 1f), floatArrayOf(0f, 0f, 0f), floatArrayOf(1f, 1f, 1f))
            .contrast(1.0f)
            .vibrance(0.5f)
            .saturation(1.0f)
            .curves(floatArrayOf(0f, 0f, 1f), floatArrayOf(0f, 0f, 1f), floatArrayOf(0f, 0f, 1f))
            .fastMath(false)
            .build(engine)
        
        assertNotNull(grading)
        assertTrue(engine.isValidColorGrading(grading))

        engine.destroyColorGrading(grading)
    }

    @Test
    fun testCustomLut() {
        // ColorGrading.Builder.customLut is not bound in filament.js.
        if (TestEnv.target == TestTarget.JS) return
        val dim = 16
        // Identity-ish LUT data: any values are fine, we only exercise the binding.
        val lut = FloatArray(dim * dim * dim * 3) { (it % 3) * 0.5f }
        val grading = ColorGrading.Builder()
            .customLut(lut, dim)
            .build(engine)
        assertNotNull(grading)
        assertTrue(engine.isValidColorGrading(grading))
        engine.destroyColorGrading(grading)
    }
}

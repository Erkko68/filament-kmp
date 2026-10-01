package io.github.erkko68.filament

import io.github.erkko68.filament.testutils.FilamentTestFixture
import kotlin.test.Test
import kotlin.test.assertEquals
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
        assertTrue(engine.isValid(grading))

        engine.destroy(grading)
    }

    @Test
    fun testCustomLut() {
        val dim = 16
        // Identity-ish LUT data: any values are fine, we only exercise the binding.
        val lut = FloatArray(dim * dim * dim * 3) { (it % 3) * 0.5f }
        val grading = ColorGrading.Builder()
            .customLut(lut, dim)
            .build(engine)
        assertNotNull(grading)
        assertTrue(engine.isValid(grading))
        engine.destroy(grading)
    }

    @Test
    fun testOutputColorSpace() {
        val linear = Gamut.Rec709 - TransferFunction.Linear - WhitePoint.D65
        assertEquals(ColorSpace(Gamut.Rec709.primaries, TransferFunction(1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0), WhitePoint.D65), linear)
        for (colorSpace in listOf(linear, Gamut.Rec709 - TransferFunction.sRGB - WhitePoint.D65)) {
            val grading = ColorGrading.Builder().outputColorSpace(colorSpace).build(engine)
            assertTrue(engine.isValid(grading))
            engine.destroy(grading)
        }
    }
}

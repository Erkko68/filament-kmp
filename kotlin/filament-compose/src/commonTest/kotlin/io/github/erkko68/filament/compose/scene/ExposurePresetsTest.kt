package io.github.erkko68.filament.compose.scene

import kotlin.math.log2
import kotlin.math.roundToInt
import kotlin.test.Test
import kotlin.test.assertEquals

/** Each [Exposure] preset meters at the EV100 its KDoc gives: EV100 = log2(N² / t · 100 / S). */
class ExposurePresetsTest {

    private val Exposure.ev100 get() = log2(aperture * aperture / shutterSpeed * 100f / sensitivity)

    @Test
    fun presetsMeterAtTheirEv100() {
        assertEquals(15, Exposure.Sunny.ev100.roundToInt())
        assertEquals(12, Exposure.Overcast.ev100.roundToInt())
        assertEquals(6, Exposure.Indoor.ev100.roundToInt())
        assertEquals(Exposure(), Exposure.Sunny, "the default is the sunny exposure")
    }
}

package io.github.erkko68.filament

import kotlin.test.Test
import kotlin.test.assertEquals

class MaterialTest {
    @Test
    fun testUserVariantFlagsMakeUpAll() {
        val bits = with(UserVariantFilterBit) {
            listOf(DIRECTIONAL_LIGHTING, DYNAMIC_LIGHTING, SHADOW_RECEIVER, SKINNING, FOG, VSM, SSR, STE)
        }
        assertEquals(bits.size, bits.distinct().size)
        assertEquals(UserVariantFilterBit.ALL, bits.fold(0) { acc, b -> acc or b })
    }
}

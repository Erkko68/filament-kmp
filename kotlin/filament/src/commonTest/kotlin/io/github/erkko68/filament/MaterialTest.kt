package io.github.erkko68.filament

import io.github.erkko68.filament.testutils.FilamentTestFixture
import kotlin.test.Test
import kotlin.test.assertNull
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

class MaterialPayloadTest : FilamentTestFixture() {
    // Filament's parser panics on these; the builder must turn them away first.
    @Test
    fun testInvalidPayloadsBuildNothing() {
        assertNull(Material.Builder().payload("not a material package".encodeToByteArray()).build(engine))
        assertNull(Material.Builder().payload("SREV".encodeToByteArray()).build(engine))
    }
}

package io.github.erkko68.filament

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

/** Plain Kotlin value types: equality, aliases and derived fields that never reach the native side. */
class ValueTypesTest {
    // Equal to itself and to a copy (with the same hash), different from each variant and from other types.
    private fun <T : Any> assertValueEquality(make: () -> T, vararg different: T) {
        val value = make()
        assertTrue(value.equals(value))
        assertEquals(make(), value)
        assertEquals(make().hashCode(), value.hashCode())
        different.forEach { assertNotEquals(it, value) }
        assertFalse(value.equals("other type"))
        assertFalse(value.equals(null))
    }

    @Test
    fun boxEquality() {
        assertValueEquality(
            { Box(floatArrayOf(1f, 2f, 3f), floatArrayOf(4f, 5f, 6f)) },
            Box(floatArrayOf(0f, 2f, 3f), floatArrayOf(4f, 5f, 6f)),
            Box(floatArrayOf(1f, 2f, 3f), floatArrayOf(4f, 5f, 0f)),
        )
        assertValueEquality(
            { Aabb(floatArrayOf(1f, 2f, 3f), floatArrayOf(4f, 5f, 6f)) },
            Aabb(floatArrayOf(0f, 2f, 3f), floatArrayOf(4f, 5f, 6f)),
            Aabb(floatArrayOf(1f, 2f, 3f), floatArrayOf(4f, 5f, 0f)),
        )
    }

    @Test
    fun colorSpaceEquality() {
        val r = floatArrayOf(0.64f, 0.33f)
        val g = floatArrayOf(0.3f, 0.6f)
        val b = floatArrayOf(0.15f, 0.06f)
        val other = floatArrayOf(0.5f, 0.5f)
        assertValueEquality({ Primaries(r, g, b) }, Primaries(other, g, b), Primaries(r, other, b), Primaries(r, g, other))
        assertValueEquality(
            { Gamut.Rec709 - TransferFunction.sRGB - WhitePoint.D65 },
            Gamut(other, g, b) - TransferFunction.sRGB - WhitePoint.D65,
            Gamut.Rec709 - TransferFunction.Linear - WhitePoint.D65,
            Gamut.Rec709 - TransferFunction.sRGB - other,
        )
    }

    @Test
    fun viewportEmptiness() {
        assertTrue(Viewport().empty())
        assertTrue(Viewport(width = 4).empty())
        assertTrue(Viewport(height = 4).empty())
        assertFalse(Viewport(1, 2, 4, 8).empty())
    }

    @Test
    fun samplerFiltersAndAnisotropy() {
        assertEquals(TextureSampler.MinFilter.NEAREST, TextureSampler(TextureSampler.MagFilter.NEAREST).minFilter)
        assertEquals(TextureSampler.MinFilter.LINEAR, TextureSampler(TextureSampler.MagFilter.LINEAR).minFilter)

        val sampler = TextureSampler()
        sampler.anisotropy = 4f
        assertEquals(4f, sampler.anisotropy)
        // Rounded down to a power of two, capped at 128; zero disables it.
        sampler.anisotropy = 7f
        assertEquals(4f, sampler.anisotropy)
        sampler.anisotropy = 1000f
        assertEquals(128f, sampler.anisotropy)
        sampler.anisotropy = 0f
        assertEquals(1f, sampler.anisotropy)

        sampler.setCompareMode(TextureSampler.CompareMode.COMPARE_TO_TEXTURE)
        assertEquals(TextureSampler.CompareMode.COMPARE_TO_TEXTURE, sampler.compareMode)
        assertEquals(TextureSampler.CompareFunc.LE, sampler.compareFunc)
    }

    @Test
    fun legacyAliases() {
        val custom = VertexBuffer.VertexAttribute.entries.filter { it.name.startsWith("CUSTOM") }
        val morph = with(VertexBuffer.VertexAttribute) {
            listOf(
                MORPH_POSITION_0, MORPH_POSITION_1, MORPH_POSITION_2, MORPH_POSITION_3,
                MORPH_TANGENTS_0, MORPH_TANGENTS_1, MORPH_TANGENTS_2, MORPH_TANGENTS_3,
            )
        }
        assertEquals(custom, morph)
        assertSame(RenderTarget.AttachmentPoint.COLOR, RenderTarget.AttachmentPoint.COLOR0)
    }
}

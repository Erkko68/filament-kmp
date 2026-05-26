package io.github.erkko68.filament

import io.github.erkko68.filament.testutils.FilamentTestFixture
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TextureSamplerTest : FilamentTestFixture() {
    @Test
    fun defaultConstructor() {
        val s = TextureSampler()
        // Should not crash — proves constructor binding works
        assertTrue(true)
    }

    @Test
    fun magFilterConstructor() {
        val s = TextureSampler(TextureSampler.MagFilter.LINEAR)
        assertEquals(TextureSampler.MagFilter.LINEAR, s.magFilter)
    }

    @Test
    fun magFilterAndWrapModeConstructor() {
        val s = TextureSampler(TextureSampler.MagFilter.LINEAR, TextureSampler.WrapMode.REPEAT)
        assertEquals(TextureSampler.MagFilter.LINEAR, s.magFilter)
        assertEquals(TextureSampler.WrapMode.REPEAT, s.wrapModeS)
    }

    @Test
    fun fullConstructor() {
        val s = TextureSampler(
            TextureSampler.MinFilter.LINEAR,
            TextureSampler.MagFilter.LINEAR,
            TextureSampler.WrapMode.REPEAT,
            TextureSampler.WrapMode.CLAMP_TO_EDGE,
            TextureSampler.WrapMode.MIRRORED_REPEAT
        )
        assertEquals(TextureSampler.MinFilter.LINEAR, s.minFilter)
        assertEquals(TextureSampler.MagFilter.LINEAR, s.magFilter)
        assertEquals(TextureSampler.WrapMode.REPEAT, s.wrapModeS)
        assertEquals(TextureSampler.WrapMode.CLAMP_TO_EDGE, s.wrapModeT)
        assertEquals(TextureSampler.WrapMode.MIRRORED_REPEAT, s.wrapModeR)
    }

    @Test
    fun compareModeConstructors() {
        val s1 = TextureSampler(TextureSampler.CompareMode.NONE)
        assertEquals(TextureSampler.CompareMode.NONE, s1.compareMode)

        val s2 = TextureSampler(TextureSampler.CompareMode.COMPARE_TO_TEXTURE, TextureSampler.CompareFunction.LESS)
        assertEquals(TextureSampler.CompareMode.COMPARE_TO_TEXTURE, s2.compareMode)
        assertEquals(TextureSampler.CompareFunction.LESS, s2.compareFunction)
    }

    @Test
    fun minFilterRoundTrip() {
        val s = TextureSampler()
        s.minFilter = TextureSampler.MinFilter.LINEAR_MIPMAP_LINEAR
        assertEquals(TextureSampler.MinFilter.LINEAR_MIPMAP_LINEAR, s.minFilter)
    }

    @Test
    fun magFilterRoundTrip() {
        val s = TextureSampler()
        s.magFilter = TextureSampler.MagFilter.LINEAR
        assertEquals(TextureSampler.MagFilter.LINEAR, s.magFilter)
    }

    @Test
    fun wrapModeRoundTrip() {
        val s = TextureSampler()
        s.wrapModeS = TextureSampler.WrapMode.CLAMP_TO_EDGE
        s.wrapModeT = TextureSampler.WrapMode.REPEAT
        s.wrapModeR = TextureSampler.WrapMode.MIRRORED_REPEAT
        assertEquals(TextureSampler.WrapMode.CLAMP_TO_EDGE, s.wrapModeS)
        assertEquals(TextureSampler.WrapMode.REPEAT, s.wrapModeT)
        assertEquals(TextureSampler.WrapMode.MIRRORED_REPEAT, s.wrapModeR)
    }

    @Test
    fun anisotropyRoundTrip() {
        val s = TextureSampler()
        s.anisotropy = 4.0f
        assertEquals(4.0f, s.anisotropy)
    }

    @Test
    fun compareFunctionRoundTrip() {
        val s = TextureSampler()
        s.compareMode = TextureSampler.CompareMode.COMPARE_TO_TEXTURE
        s.compareFunction = TextureSampler.CompareFunction.GREATER_EQUAL
        assertEquals(TextureSampler.CompareMode.COMPARE_TO_TEXTURE, s.compareMode)
        assertEquals(TextureSampler.CompareFunction.GREATER_EQUAL, s.compareFunction)
    }
}

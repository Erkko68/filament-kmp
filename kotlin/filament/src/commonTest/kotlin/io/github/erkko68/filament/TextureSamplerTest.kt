package io.github.erkko68.filament

import io.github.erkko68.filament.testutils.FilamentTestFixture
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TextureSamplerTest : FilamentTestFixture() {
    @Test
    fun defaultConstructor() {
        val s = TextureSampler()
        // C++'s SamplerParams{} defaults.
        assertEquals(TextureSampler.MinFilter.NEAREST, s.minFilter)
        assertEquals(TextureSampler.MagFilter.NEAREST, s.magFilter)
        assertEquals(TextureSampler.WrapMode.CLAMP_TO_EDGE, s.wrapModeS)
        assertEquals(1f, s.anisotropy)
        assertEquals(TextureSampler.CompareMode.NONE, s.compareMode)
        assertEquals(TextureSampler.CompareFunc.LE, s.compareFunc)
    }

    @Test
    fun minMagWrapConstructor() {
        val s = TextureSampler(
            TextureSampler.MinFilter.LINEAR_MIPMAP_LINEAR,
            TextureSampler.MagFilter.LINEAR,
            TextureSampler.WrapMode.MIRRORED_REPEAT
        )
        assertEquals(TextureSampler.MinFilter.LINEAR_MIPMAP_LINEAR, s.minFilter)
        assertEquals(TextureSampler.MagFilter.LINEAR, s.magFilter)
        assertEquals(TextureSampler.WrapMode.MIRRORED_REPEAT, s.wrapModeS)
        assertEquals(TextureSampler.WrapMode.MIRRORED_REPEAT, s.wrapModeT)
        assertEquals(TextureSampler.WrapMode.MIRRORED_REPEAT, s.wrapModeR)
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

        val s2 = TextureSampler(TextureSampler.CompareMode.COMPARE_TO_TEXTURE, TextureSampler.CompareFunc.L)
        assertEquals(TextureSampler.CompareMode.COMPARE_TO_TEXTURE, s2.compareMode)
        assertEquals(TextureSampler.CompareFunc.L, s2.compareFunc)
    }
}

package io.github.erkko68.filament.compose.scene

import io.github.erkko68.filament.QualityLevel
import io.github.erkko68.filament.ToneMapper
import io.github.erkko68.filament.View
import io.github.erkko68.filament.Dithering as FilamentDithering
import io.github.erkko68.filament.AntiAliasing as FilamentAntiAliasing
import io.github.erkko68.filament.compose.testutils.ComposeTestFixture
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * Verifies [PostProcessing.applyTo] pushes the expected option values onto a (NOOP) [View] and
 * manages the allocated [io.github.erkko68.filament.ColorGrading]. NOOP exercises the full binding
 * path for every option struct and for ColorGrading construction (cf. core `ViewOptionsRoundTripTest`
 * / `ColorGradingTest`), so no GPU is needed. Views are freed by the engine on fixture teardown.
 */
class PostProcessingApplyTest : ComposeTestFixture() {

    private fun newView(): View = engine.createView()

    @Test
    fun enabledFlagAppliesToView() {
        val view = newView()
        PostProcessing(enabled = false).applyTo(view, null)
        assertFalse(view.isPostProcessingEnabled)
        PostProcessing(enabled = true).applyTo(view, null)
        assertTrue(view.isPostProcessingEnabled)
    }

    @Test
    fun effectsEnableAndRoundTrip() {
        val view = newView()
        PostProcessing(
            // resolution drives the mip-chain length, so it must be large enough to support `levels`
            // (Filament clamps levels to the chain the resolution allows) — mirrors core ViewOptionsRoundTripTest.
            bloom = Bloom(strength = 0.42f, levels = 8, resolution = 384),
            vignette = Vignette(midPoint = 0.3f),
            fog = Fog(density = 0.25f),
            ambientOcclusion = AmbientOcclusion(radius = 0.5f),
            screenSpaceReflections = ScreenSpaceReflections(maxDistance = 5f),
            depthOfField = DepthOfField(cocScale = 2f),
            // homogeneousScaling must be true for NOOP to keep dynamic resolution enabled
            // (non-homogeneous scaling needs backend support NOOP lacks) — mirrors core round-trip test.
            dynamicResolution = DynamicResolution(minScale = 0.5f, maxScale = 0.5f, homogeneousScaling = true),
        ).applyTo(view, null)

        assertTrue(view.bloomOptions.enabled)
        assertEquals(0.42f, view.bloomOptions.strength)
        assertEquals(8, view.bloomOptions.levels)
        assertTrue(view.vignetteOptions.enabled)
        assertEquals(0.3f, view.vignetteOptions.midPoint)
        assertTrue(view.fogOptions.enabled)
        assertEquals(0.25f, view.fogOptions.density)
        assertTrue(view.ambientOcclusionOptions.enabled)
        assertEquals(0.5f, view.ambientOcclusionOptions.radius)
        assertTrue(view.screenSpaceReflectionsOptions.enabled)
        assertEquals(5f, view.screenSpaceReflectionsOptions.maxDistance)
        assertTrue(view.depthOfFieldOptions.enabled)
        assertEquals(2f, view.depthOfFieldOptions.cocScale)
        assertTrue(view.dynamicResolutionOptions.enabled)
        assertContentEquals(floatArrayOf(0.5f, 0.5f), view.dynamicResolutionOptions.minScale)
    }

    @Test
    fun antiAliasingMapsEachMode() {
        val view = newView()
        PostProcessing(antiAliasing = AntiAliasing(msaaEnabled = true, msaaSampleCount = 8, fxaaEnabled = true, taaEnabled = true))
            .applyTo(view, null)
        assertTrue(view.multiSampleAntiAliasingOptions.enabled)
        assertEquals(8, view.multiSampleAntiAliasingOptions.sampleCount)
        assertEquals(FilamentAntiAliasing.FXAA, view.antiAliasing)
        assertTrue(view.temporalAntiAliasingOptions.enabled)

        PostProcessing(antiAliasing = AntiAliasing(msaaEnabled = false, fxaaEnabled = false, taaEnabled = false))
            .applyTo(view, null)
        assertFalse(view.multiSampleAntiAliasingOptions.enabled)
        assertEquals(FilamentAntiAliasing.NONE, view.antiAliasing)
        assertFalse(view.temporalAntiAliasingOptions.enabled)
    }

    @Test
    fun nullEffectsDisableThem() {
        val view = newView()
        // First enable, then re-apply an empty config: applyTo must clear, not leave stale state.
        PostProcessing(bloom = Bloom(), vignette = Vignette()).applyTo(view, null)
        PostProcessing().applyTo(view, null)
        assertFalse(view.bloomOptions.enabled)
        assertFalse(view.vignetteOptions.enabled)
        assertFalse(view.fogOptions.enabled)
        assertFalse(view.ambientOcclusionOptions.enabled)
        assertFalse(view.depthOfFieldOptions.enabled)
    }

    @Test
    fun colorGradingIsSetAndCleared() {
        val view = newView()
        val grading = ColorGrade(contrast = 1.2f).build(engine)
        PostProcessing(colorGrade = ColorGrade(contrast = 1.2f)).applyTo(view, grading)
        assertSame(grading, view.colorGrading)

        PostProcessing(colorGrade = null).applyTo(view, null)
        assertNull(view.colorGrading, "re-applying without a grading should clear it")
        engine.destroy(grading)
    }

    @Test
    fun ditheringAndRenderQualityApply() {
        val view = newView()
        PostProcessing(dithering = Dithering(FilamentDithering.NONE), renderQuality = RenderQuality(QualityLevel.LOW))
            .applyTo(view, null)
        assertEquals(FilamentDithering.NONE, view.dithering)
        assertEquals(QualityLevel.LOW, view.renderQuality.hdrColorBuffer)

        // Null restores the native defaults.
        PostProcessing().applyTo(view, null)
        assertEquals(FilamentDithering.TEMPORAL, view.dithering)
        assertEquals(QualityLevel.HIGH, view.renderQuality.hdrColorBuffer)
    }

    @Test
    fun everyToneMappingBuildsAColorGrading() {
        val toneMappings = listOf(
            ToneMapping.ACES, ToneMapping.ACESLegacy, ToneMapping.Filmic, ToneMapping.PBRNeutral, ToneMapping.GT7,
            ToneMapping.Linear, ToneMapping.DisplayRange, ToneMapping.Agx(ToneMapper.Agx.AgxLook.PUNCHY),
            ToneMapping.Generic(contrast = 1.4f),
        )
        for (toneMapping in toneMappings) {
            val grading = ColorGrade(toneMapping = toneMapping).build(engine)
            assertTrue(engine.isValid(grading), "$toneMapping")
            engine.destroy(grading)
        }
    }
}

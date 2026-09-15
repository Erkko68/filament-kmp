package io.github.erkko68.filament.compose.scene

import io.github.erkko68.filament.View
import io.github.erkko68.filament.compose.testutils.ComposeTestFixture
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
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
        PostProcessing(enabled = false).applyTo(view, engine)
        assertFalse(view.isPostProcessingEnabled)
        PostProcessing(enabled = true).applyTo(view, engine)
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
        ).applyTo(view, engine)

        assertTrue(view.bloomOptions.enabled)
        assertEquals(0.42f, view.bloomOptions.strength, 1e-6f)
        assertEquals(8, view.bloomOptions.levels)
        assertTrue(view.vignetteOptions.enabled)
        assertEquals(0.3f, view.vignetteOptions.midPoint, 1e-6f)
        assertTrue(view.fogOptions.enabled)
        assertEquals(0.25f, view.fogOptions.density, 1e-6f)
        assertTrue(view.ambientOcclusionOptions.enabled)
        assertEquals(0.5f, view.ambientOcclusionOptions.radius, 1e-6f)
        assertTrue(view.screenSpaceReflectionsOptions.enabled)
        assertEquals(5f, view.screenSpaceReflectionsOptions.maxDistance, 1e-6f)
        assertTrue(view.depthOfFieldOptions.enabled)
        assertEquals(2f, view.depthOfFieldOptions.cocScale, 1e-6f)
        assertTrue(view.dynamicResolutionOptions.enabled)
        assertEquals(0.5f, view.dynamicResolutionOptions.minScale, 1e-6f)
    }

    @Test
    fun antiAliasingMapsEachMode() {
        val view = newView()
        PostProcessing(antiAliasing = AntiAliasing(msaaEnabled = true, msaaSampleCount = 8, fxaaEnabled = true, taaEnabled = true))
            .applyTo(view, engine)
        assertTrue(view.multiSampleAntiAliasingOptions.enabled)
        assertEquals(8, view.multiSampleAntiAliasingOptions.sampleCount)
        assertEquals(View.AntiAliasing.FXAA, view.antiAliasing)
        assertTrue(view.temporalAntiAliasingOptions.enabled)

        PostProcessing(antiAliasing = AntiAliasing(msaaEnabled = false, fxaaEnabled = false, taaEnabled = false))
            .applyTo(view, engine)
        assertFalse(view.multiSampleAntiAliasingOptions.enabled)
        assertEquals(View.AntiAliasing.NONE, view.antiAliasing)
        assertFalse(view.temporalAntiAliasingOptions.enabled)
    }

    @Test
    fun nullEffectsDisableThem() {
        val view = newView()
        // First enable, then re-apply an empty config: applyTo must clear, not leave stale state.
        PostProcessing(bloom = Bloom(), vignette = Vignette()).applyTo(view, engine)
        PostProcessing().applyTo(view, engine)
        assertFalse(view.bloomOptions.enabled)
        assertFalse(view.vignetteOptions.enabled)
        assertFalse(view.fogOptions.enabled)
        assertFalse(view.ambientOcclusionOptions.enabled)
        assertFalse(view.depthOfFieldOptions.enabled)
    }

    @Test
    fun colorGradeAllocatesAndClears() {
        val view = newView()
        val grading = PostProcessing(colorGrade = ColorGrade(contrast = 1.2f)).applyTo(view, engine)
        assertNotNull(grading, "a ColorGrading should be allocated when colorGrade is set")
        engine.destroyColorGrading(grading)

        val none = PostProcessing(colorGrade = null).applyTo(view, engine)
        assertNull(none, "no ColorGrading should be allocated when colorGrade is null")
    }
}

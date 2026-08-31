package io.github.erkko68.filament

import io.github.erkko68.filament.testsupport.TestEnv
import io.github.erkko68.filament.testsupport.TestTarget
import io.github.erkko68.filament.testutils.FilamentTestFixture
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Round-trips EVERY field of each View option struct through the C bridge
 * (set on the view -> read back) to catch a dropped or swapped field in the
 * bridge's field-by-field copy loops. Distinct in-range values per field so a
 * swap (X written into Y) is detectable. CPU state, runs on NOOP / all targets.
 */
class ViewOptionsRoundTripTest : FilamentTestFixture() {
    private lateinit var view: View

    private fun setUpView() {
        view = engine.createView()
    }

    @Test
    fun bloomOptionsRoundTrip() {
        setUpView()
        view.bloomOptions = View.BloomOptions().apply {
            enabled = true; levels = 5; resolution = 384; strength = 0.7f
            threshold = false; dirtStrength = 0.3f; quality = View.Quality.HIGH
            lensFlare = true; starburst = false; chromaticAberration = 0.1f
            ghostCount = 3; ghostSpacing = 0.6f; ghostThreshold = 2f
            haloRadius = 0.4f; haloThickness = 0.2f; haloThreshold = 3f
            highlight = 1000f; blendMode = View.BloomOptions.BlendMode.INTERPOLATE
        }
        view.bloomOptions.run {
            assertTrue(enabled); assertEquals(5, levels); assertEquals(384, resolution)
            assertEq(0.7f, strength, "strength"); assertEquals(false, threshold)
            if (!webOptionGap) assertEq(0.3f, dirtStrength, "dirtStrength")
            assertEquals(View.Quality.HIGH, quality)
            assertTrue(lensFlare); assertEquals(false, starburst)
            assertEq(0.1f, chromaticAberration, "chromaticAberration"); assertEquals(3, ghostCount)
            assertEq(0.6f, ghostSpacing, "ghostSpacing"); assertEq(2f, ghostThreshold, "ghostThreshold")
            assertEq(0.4f, haloRadius, "haloRadius"); assertEq(0.2f, haloThickness, "haloThickness")
            assertEq(3f, haloThreshold, "haloThreshold"); assertEq(1000f, highlight, "highlight")
            assertEquals(View.BloomOptions.BlendMode.INTERPOLATE, blendMode)
        }
    }

    @Test
    fun fogOptionsRoundTrip() {
        setUpView()
        view.fogOptions = View.FogOptions().apply {
            enabled = true; distance = 10f; density = 0.5f; height = 2f
            heightFalloff = 1.5f; color = floatArrayOf(0.1f, 0.2f, 0.3f)
            cutOffDistance = 100f; maximumOpacity = 0.8f; inScatteringStart = 5f
            inScatteringSize = 3f; fogColorFromIbl = true
        }
        view.fogOptions.run {
            assertTrue(enabled); assertEq(10f, distance, "distance"); assertEq(0.5f, density, "density")
            assertEq(2f, height, "height"); assertEq(1.5f, heightFalloff, "heightFalloff")
            assertArr(floatArrayOf(0.1f, 0.2f, 0.3f), color)
            assertEq(100f, cutOffDistance, "cutOffDistance"); assertEq(0.8f, maximumOpacity, "maximumOpacity")
            assertEq(5f, inScatteringStart, "inScatteringStart"); assertEq(3f, inScatteringSize, "inScatteringSize")
            assertTrue(fogColorFromIbl)
        }
    }

    @Test
    fun depthOfFieldOptionsRoundTrip() {
        setUpView()
        view.depthOfFieldOptions = View.DepthOfFieldOptions().apply {
            enabled = true; cocScale = 2f; cocAspectRatio = 1.5f; maxApertureDiameter = 0.01f
            filter = View.DepthOfFieldOptions.Filter.MEDIAN; nativeResolution = true
            foregroundRingCount = 3; backgroundRingCount = 4; fastGatherRingCount = 2
            maxForegroundCOC = 5; maxBackgroundCOC = 6
        }
        view.depthOfFieldOptions.run {
            assertTrue(enabled); assertEq(2f, cocScale, "cocScale"); assertEq(1.5f, cocAspectRatio, "cocAspectRatio")
            assertEq(0.01f, maxApertureDiameter, "maxApertureDiameter")
            assertEquals(View.DepthOfFieldOptions.Filter.MEDIAN, filter); assertTrue(nativeResolution)
            assertEquals(3, foregroundRingCount); assertEquals(4, backgroundRingCount)
            assertEquals(2, fastGatherRingCount); assertEquals(5, maxForegroundCOC)
            assertEquals(6, maxBackgroundCOC)
        }
    }

    @Test
    fun vignetteOptionsRoundTrip() {
        setUpView()
        view.vignetteOptions = View.VignetteOptions().apply {
            enabled = true; midPoint = 0.5f; roundness = 0.6f; feather = 0.4f
            color = floatArrayOf(0.1f, 0.2f, 0.3f, 1f)
        }
        view.vignetteOptions.run {
            assertTrue(enabled); assertEq(0.5f, midPoint, "midPoint"); assertEq(0.6f, roundness, "roundness")
            assertEq(0.4f, feather, "feather"); assertArr(floatArrayOf(0.1f, 0.2f, 0.3f, 1f), color)
        }
    }

    @Test
    fun temporalAntiAliasingOptionsRoundTrip() {
        setUpView()
        view.temporalAntiAliasingOptions = View.TemporalAntiAliasingOptions().apply {
            feedback = 0.12f; lodBias = -1f; sharpness = 0.3f; enabled = true
            upscaling = 1f; filterHistory = true; filterInput = false; useYCoCg = true
            hdr = false
            boxType = View.TemporalAntiAliasingOptions.BoxType.AABB_VARIANCE
            boxClipping = View.TemporalAntiAliasingOptions.BoxClipping.NONE
            jitterPattern = View.TemporalAntiAliasingOptions.JitterPattern.UNIFORM_HELIX_X4
            varianceGamma = 1.1f; preventFlickering = true; historyReprojection = false
        }
        view.temporalAntiAliasingOptions.run {
            assertEq(0.12f, feedback, "feedback"); assertEq(-1f, lodBias, "lodBias"); assertEq(0.3f, sharpness, "sharpness")
            assertTrue(enabled); assertEq(1f, upscaling, "upscaling"); assertTrue(filterHistory)
            assertEquals(false, filterInput); assertTrue(useYCoCg); assertEquals(false, hdr)
            assertEquals(View.TemporalAntiAliasingOptions.BoxType.AABB_VARIANCE, boxType)
            assertEquals(View.TemporalAntiAliasingOptions.BoxClipping.NONE, boxClipping)
            assertEquals(View.TemporalAntiAliasingOptions.JitterPattern.UNIFORM_HELIX_X4, jitterPattern)
            assertEq(1.1f, varianceGamma, "varianceGamma"); assertTrue(preventFlickering)
            assertEquals(false, historyReprojection)
        }
    }

    @Test
    fun screenSpaceReflectionsOptionsRoundTrip() {
        setUpView()
        view.screenSpaceReflectionsOptions = View.ScreenSpaceReflectionsOptions().apply {
            enabled = true; thickness = 0.1f; bias = 0.2f; maxDistance = 3f; stride = 2f
        }
        view.screenSpaceReflectionsOptions.run {
            assertTrue(enabled); assertEq(0.1f, thickness, "thickness"); assertEq(0.2f, bias, "bias")
            assertEq(3f, maxDistance, "maxDistance"); assertEq(2f, stride, "stride")
        }
    }

    @Test
    fun ambientOcclusionOptionsRoundTrip() {
        setUpView()
        view.ambientOcclusionOptions = View.AmbientOcclusionOptions().apply {
            radius = 0.5f; bias = 0.001f; intensity = 1.5f; power = 1.2f
            minHorizonAngleRad = 0.3f; quality = View.Quality.HIGH; lowPassFilter = View.Quality.MEDIUM
            upsampling = View.Quality.LOW; enabled = true; bentNormals = true
            bilateralThreshold = 0.05f; resolution = 0.5f
            aoType = View.AmbientOcclusionOptions.AmbientOcclusionType.GTAO
            ssct = View.AmbientOcclusionOptions.Ssct().apply {
                enabled = true; lightConeRad = 1.0f; shadowDistance = 0.4f
                contactDistanceMax = 1.2f; intensity = 0.9f
                lightDirection = floatArrayOf(0f, -1f, 0f); depthBias = 0.02f
                depthSlopeBias = 0.03f; sampleCount = 6; rayCount = 2
            }
            gtao = View.AmbientOcclusionOptions.Gtao().apply {
                sampleSliceCount = 6; sampleStepsPerSlice = 5; thicknessHeuristic = 0.02f
                useVisibilityBitmasks = true; constThickness = 0.75f; linearThickness = true
            }
        }
        view.ambientOcclusionOptions.run {
            assertEq(0.5f, radius, "radius"); assertEq(0.001f, bias, "bias"); assertEq(1.5f, intensity, "intensity")
            assertEq(1.2f, power, "power"); assertEq(0.3f, minHorizonAngleRad, "minHorizonAngleRad")
            assertEquals(View.Quality.HIGH, quality); assertEquals(View.Quality.MEDIUM, lowPassFilter)
            assertEquals(View.Quality.LOW, upsampling); assertTrue(enabled); assertTrue(bentNormals)
            assertEq(0.05f, bilateralThreshold, "bilateralThreshold"); assertEq(0.5f, resolution, "resolution")
            assertEquals(View.AmbientOcclusionOptions.AmbientOcclusionType.GTAO, aoType)
            if (!webOptionGap) ssct.run {
                assertTrue(enabled); assertEq(1.0f, lightConeRad, "lightConeRad"); assertEq(0.4f, shadowDistance, "shadowDistance")
                assertEq(1.2f, contactDistanceMax, "contactDistanceMax"); assertEq(0.9f, intensity, "intensity")
                assertArr(floatArrayOf(0f, -1f, 0f), lightDirection); assertEq(0.02f, depthBias, "depthBias")
                assertEq(0.03f, depthSlopeBias, "depthSlopeBias"); assertEquals(6, sampleCount); assertEquals(2, rayCount)
            }
            if (!webOptionGap) gtao.run {
                assertEquals(6, sampleSliceCount); assertEquals(5, sampleStepsPerSlice)
                assertEquals(0.02f, thicknessHeuristic); assertTrue(useVisibilityBitmasks)
                assertEquals(0.75f, constThickness); assertTrue(linearThickness)
            }
        }
    }

    @Test
    fun dynamicResolutionOptionsRoundTrip() {
        setUpView()
        // minScale == maxScale so the engine keeps `enabled` set even on backends without
        // frame-time support (Noop/sim report isFrameTimeSupported()=false since 1.72.0).
        view.dynamicResolutionOptions = View.DynamicResolutionOptions().apply {
            enabled = true; homogeneousScaling = true; minScale = 0.5f; maxScale = 0.5f
            sharpness = 0.6f; quality = View.Quality.HIGH
        }
        view.dynamicResolutionOptions.run {
            assertTrue(enabled); assertTrue(homogeneousScaling); assertEq(0.5f, minScale, "minScale")
            assertEq(0.5f, maxScale, "maxScale"); assertEq(0.6f, sharpness, "sharpness"); assertEquals(View.Quality.HIGH, quality)
        }
    }

    @Test
    fun shadowAndMsaaOptionsRoundTrip() {
        setUpView()
        view.vsmShadowOptions = View.VsmShadowOptions().apply {
            anisotropy = 2; mipmapping = true; msaaSamples = 4; highPrecision = true
            lightBleedReduction = 0.2f
        }
        view.vsmShadowOptions.run {
            assertEquals(2, anisotropy); assertTrue(mipmapping); assertEquals(4, msaaSamples)
            assertTrue(highPrecision); assertEq(0.2f, lightBleedReduction, "lightBleedReduction")
        }
        view.softShadowOptions = View.SoftShadowOptions().apply {
            penumbraScale = 2f; penumbraRatioScale = 1.5f
            maxPenumbraRatio = 8f; maxSearchRadius = 0.75f
        }
        view.softShadowOptions.run {
            assertEq(2f, penumbraScale, "penumbraScale"); assertEq(1.5f, penumbraRatioScale, "penumbraRatioScale")
            assertEq(8f, maxPenumbraRatio, "maxPenumbraRatio"); assertEq(0.75f, maxSearchRadius, "maxSearchRadius")
        }
        view.multiSampleAntiAliasingOptions = View.MultiSampleAntiAliasingOptions().apply {
            enabled = true; sampleCount = 4; customResolve = true
        }
        view.multiSampleAntiAliasingOptions.run {
            assertTrue(enabled); assertEquals(4, sampleCount); assertTrue(customResolve)
        }
    }

    private fun assertArr(expected: FloatArray, actual: FloatArray) {
        assertEquals(expected.size, actual.size)
        for (i in expected.indices) assertEquals(expected[i], actual[i], 1e-6f, "index $i")
    }

    /**
     * BloomOptions::dirt/dirtStrength, FogOptions::skyColor and AmbientOcclusionOptions::
     * ssct/gtao are the only option fields with no embind binding — beamsplitter skips
     * pointer and nested-struct members ("JavaScript binding for X is not yet supported"),
     * so on web they cannot be pushed to the engine and never round-trip.
     */
    private val webOptionGap get() = TestEnv.target == TestTarget.JS

    // These values round-trip through the engine as C++ float, and Kotlin/JS carries Float
    // as a double, so the read-back is the float32-rounded value rather than the literal.
    private fun assertEq(expected: Float, actual: Float, name: String) {
        assertEquals(expected, actual, 1e-6f, name)
    }
}

package io.github.erkko68.filament

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
            assertEquals(0.7f, strength); assertEquals(false, threshold)
            assertEquals(0.3f, dirtStrength); assertEquals(View.Quality.HIGH, quality)
            assertTrue(lensFlare); assertEquals(false, starburst)
            assertEquals(0.1f, chromaticAberration); assertEquals(3, ghostCount)
            assertEquals(0.6f, ghostSpacing); assertEquals(2f, ghostThreshold)
            assertEquals(0.4f, haloRadius); assertEquals(0.2f, haloThickness)
            assertEquals(3f, haloThreshold); assertEquals(1000f, highlight)
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
            assertTrue(enabled); assertEquals(10f, distance); assertEquals(0.5f, density)
            assertEquals(2f, height); assertEquals(1.5f, heightFalloff)
            assertArr(floatArrayOf(0.1f, 0.2f, 0.3f), color)
            assertEquals(100f, cutOffDistance); assertEquals(0.8f, maximumOpacity)
            assertEquals(5f, inScatteringStart); assertEquals(3f, inScatteringSize)
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
            assertTrue(enabled); assertEquals(2f, cocScale); assertEquals(1.5f, cocAspectRatio)
            assertEquals(0.01f, maxApertureDiameter)
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
            assertTrue(enabled); assertEquals(0.5f, midPoint); assertEquals(0.6f, roundness)
            assertEquals(0.4f, feather); assertArr(floatArrayOf(0.1f, 0.2f, 0.3f, 1f), color)
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
            assertEquals(0.12f, feedback); assertEquals(-1f, lodBias); assertEquals(0.3f, sharpness)
            assertTrue(enabled); assertEquals(1f, upscaling); assertTrue(filterHistory)
            assertEquals(false, filterInput); assertTrue(useYCoCg); assertEquals(false, hdr)
            assertEquals(View.TemporalAntiAliasingOptions.BoxType.AABB_VARIANCE, boxType)
            assertEquals(View.TemporalAntiAliasingOptions.BoxClipping.NONE, boxClipping)
            assertEquals(View.TemporalAntiAliasingOptions.JitterPattern.UNIFORM_HELIX_X4, jitterPattern)
            assertEquals(1.1f, varianceGamma); assertTrue(preventFlickering)
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
            assertTrue(enabled); assertEquals(0.1f, thickness); assertEquals(0.2f, bias)
            assertEquals(3f, maxDistance); assertEquals(2f, stride)
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
            assertEquals(0.5f, radius); assertEquals(0.001f, bias); assertEquals(1.5f, intensity)
            assertEquals(1.2f, power); assertEquals(0.3f, minHorizonAngleRad)
            assertEquals(View.Quality.HIGH, quality); assertEquals(View.Quality.MEDIUM, lowPassFilter)
            assertEquals(View.Quality.LOW, upsampling); assertTrue(enabled); assertTrue(bentNormals)
            assertEquals(0.05f, bilateralThreshold); assertEquals(0.5f, resolution)
            assertEquals(View.AmbientOcclusionOptions.AmbientOcclusionType.GTAO, aoType)
            ssct.run {
                assertTrue(enabled); assertEquals(1.0f, lightConeRad); assertEquals(0.4f, shadowDistance)
                assertEquals(1.2f, contactDistanceMax); assertEquals(0.9f, intensity)
                assertArr(floatArrayOf(0f, -1f, 0f), lightDirection); assertEquals(0.02f, depthBias)
                assertEquals(0.03f, depthSlopeBias); assertEquals(6, sampleCount); assertEquals(2, rayCount)
            }
            gtao.run {
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
            assertTrue(enabled); assertTrue(homogeneousScaling); assertEquals(0.5f, minScale)
            assertEquals(0.5f, maxScale); assertEq(0.6f, sharpness, "sharpness"); assertEquals(View.Quality.HIGH, quality)
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
            assertEquals(2f, penumbraScale); assertEquals(1.5f, penumbraRatioScale)
            assertEquals(8f, maxPenumbraRatio); assertEquals(0.75f, maxSearchRadius)
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

    // These values round-trip through the engine as C++ float, and Kotlin/JS carries Float as a
    // double, so the read-back is the float32-rounded value rather than the literal.
    private fun assertEq(expected: Float, actual: Float, name: String) {
        assertEquals(expected, actual, 1e-6f, name)
    }
}

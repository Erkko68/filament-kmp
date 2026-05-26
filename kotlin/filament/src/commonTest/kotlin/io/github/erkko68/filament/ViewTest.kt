package io.github.erkko68.filament

import io.github.erkko68.filament.testutils.FilamentTestFixture
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ViewTest : FilamentTestFixture() {
    @Test
    fun testPickingQueryResult() {
        val result = View.PickingQueryResult(42, 0.75f, floatArrayOf(10f, 20f))
        assertEquals(42, result.renderable)
        assertEquals(0.75f, result.depth)
        assertEquals(10f, result.fragCoords[0])
        assertEquals(20f, result.fragCoords[1])
    }

    @Test
    fun testOptionsInstantiation() {
        val dro = View.DynamicResolutionOptions().apply {
            enabled = true
            homogeneousScaling = true
            minScale = 0.5f
            maxScale = 1.0f
            sharpness = 0.5f
            quality = View.Quality.HIGH
        }
        assertTrue(dro.enabled)
        assertTrue(dro.homogeneousScaling)
        assertEquals(0.5f, dro.minScale)
        assertEquals(1.0f, dro.maxScale)
        assertEquals(0.5f, dro.sharpness)
        assertEquals(View.Quality.HIGH, dro.quality)

        val rq = View.RenderQuality().apply {
            hdrColorBuffer = View.Quality.HIGH
        }
        assertEquals(View.Quality.HIGH, rq.hdrColorBuffer)

        val bloom = View.BloomOptions().apply {
            enabled = true
            levels = 4
            resolution = 256
            strength = 0.5f
            threshold = true
            dirtStrength = 0.5f
            quality = View.Quality.HIGH
            lensFlare = true
            starburst = true
            chromaticAberration = 0.05f
            ghostCount = 4
            ghostSpacing = 0.5f
            ghostThreshold = 0.5f
            haloRadius = 0.5f
            haloThickness = 0.5f
            haloThreshold = 0.5f
            highlight = 0.5f
            blendMode = View.BloomOptions.BlendMode.ADD
        }
        assertTrue(bloom.enabled)
        assertEquals(4, bloom.levels)
        assertEquals(256, bloom.resolution)
        assertEquals(0.5f, bloom.strength)
        assertTrue(bloom.threshold)
        assertEquals(0.5f, bloom.dirtStrength)
        assertEquals(View.Quality.HIGH, bloom.quality)
        assertTrue(bloom.lensFlare)
        assertTrue(bloom.starburst)
        assertEquals(0.05f, bloom.chromaticAberration)
        assertEquals(4, bloom.ghostCount)
        assertEquals(0.5f, bloom.ghostSpacing)
        assertEquals(View.BloomOptions.BlendMode.ADD, bloom.blendMode)

        val fog = View.FogOptions().apply {
            enabled = true
            distance = 10f
            density = 0.5f
            height = 1f
            heightFalloff = 0.1f
            color = floatArrayOf(1f, 1f, 1f)
            cutOffDistance = 100f
            maximumOpacity = 1f
            inScatteringStart = 0f
            inScatteringSize = 10f
            fogColorFromIbl = true
        }
        assertTrue(fog.enabled)
        assertEquals(10f, fog.distance)
        assertEquals(0.5f, fog.density)
        assertEquals(1f, fog.height)
        assertEquals(1f, fog.color[0])
        assertEquals(100f, fog.cutOffDistance)
        assertTrue(fog.fogColorFromIbl)

        val dof = View.DepthOfFieldOptions().apply {
            enabled = true
            cocScale = 1f
            maxApertureDiameter = 0.05f
            filter = View.DepthOfFieldOptions.Filter.MEDIAN
            nativeResolution = true
            foregroundRingCount = 2
            backgroundRingCount = 2
            fastGatherRingCount = 2
            maxForegroundCOC = 4
            maxBackgroundCOC = 4
        }
        assertTrue(dof.enabled)
        assertEquals(1f, dof.cocScale)
        assertEquals(0.05f, dof.maxApertureDiameter)
        assertEquals(View.DepthOfFieldOptions.Filter.MEDIAN, dof.filter)
        assertTrue(dof.nativeResolution)

        val vig = View.VignetteOptions().apply {
            enabled = true
            midPoint = 0.5f
            roundness = 0.5f
            feather = 0.5f
            color = floatArrayOf(0.1f, 0.2f, 0.3f)
        }
        assertTrue(vig.enabled)
        assertEquals(0.5f, vig.midPoint)
        assertEquals(0.1f, vig.color[0], 1e-6f)

        val ao = View.AmbientOcclusionOptions().apply {
            radius = 0.5f
            bias = 0.01f
            intensity = 1f
            scale = 1f
            power = 1f
            minConeAngle = 0.1f
            quality = View.Quality.HIGH
            lowPassFilter = View.Quality.HIGH
            upsampling = View.Quality.HIGH
            enabled = true
            bentNormals = true
            bilateralThreshold = 0.5f
            resolution = 0.5f
            ssct = View.AmbientOcclusionOptions.Ssct().apply {
                enabled = true
                lightConeRad = 0.5f
                shadowDistance = 10f
                contactDistanceMax = 10f
                intensity = 1f
                lightDirection = floatArrayOf(0f, -1f, 0f)
                depthBias = 0.01f
                depthSlopeBias = 0.01f
                sampleCount = 4
                rayCount = 4
            }
        }
        assertEquals(0.5f, ao.radius)
        assertTrue(ao.enabled)
        assertTrue(ao.bentNormals)
        assertTrue(ao.ssct.enabled)
        assertEquals(0f, ao.ssct.lightDirection[0])

        val taa = View.TemporalAntiAliasingOptions().apply {
            feedback = 0.5f
            lodBias = 0f
            sharpness = 0.5f
            enabled = true
            upscaling = 1f
            filterHistory = true
            filterInput = true
            useYCoCg = true
            hdr = true
            boxType = 1
            boxClipping = 1
            jitterPattern = 1
            varianceGamma = 0.5f
            preventFlickering = true
            historyReprojection = true
        }
        assertTrue(taa.enabled)
        assertEquals(0.5f, taa.feedback)

        val ssr = View.ScreenSpaceReflectionsOptions().apply {
            enabled = true
            thickness = 0.1f
            bias = 0.01f
            maxDistance = 10f
            stride = 1f
        }
        assertTrue(ssr.enabled)
        assertEquals(0.1f, ssr.thickness, 1e-6f)

        val vsm = View.VsmShadowOptions().apply {
            anisotropy = 4
            mipmapping = true
            msaaSamples = 4
            highPrecision = true
            lightBleedReduction = 0.5f
        }
        assertEquals(4, vsm.anisotropy)
        assertTrue(vsm.mipmapping)

        val soft = View.SoftShadowOptions().apply {
            penumbraScale = 1f
            penumbraRatioScale = 1f
        }
        assertEquals(1f, soft.penumbraScale)

        val guard = View.GuardBandOptions().apply {
            enabled = true
        }
        assertTrue(guard.enabled)

        val stereo = View.StereoscopicOptions().apply {
            enabled = true
        }
        assertTrue(stereo.enabled)

        val msaa = View.MultiSampleAntiAliasingOptions().apply {
            enabled = true
            sampleCount = 4
            customResolve = true
        }
        assertTrue(msaa.enabled)
        assertEquals(4, msaa.sampleCount)
    }

    @Test
    fun testViewLifecycleAndProperties() {
        val view = engine.createView()
        assertNotNull(view)
        assertTrue(engine.isValidView(view))

        view.name = "TestView"
        assertEquals("TestView", view.name)

        view.viewport = Viewport(0, 0, 1024, 768)
        val vp = view.viewport
        assertEquals(0, vp.left)
        assertEquals(1024, vp.width)

        view.blendMode = View.BlendMode.TRANSLUCENT
        assertEquals(View.BlendMode.TRANSLUCENT, view.blendMode)

        view.setVisibleLayers(0x3, 0x1)
        view.setLayerEnabled(0, true)
        assertEquals(0x1, view.getVisibleLayers())

        view.isPostProcessingEnabled = true
        assertTrue(view.isPostProcessingEnabled)

        view.dithering = View.Dithering.TEMPORAL
        assertEquals(View.Dithering.TEMPORAL, view.dithering)

        // Assign sub-options to View
        view.dynamicResolutionOptions = View.DynamicResolutionOptions().apply { enabled = true }
        assertTrue(view.dynamicResolutionOptions.enabled)
        assertNotNull(view.getLastDynamicResolutionScale())

        view.renderQuality = View.RenderQuality().apply { hdrColorBuffer = View.Quality.HIGH }
        assertEquals(View.Quality.HIGH, view.renderQuality.hdrColorBuffer)

        view.bloomOptions = View.BloomOptions().apply { enabled = true }
        assertTrue(view.bloomOptions.enabled)

        view.fogOptions = View.FogOptions().apply { enabled = true }
        assertTrue(view.fogOptions.enabled)

        view.depthOfFieldOptions = View.DepthOfFieldOptions().apply { enabled = true }
        assertTrue(view.depthOfFieldOptions.enabled)

        view.vignetteOptions = View.VignetteOptions().apply { enabled = true }
        assertTrue(view.vignetteOptions.enabled)

        view.ambientOcclusionOptions = View.AmbientOcclusionOptions().apply { enabled = true }
        assertTrue(view.ambientOcclusionOptions.enabled)

        view.temporalAntiAliasingOptions = View.TemporalAntiAliasingOptions().apply { enabled = true }
        assertTrue(view.temporalAntiAliasingOptions.enabled)

        view.screenSpaceReflectionsOptions = View.ScreenSpaceReflectionsOptions().apply { enabled = true }
        assertTrue(view.screenSpaceReflectionsOptions.enabled)

        view.shadowType = View.ShadowType.VSM
        assertEquals(View.ShadowType.VSM, view.shadowType)

        view.vsmShadowOptions = View.VsmShadowOptions().apply { anisotropy = 4 }
        assertEquals(4, view.vsmShadowOptions.anisotropy)

        view.softShadowOptions = View.SoftShadowOptions().apply { penumbraScale = 2f }
        assertEquals(2f, view.softShadowOptions.penumbraScale)

        view.guardBandOptions = View.GuardBandOptions().apply { enabled = true }
        assertTrue(view.guardBandOptions.enabled)

        view.stereoscopicOptions = View.StereoscopicOptions().apply { enabled = true }
        assertTrue(view.stereoscopicOptions.enabled)

        view.multiSampleAntiAliasingOptions = View.MultiSampleAntiAliasingOptions().apply { enabled = true }
        assertTrue(view.multiSampleAntiAliasingOptions.enabled)

        // View Boolean Flags
        view.isFrustumCullingEnabled = true
        assertTrue(view.isFrustumCullingEnabled)
        view.isShadowingEnabled = true
        assertTrue(view.isShadowingEnabled)
        view.isScreenSpaceRefractionEnabled = true
        assertTrue(view.isScreenSpaceRefractionEnabled)
        view.isStencilBufferEnabled = true
        assertTrue(view.isStencilBufferEnabled)
        view.isFrontFaceWindingInverted = true
        assertTrue(view.isFrontFaceWindingInverted)
        view.isTransparentPickingEnabled = true
        assertTrue(view.isTransparentPickingEnabled)

        // Globals & Dynamic lighting
        view.setMaterialGlobal(0, floatArrayOf(1.5f, 2.5f, 3.5f, 4.5f))
        val global = view.getMaterialGlobal(0)
        assertEquals(1.5f, global[0])
        assertEquals(2.5f, global[1])
        assertEquals(3.5f, global[2])
        assertEquals(4.5f, global[3])

        view.setDynamicLightingOptions(0.5f, 50f)
        assertTrue(view.fogEntity >= 0)

        view.antiAliasing = View.AntiAliasing.FXAA
        assertEquals(View.AntiAliasing.FXAA, view.antiAliasing)

        // Camera, Scene, etc attachments
        assertNull(view.scene)
        val scene = engine.createScene()
        view.scene = scene
        assertNotNull(view.scene)

        assertFalse(view.hasCamera)
        val entity = EntityManager.get().create()
        val camera = engine.createCamera(entity)
        view.camera = camera
        assertTrue(view.hasCamera)
        assertNotNull(view.camera)

        // History / Picking
        view.clearFrameHistory(engine)
        view.pick(100, 100) { result ->
            // Callback
        }

        // Cleanup
        engine.destroyCameraComponent(entity)
        EntityManager.get().destroy(entity)
        engine.destroyScene(scene)
        engine.destroyView(view)
    }
}

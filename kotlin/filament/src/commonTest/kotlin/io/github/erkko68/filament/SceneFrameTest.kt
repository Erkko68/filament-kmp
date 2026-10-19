package io.github.erkko68.filament

import io.github.erkko68.filament.testutils.FilamentTestFixture
import io.github.erkko68.filament.testsupport.FrameProbe
import io.github.erkko68.filament.testutils.TestMaterials
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/** A consumer's scene driven through frames on NOOP: everything Filament decides on the CPU is observable without pixels. */
class SceneFrameTest : FilamentTestFixture() {
    private fun translation(x: Float, y: Float, z: Float) =
        FloatArray(16) { if (it % 5 == 0) 1f else 0f }.also { it[12] = x; it[13] = y; it[14] = z }

    private fun withProbe(block: (FrameProbe, MaterialInstance) -> Unit) {
        val probe = FrameProbe(engine)
        // Camera at +Y looking down at the y = 0 plane.
        probe.camera.lookAt(0.0, 5.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0)
        val material = probe.instance(probe.material(TestMaterials.getLitMaterialBytes())).apply {
            setParameter("baseColor", 0.8f, 0.8f, 0.8f)
            setParameter("metallic", 0f)
            setParameter("roughness", 0.9f)
        }
        try {
            block(probe, material)
        } finally {
            probe.destroy()
        }
    }

    private fun FrameProbe.visibleAfterAFrame(): Int {
        assertNotNull(renderAndRead(frames = 1), "frame did not complete")
        return view.visibleRenderableCount
    }

    @Test
    fun cullingFollowsTransformsSceneMembershipAndLayers() = withProbe { probe, material ->
        val left = probe.addHorizontalQuad(material, -1f, 0f, 0f, 0.5f)
        val right = probe.addHorizontalQuad(material, 1f, 0f, 0f, 0.5f)
        // Above the camera, so outside the view frustum.
        val above = probe.addHorizontalQuad(material, 0f, 10f, 0f, 0.5f)
        assertEquals(2, probe.visibleAfterAFrame())

        val tm = engine.transformManager
        tm.setTransform(tm.getInstance(above), translation(0f, -10f, 0f))
        assertEquals(3, probe.visibleAfterAFrame(), "moved into view")

        probe.scene.remove(left)
        assertEquals(2, probe.visibleAfterAFrame(), "removed from the scene")

        val rm = engine.renderableManager
        rm.setLayerMask(rm.getInstance(right), 0xFF, 0x02)
        assertEquals(1, probe.visibleAfterAFrame(), "moved to a layer the view doesn't show")
        probe.view.setLayerEnabled(1, true)
        assertEquals(2, probe.visibleAfterAFrame(), "layer enabled on the view")
    }

    /** Lights, shadows and an environment, through every shadow technique: setup and variant selection run on the CPU. */
    @Test
    fun aLitShadowedSceneRendersWithEveryShadowType() = withProbe { probe, material ->
        probe.addHorizontalQuad(material, 0f, 0f, 0f, 4f)
        probe.addHorizontalQuad(material, 0f, 1f, 0f, 0.5f)
        val em = EntityManager.get()
        val lights = listOf(
            LightManager.Builder(LightManager.Type.SUN).direction(0.7f, -0.7f, 0f).intensity(100_000f).castShadows(true),
            LightManager.Builder(LightManager.Type.SPOT).position(0f, 3f, 0f).direction(0f, -1f, 0f).spotLightCone(0.3f, 0.6f).castShadows(true),
            LightManager.Builder(LightManager.Type.POINT).position(1f, 2f, 1f).falloff(5f),
        ).map { builder -> em.create().also { builder.build(engine, it); probe.scene.addEntity(it); probe.track(it) } }
        assertEquals(lights.size, probe.scene.lightCount)

        val cubemap = Texture.Builder().width(4).height(4).levels(3).sampler(Texture.Sampler.SAMPLER_CUBEMAP)
            .usage(Texture.Usage.SAMPLEABLE or Texture.Usage.COLOR_ATTACHMENT).format(Texture.InternalFormat.RGBA8).build(engine)
        val ibl = IndirectLight.Builder().reflections(cubemap).irradiance(3, FloatArray(27) { 0.5f }).intensity(30_000f).build(engine)
        val sky = Skybox.Builder().environment(cubemap).build(engine)
        probe.scene.indirectLight = ibl
        probe.scene.skybox = sky

        for (shadowType in ShadowType.entries) {
            probe.view.shadowType = shadowType
            assertNotNull(probe.renderAndRead(), "$shadowType frame did not complete")
        }

        probe.scene.indirectLight = null
        probe.scene.skybox = null
        engine.destroy(sky)
        engine.destroy(ibl)
        engine.destroy(cubemap)
    }
}

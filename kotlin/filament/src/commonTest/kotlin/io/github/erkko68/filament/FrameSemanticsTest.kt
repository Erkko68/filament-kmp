package io.github.erkko68.filament

import io.github.erkko68.filament.testutils.FrameProbe
import io.github.erkko68.filament.testutils.RenderingTestFixture
import io.github.erkko68.filament.testutils.TestMaterials
import io.github.erkko68.filament.testutils.meanAbsoluteDifference
import io.github.erkko68.filament.testutils.regionStats
import io.github.erkko68.filament.testsupport.IgnoreJs
import io.github.erkko68.filament.testsupport.TestEnv
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Tier C: semantic frame assertions. Renders a tiny lit scene (floor quad +
 * hovering caster quad + tilted sun) and asserts *relations between image
 * regions* — invariant across rasterizers/AA/sRGB, so no golden files and no
 * per-platform baselines. Guards the historical "wrong pixels, no exception"
 * wrapper-bug classes: uninitialized shadow options, silently-black materials,
 * missing shadow variants, dead light bindings.
 *
 * Scene layout (camera at +Y looking down, up = +Z; sun tilted towards -X so
 * the caster's shadow lands on the floor at +X of it, in the open):
 *
 *   open floor        caster        shadow
 *   x ≈ [-1.5,-0.7]   [-0.5,0.5]    [0.6,1.4]      (world units, y=0 plane)
 */
@IgnoreJs // Renderer.readPixels is a no-op on web (not bound in jsbindings.cpp).
class FrameSemanticsTest : RenderingTestFixture() {

    private fun litScene(engine: Engine, probe: FrameProbe, shadows: Boolean = true, casterCz: Float = 0f): Entity {
        val mat = probe.material(TestMaterials.getLitMaterialBytes())
        val floorInst = probe.instance(mat).apply {
            setParameter("baseColor", 0.8f, 0.8f, 0.8f)
            setParameter("metallic", 0.0f)
            setParameter("roughness", 0.9f)
        }
        val casterInst = probe.instance(mat).apply {
            setParameter("baseColor", 0.8f, 0.1f, 0.1f)
            setParameter("metallic", 0.0f)
            setParameter("roughness", 0.9f)
        }
        probe.addHorizontalQuad(floorInst, 0f, 0f, 0f, 8f)
        probe.addHorizontalQuad(casterInst, 0f, 1f, casterCz, 0.5f)

        val sun = EntityManager.get().create()
        LightManager.Builder(LightManager.Type.SUN)
            .direction(0.70710678f, -0.70710678f, 0f)
            .color(1f, 1f, 1f)
            .intensity(100_000f)
            .castShadows(shadows)
            .build(engine, sun)
        probe.scene.addEntity(sun)
        probe.track(sun)

        probe.camera.lookAt(0.0, 5.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0)
        return sun
    }

    private fun withProbe(block: (Engine, FrameProbe) -> Unit) {
        val engine = engine ?: return
        // Emulated Android GPUs advertise FL1 but render lit content black and can
        // crash the emulator on shadow passes; FL0 can't do lit materials at all.
        if (TestEnv.emulatedGpu) return
        if (engine.supportedFeatureLevel == Engine.FeatureLevel.FEATURE_LEVEL_0) return
        val probe = FrameProbe(engine)
        try {
            block(engine, probe)
        } finally {
            probe.destroy()
        }
    }

    /** Guards the silently-black-material class (web UBO bug, dead parameter marshalling). */
    @Test
    fun litMaterialShowsItsBaseColor() = withProbe { engine, probe ->
        litScene(engine, probe)
        val pixels = assertNotNull(probe.renderAndRead(), "readback did not complete")

        // Centre of the frame is the red caster quad, lit from above.
        val centre = pixels.regionStats(probe.width, probe.height, 0.45, 0.55, 0.45, 0.55)
        assertTrue(centre.meanLuma > 20.0, "lit material rendered ~black: $centre")
        assertTrue(centre.meanR > centre.meanB * 1.5, "red base colour not visible: $centre")
    }

    /** Guards the shadows-silently-absent class (uninitialized ShadowOptions C struct). */
    @Test
    fun directionalShadowDarkensTheFloor() = withProbe { engine, probe ->
        litScene(engine, probe)
        val pixels = assertNotNull(probe.renderAndRead(), "readback did not complete")

        assertShadowPresent(pixels, probe, "PCF")
    }

    /** Guards the missing-vsm-variant panic class (#64) — the technique switch must still render. */
    @Test
    fun vsmShadowsStillRender() = withProbe { engine, probe ->
        litScene(engine, probe)
        probe.view.shadowType = View.ShadowType.VSM
        val pixels = assertNotNull(probe.renderAndRead(), "readback did not complete")

        assertShadowPresent(pixels, probe, "VSM")
    }

    /** Guards dead light bindings: adding a sun must change the frame. */
    @Test
    fun lightMakesADifference() = withProbe { engine, probe ->
        val sun = litScene(engine, probe)
        val lit = assertNotNull(probe.renderAndRead(), "readback did not complete")

        probe.scene.removeEntity(sun)
        val unlit = assertNotNull(probe.renderAndRead(), "readback did not complete")

        val diff = meanAbsoluteDifference(lit, unlit)
        assertTrue(diff > 10.0, "removing the sun barely changed the frame (mad=$diff)")
    }

    /**
     * Pins the readPixels row order, which is backend-dependent: Metal delivers rows
     * top-down, OpenGL bottom-up (GL convention). The JVM compose surface keys its
     * vertical flip off this. The caster is shifted to world +Z — screen top, since the
     * camera looks down -Y with up = +Z — so the convention decides which rows it fills.
     * Vulkan/WebGPU are unverified; running there makes this test pin them too.
     */
    @Test
    fun readPixelsRowOrderMatchesBackendConvention() = withProbe { engine, probe ->
        // Caster at y=1, cz=0.8 spans z 0.3..1.3; visible half-extent at that height is
        // tan(22.5°)·4 ≈ 1.66, so it covers screen-y fractions ≈ 0.59..0.89 of the top half.
        litScene(engine, probe, shadows = false, casterCz = 0.8f)
        val pixels = assertNotNull(probe.renderAndRead(), "readback did not complete")

        val highRows = pixels.regionStats(probe.width, probe.height, 0.40, 0.60, 0.65, 0.85)
        val lowRows = pixels.regionStats(probe.width, probe.height, 0.40, 0.60, 0.15, 0.35)
        val highIsRed = highRows.meanR > highRows.meanB * 1.5
        val lowIsRed = lowRows.meanR > lowRows.meanB * 1.5
        assertTrue(highIsRed != lowIsRed, "ambiguous readback: high=$highRows low=$lowRows")

        val expectTopDown = engine.backend == Engine.Backend.METAL
        assertEquals(
            expectTopDown, lowIsRed,
            "readPixels row order changed for ${engine.backend}: high=$highRows low=$lowRows",
        )
    }

    private fun assertShadowPresent(pixels: ByteArray, probe: FrameProbe, technique: String) {
        // Camera looks down -Y with up=+Z, so screen-right is world -X. Visible
        // half-extent at y=0 is tan(22.5°)*5 ≈ 2.07; the shadow centred at world
        // x≈+1 lands at screen fraction 0.5 − 1.0/(2·2.07) ≈ 0.26, open floor mirrors it.
        val shadowed = pixels.regionStats(probe.width, probe.height, 0.20, 0.32, 0.45, 0.55)
        val open = pixels.regionStats(probe.width, probe.height, 0.68, 0.80, 0.45, 0.55)
        assertTrue(open.meanLuma > 20.0, "$technique: open floor rendered ~black: $open")
        assertTrue(
            shadowed.meanLuma < open.meanLuma * 0.7,
            "$technique: no shadow detected — shadowed=$shadowed vs open=$open",
        )
    }
}

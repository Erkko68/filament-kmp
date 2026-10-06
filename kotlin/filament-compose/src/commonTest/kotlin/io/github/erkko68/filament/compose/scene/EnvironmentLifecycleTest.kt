package io.github.erkko68.filament.compose.scene

import androidx.compose.ui.test.ExperimentalTestApi
import io.github.erkko68.filament.compose.testutils.TierBSceneFixture
import io.github.erkko68.filament.compose.testutils.composeScene
import io.github.erkko68.filament.compose.testutils.skippedComposeTest
import io.github.erkko68.filament.compose.testutils.withFilamentScene
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * Tier-B (real-backend) lifecycle coverage for the environment apply paths: `ApplySkybox` and
 * `ApplyIndirectLight` build GPU resources (a `Skybox` / `IndirectLight`) the NOOP driver panics on,
 * so this gates on a DEFAULT backend via [TierBSceneFixture]. Both apply *synchronously* on the
 * composition thread (no async loader), so unlike glTF they fit the headless harness.
 *
 * Assets are avoided deliberately: a **color** skybox needs no cubemap, and the IBL is built from
 * **spherical-harmonics** coefficients rather than a KTX/HDR cubemap — so the test needs no bundled
 * environment file. The texture-backed paths (`rememberKTXEnvironment`, `rememberHDREnvironment`) are
 * covered by [EnvironmentLoadingTest].
 */
class EnvironmentLifecycleTest : TierBSceneFixture() {

    // Gated tests must *return* the harness result (skippedComposeTest() on the skip branch) so the
    // async web `runComposeUiTest` is awaited — see skippedComposeTest's KDoc.
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun colorSkyboxAppliesAndClearsOnDisposal() = run {
        val engine = engine ?: return@run skippedComposeTest()
        val scene = scene ?: return@run skippedComposeTest()

        composeScene(
            engine = engine,
            scene = scene,
            whileComposed = { assertNotNull(scene.skybox, "color skybox should be attached while composed") },
            afterDispose = { assertNull(scene.skybox, "skybox should be cleared after disposal") },
        ) {
            val state = rememberSkyboxState(initialSource = SkyboxSource.Color(LinearColor(0.05f, 0.05f, 0.08f)))
            ApplySkybox(state, engine, scene)
        }
    }

    /** A color is set on the live Skybox: animating it must not rebuild the Skybox every frame. */
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun colorUpdatesTheSkyboxInPlace() = run {
        val engine = engine ?: return@run skippedComposeTest()
        val scene = scene ?: return@run skippedComposeTest()

        withFilamentScene(engine, scene) { setContent ->
            val state = SkyboxState(SkyboxSource.Color(LinearColor(0.05f, 0.05f, 0.08f)), false, 1f, 0)
            setContent { ApplySkybox(state, engine, scene) }
            waitForIdle()
            val skybox = assertNotNull(scene.skybox)

            state.source = SkyboxSource.Color(LinearColor(1f, 0f, 0f), alpha = 0.5f)
            mainClock.advanceTimeByFrame()
            waitForIdle()
            assertSame(skybox, scene.skybox, "a new color should not rebuild the Skybox")

            // Intensity has no setter: that does need a new one.
            state.intensity = 2f
            mainClock.advanceTimeByFrame()
            waitForIdle()
            assertEquals(2f, assertNotNull(scene.skybox).intensity)
            assertTrue(!engine.isValid(skybox), "the replaced Skybox is destroyed")

            state.source = null
            mainClock.advanceTimeByFrame()
            waitForIdle()
            assertNull(scene.skybox, "a null source removes the skybox")
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun shIndirectLightAppliesAndClearsOnDisposal() = run {
        val engine = engine ?: return@run skippedComposeTest()
        val scene = scene ?: return@run skippedComposeTest()

        composeScene(
            engine = engine,
            scene = scene,
            whileComposed = { assertNotNull(scene.indirectLight, "IBL should be attached while composed") },
            afterDispose = { assertNull(scene.indirectLight, "IBL should be cleared after disposal") },
        ) {
            // bands = 1 → a single constant-ambient SH term (1²×3 = 3 coefficients); no cubemap needed.
            val state = rememberIndirectLightState(
                initialIrradianceSh = SphericalHarmonics(bands = 1, coefficients = floatArrayOf(0.5f, 0.5f, 0.5f)),
                initialIntensity = 30_000f,
            )
            ApplyIndirectLight(state, engine, scene)
        }
    }

    /** Intensity and rotation are set on the live IndirectLight: animating them must not rebuild it every frame. */
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun intensityAndRotationUpdateTheIndirectLightInPlace() = run {
        val engine = engine ?: return@run skippedComposeTest()
        val scene = scene ?: return@run skippedComposeTest()

        withFilamentScene(engine, scene) { setContent ->
            val state = IndirectLightState(
                null, null, SphericalHarmonics(bands = 1, coefficients = floatArrayOf(0.5f, 0.5f, 0.5f)), 30_000f, null,
            )
            setContent { ApplyIndirectLight(state, engine, scene) }
            waitForIdle()
            val ibl = assertNotNull(scene.indirectLight)

            state.intensity = 10_000f
            state.rotation = Rotation.euler(yaw = 90f)
            mainClock.advanceTimeByFrame()
            waitForIdle()
            assertSame(ibl, scene.indirectLight, "a new intensity/rotation should not rebuild the IndirectLight")
            assertEquals(10_000f, ibl.intensity)
            assertEquals(Rotation.euler(yaw = 90f).toRotationMatrix().toList(), ibl.rotation.toList())

            // Back to no rotation: the identity, as a freshly built one has.
            state.rotation = null
            mainClock.advanceTimeByFrame()
            waitForIdle()
            assertEquals(Rotation.Identity.toRotationMatrix().toList(), ibl.rotation.toList())

            // New irradiance does need a new one.
            state.irradianceSh = SphericalHarmonics(bands = 1, coefficients = floatArrayOf(1f, 1f, 1f))
            mainClock.advanceTimeByFrame()
            waitForIdle()
            assertEquals(10_000f, assertNotNull(scene.indirectLight).intensity)

            setContent {}
            waitForIdle()
            assertNull(scene.indirectLight, "IBL should be cleared after disposal")
        }
    }
}

package io.github.erkko68.filament.compose.scene

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
 * Tier-B (real-backend) lifecycle coverage for the environment composables: `Skybox` and
 * `IndirectLight` build GPU resources (a `Skybox` / `IndirectLight`) the NOOP driver panics on,
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
            Skybox(color = LinearColor(0.05f, 0.05f, 0.08f))
        }
    }

    /** A color is set on the live Skybox: animating it must not rebuild the Skybox every frame. */
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun colorUpdatesTheSkyboxInPlace() = run {
        val engine = engine ?: return@run skippedComposeTest()
        val scene = scene ?: return@run skippedComposeTest()

        withFilamentScene(engine, scene) { setContent ->
            var color by mutableStateOf(LinearColor(0.05f, 0.05f, 0.08f))
            var intensity by mutableStateOf(1f)
            var shown by mutableStateOf(true)
            setContent { if (shown) Skybox(color = color, intensity = intensity) }
            waitForIdle()
            val skybox = assertNotNull(scene.skybox)

            color = LinearColor(1f, 0f, 0f)
            mainClock.advanceTimeByFrame()
            waitForIdle()
            assertSame(skybox, scene.skybox, "a new color should not rebuild the Skybox")

            // Intensity has no setter: that does need a new one.
            intensity = 2f
            mainClock.advanceTimeByFrame()
            waitForIdle()
            assertEquals(2f, assertNotNull(scene.skybox).intensity)
            assertTrue(!engine.isValid(skybox), "the replaced Skybox is destroyed")

            shown = false
            mainClock.advanceTimeByFrame()
            waitForIdle()
            assertNull(scene.skybox, "leaving the composition removes the skybox")
        }
    }

    /** A scene has one skybox: a second one replaces the first, and the first leaving doesn't clear it. */
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun aReplacedSkyboxLeavingKeepsTheOneThatReplacedIt() = run {
        val engine = engine ?: return@run skippedComposeTest()
        val scene = scene ?: return@run skippedComposeTest()

        withFilamentScene(engine, scene) { setContent ->
            var first by mutableStateOf(true)
            setContent {
                if (first) Skybox(color = LinearColor(1f, 0f, 0f))
                Skybox(color = LinearColor(0f, 0f, 1f), intensity = 2f)
            }
            waitForIdle()
            assertEquals(2f, assertNotNull(scene.skybox).intensity, "the later Skybox wins")

            first = false
            mainClock.advanceTimeByFrame()
            waitForIdle()
            assertEquals(2f, assertNotNull(scene.skybox).intensity, "the replaced Skybox leaving clears nothing")

            setContent {}
            waitForIdle()
            assertNull(scene.skybox)
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
            IndirectLight(
                reflections = null,
                irradiance = SphericalHarmonics(bands = 1, coefficients = floatArrayOf(0.5f, 0.5f, 0.5f)),
            )
        }
    }

    /** Intensity and rotation are set on the live IndirectLight: animating them must not rebuild it every frame. */
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun intensityAndRotationUpdateTheIndirectLightInPlace() = run {
        val engine = engine ?: return@run skippedComposeTest()
        val scene = scene ?: return@run skippedComposeTest()

        withFilamentScene(engine, scene) { setContent ->
            var irradiance by mutableStateOf(SphericalHarmonics(bands = 1, coefficients = floatArrayOf(0.5f, 0.5f, 0.5f)))
            var intensity by mutableStateOf(30_000f)
            var rotation by mutableStateOf(Rotation.Identity)
            setContent { IndirectLight(reflections = null, irradiance = irradiance, intensity = intensity, rotation = rotation) }
            waitForIdle()
            val ibl = assertNotNull(scene.indirectLight)

            intensity = 10_000f
            rotation = Rotation.euler(yaw = 90f)
            mainClock.advanceTimeByFrame()
            waitForIdle()
            assertSame(ibl, scene.indirectLight, "a new intensity/rotation should not rebuild the IndirectLight")
            assertEquals(10_000f, ibl.intensity)
            assertEquals(Rotation.euler(yaw = 90f).toRotationMatrix().toList(), ibl.rotation.toList())

            // Back to no rotation: the identity, as a freshly built one has.
            rotation = Rotation.Identity
            mainClock.advanceTimeByFrame()
            waitForIdle()
            assertEquals(Rotation.Identity.toRotationMatrix().toList(), ibl.rotation.toList())

            // New irradiance does need a new one.
            irradiance = SphericalHarmonics(bands = 1, coefficients = floatArrayOf(1f, 1f, 1f))
            mainClock.advanceTimeByFrame()
            waitForIdle()
            assertEquals(10_000f, assertNotNull(scene.indirectLight).intensity)

            setContent {}
            waitForIdle()
            assertNull(scene.indirectLight, "IBL should be cleared after disposal")
        }
    }
}

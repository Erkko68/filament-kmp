package io.github.erkko68.filament.compose.scene

import androidx.compose.ui.test.ExperimentalTestApi
import io.github.erkko68.filament.compose.testutils.TierBSceneFixture
import io.github.erkko68.filament.compose.testutils.composeScene
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * Tier-B (real-backend) lifecycle coverage for the environment apply paths: `ApplySkybox` and
 * `ApplyIndirectLight` build GPU resources (a `Skybox` / `IndirectLight`) the NOOP driver panics on,
 * so this gates on a DEFAULT backend via [TierBSceneFixture]. Both apply *synchronously* on the
 * composition thread (no async loader), so unlike glTF they fit the headless harness.
 *
 * Assets are avoided deliberately: a **color** skybox needs no cubemap, and the IBL is built from
 * **spherical-harmonics** coefficients rather than a KTX/HDR cubemap — so the test needs no bundled
 * environment file. The texture-backed paths (cubemap skybox, cubemap IBL, `rememberKTXEnvironment`)
 * are left for when a bundled KTX asset is added.
 */
class EnvironmentLifecycleTest : TierBSceneFixture() {

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun colorSkyboxAppliesAndClearsOnDisposal() {
        val engine = engine ?: return
        val scene = scene ?: return

        composeScene(
            engine = engine,
            scene = scene,
            whileComposed = { assertNotNull(scene.skybox, "color skybox should be attached while composed") },
            afterDispose = { assertNull(scene.skybox, "skybox should be cleared after disposal") },
        ) {
            val state = rememberSkyboxState(source = SkyboxSource.Color(Color(0.05f, 0.05f, 0.08f)))
            ApplySkybox(state, engine, scene)
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun shIndirectLightAppliesAndClearsOnDisposal() {
        val engine = engine ?: return
        val scene = scene ?: return

        composeScene(
            engine = engine,
            scene = scene,
            whileComposed = { assertNotNull(scene.indirectLight, "IBL should be attached while composed") },
            afterDispose = { assertNull(scene.indirectLight, "IBL should be cleared after disposal") },
        ) {
            // bands = 1 → a single constant-ambient SH term (1²×3 = 3 coefficients); no cubemap needed.
            val state = rememberIndirectLightState(
                irradianceSh = SphericalHarmonics(bands = 1, coefficients = floatArrayOf(0.5f, 0.5f, 0.5f)),
                intensity = 30_000f,
            )
            ApplyIndirectLight(state, engine, scene)
        }
    }
}

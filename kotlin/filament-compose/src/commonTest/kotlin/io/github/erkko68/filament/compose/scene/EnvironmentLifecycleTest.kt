package io.github.erkko68.filament.compose.scene

import androidx.compose.ui.test.ExperimentalTestApi
import io.github.erkko68.filament.compose.testutils.TierBSceneFixture
import io.github.erkko68.filament.compose.testutils.composeScene
import io.github.erkko68.filament.compose.testutils.skippedComposeTest
import io.github.erkko68.filament.testsupport.IgnoreJs
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

    // Gated tests must *return* the harness result (skippedComposeTest() on the skip branch) so the
    // async web `runComposeUiTest` is awaited — see skippedComposeTest's KDoc.
    //
    // @IgnoreJs: Skybox/IndirectLight built-then-set from any *async* context (a promise
    // continuation — which is where the web compose harness runs everything) corrupts the
    // filament.wasm heap and traps with "memory access out of bounds" in Scene.setSkybox; the same
    // sequence run synchronously passes (see the setSkybox test in :kotlin:filament webTest). Wasm
    // traps aren't catchable from Kotlin, so on wasmJs this hangs the whole suite. Engine-level
    // prebuilt bug (suspected embind builder double-free via FinalizationRegistry) — regate when
    // the prebuilt is fixed.
    @OptIn(ExperimentalTestApi::class)
    @IgnoreJs
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

    @OptIn(ExperimentalTestApi::class)
    @IgnoreJs // same engine-level async setSkybox/setIndirectLight crash — see above
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
}

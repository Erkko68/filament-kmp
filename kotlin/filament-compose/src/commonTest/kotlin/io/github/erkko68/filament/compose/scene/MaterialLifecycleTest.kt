package io.github.erkko68.filament.compose.scene

import androidx.compose.ui.test.ExperimentalTestApi
import io.github.erkko68.filament.Material
import io.github.erkko68.filament.compose.testutils.TestMaterials
import io.github.erkko68.filament.compose.testutils.TierBSceneFixture
import io.github.erkko68.filament.compose.testutils.composeScene
import io.github.erkko68.filament.compose.testutils.skippedComposeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Tier-B (real-backend) coverage for `rememberMaterial`. Building a [Material] hits a GPU resource the
 * NOOP driver panics on, so this gates on a DEFAULT backend via [TierBSceneFixture]. Verifies the happy
 * path (build → kept valid while composed → freed on disposal) and the contract that a bad payload never
 * throws inside composition — it returns null and invokes `onError`. The bad-payload path is the
 * regression guard for the FFM/native parser-panic fix (`Material.Builder.build` traps the native panic
 * and throws a catchable error instead of aborting the process).
 *
 * `rememberTexture` is deliberately not covered here: the JVM image decoder calls `abort()` on
 * undecodable bytes (an uncatchable upstream crash, not a null-return), and the repo bundles no
 * decodable test image to exercise the happy path. That coverage waits on a bundled image asset.
 */
class MaterialLifecycleTest : TierBSceneFixture() {

    // Gated tests must *return* the harness result (skippedComposeTest() on the skip branch) so the
    // async web `runComposeUiTest` is awaited — see skippedComposeTest's KDoc.
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun materialBuildsWhileComposedAndIsFreedOnDisposal() = run {
        val engine = engine ?: return@run skippedComposeTest()
        val scene = scene ?: return@run skippedComposeTest()
        val bytes = TestMaterials.getEmissiveMaterialBytes()
        if (bytes.isEmpty()) return@run skippedComposeTest()

        var captured: Material? = null
        composeScene(
            engine = engine,
            scene = scene,
            whileComposed = {
                val mat = assertNotNull(captured, "material should build from valid bytes")
                assertTrue(engine.isValidMaterial(mat), "material should be live while composed")
            },
            afterDispose = {
                val mat = assertNotNull(captured, "material handle should have been captured")
                assertTrue(!engine.isValidMaterial(mat), "material should be destroyed after disposal")
            },
        ) {
            captured = rememberMaterial(engine, bytes)
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun badMaterialBytesReturnNullAndReportErrorWithoutThrowing() = run {
        val engine = engine ?: return@run skippedComposeTest()
        val scene = scene ?: return@run skippedComposeTest()

        var result: Material? = null
        var errors = 0
        composeScene(
            engine = engine,
            scene = scene,
            whileComposed = {
                assertNull(result, "a bad .filamat payload must yield a null material")
                assertEquals(1, errors, "onError should fire exactly once for a bad payload")
            },
        ) {
            result = rememberMaterial(engine, ByteArray(64) { 0xFF.toByte() }, onError = { errors++ })
        }
    }
}

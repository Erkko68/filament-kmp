package io.github.erkko68.filament.compose.scene

import androidx.compose.ui.test.ExperimentalTestApi
import io.github.erkko68.filament.Material
import io.github.erkko68.filament.compose.testutils.ComposeTestFixture
import io.github.erkko68.filament.compose.testutils.composeScene
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Coverage for `rememberMaterial`. Verifies the happy
 * path (build → kept valid while composed → freed on disposal) and the contract that a bad payload never
 * throws inside composition — it returns null and invokes `onError`. The bad-payload path is the
 * regression guard for the native parser-panic fix (`Material.Builder.build` traps the native panic
 * and throws a catchable error instead of aborting the process).
 */
class MaterialLifecycleTest : ComposeTestFixture() {

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun materialBuildsWhileComposedAndIsFreedOnDisposal() = run {
        val bytes = StandardMaterial.Emissive.payload()

        var captured: Material? = null
        composeScene(
            engine = engine,
            scene = scene,
            whileComposed = {
                val mat = assertNotNull(captured, "material should build from valid bytes")
                assertTrue(engine.isValid(mat), "material should be live while composed")
            },
            afterDispose = {
                val mat = assertNotNull(captured, "material handle should have been captured")
                assertTrue(!engine.isValid(mat), "material should be destroyed after disposal")
            },
        ) {
            captured = rememberMaterial(engine, bytes)
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun badMaterialBytesReturnNullAndReportErrorWithoutThrowing() = run {

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

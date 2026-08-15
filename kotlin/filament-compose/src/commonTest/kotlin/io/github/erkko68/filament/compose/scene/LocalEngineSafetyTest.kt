package io.github.erkko68.filament.compose.scene

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * Calling a resource loader outside `rememberFilamentScene { }` without an explicit engine is a
 * programmer error, not a runtime condition: it must fail loudly with an actionable message
 * rather than return null (which callers can't distinguish from "still loading").
 */
class LocalEngineSafetyTest {

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun loaderOutsideSceneThrowsActionableError() = runComposeUiTest {
        val failure = assertFailsWith<IllegalStateException> {
            setContent { rememberMaterial { ByteArray(0) } }
        }
        assertTrue(
            failure.message.orEmpty().contains("rememberFilamentScene"),
            "error should name the fix, was: ${failure.message}",
        )
    }
}

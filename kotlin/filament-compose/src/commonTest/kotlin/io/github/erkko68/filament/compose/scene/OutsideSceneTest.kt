package io.github.erkko68.filament.compose.scene

import androidx.compose.runtime.Composable
import io.github.erkko68.filament.LightManager
import io.github.erkko68.filament.compose.FilamentEffect
import io.github.erkko68.filament.compose.FilamentSceneScope
import io.github.erkko68.filament.compose.testutils.ComposeTestFixture
import io.github.erkko68.filament.compose.testutils.compositionFailure
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Scene content composed where there is no engine or no scene (outside `rememberFilamentScene { }`, or after the
 * scene is gone) must fail with the message that names the fix, not with a null dereference or a native abort.
 */
class OutsideSceneTest : ComposeTestFixture() {
    private val needsAnEngine: List<Pair<String, @Composable FilamentSceneScope.() -> Unit>> = listOf(
        "DirectionalLight" to { DirectionalLight() },
        "PointLight" to { PointLight() },
        "Light" to { Light(LightManager.Type.POINT) {} },
        "Group" to { Group {} },
        "CameraNode" to { CameraNode(rememberCameraState()) },
        "FilamentEffect" to { FilamentEffect {} },
        "rememberMaterial" to { rememberMaterial { ByteArray(0) } },
        "rememberTexture" to { rememberTexture { ByteArray(0) } },
        "rememberGltfAsset" to { rememberGltfAsset { ByteArray(0) } },
        "rememberStandardMaterial" to { rememberStandardMaterial(StandardMaterial.Unlit) },
        "rememberColorMaterialInstance" to { rememberColorMaterialInstance(LinearColor(1f, 1f, 1f)) },
        "rememberUnlitColorMaterialInstance" to { rememberUnlitColorMaterialInstance(LinearColor(1f, 1f, 1f)) },
        "rememberEmissiveMaterialInstance" to { rememberEmissiveMaterialInstance(LinearColor(1f, 1f, 1f)) },
        "rememberTransparentColorMaterialInstance" to { rememberTransparentColorMaterialInstance(LinearColor(1f, 1f, 1f)) },
    )

    // Everything that adds to the scene, as opposed to only building engine resources.
    private val needsAScene: List<Pair<String, @Composable FilamentSceneScope.() -> Unit>> = listOf(
        "DirectionalLight" to { DirectionalLight() },
        "PointLight" to { PointLight() },
        "Light" to { Light(LightManager.Type.POINT) {} },
        "FilamentEffect" to { FilamentEffect {} },
    )

    private fun assertNamesTheFix(what: String, missing: String, failure: Throwable?) {
        assertIs<IllegalStateException>(failure, "$what without $missing should fail to compose")
        val message = failure.message.orEmpty()
        assertTrue("No $missing in scope" in message && "rememberFilamentScene" in message, "$what without $missing said: $message")
    }

    @Test
    fun withoutAnEngine() {
        for ((what, content) in needsAnEngine) assertNamesTheFix(what, "FilamentEngine", compositionFailure(null, null, content))
    }

    @Test
    fun withAnEngineButNoScene() {
        for ((what, content) in needsAScene) assertNamesTheFix(what, "FilamentScene", compositionFailure(engine, null, content))
    }

    // The same content is fine once both are there; nothing in the lists fails for another reason.
    @Test
    fun sceneContentComposesInsideAScene() {
        for ((what, content) in needsAScene + needsAnEngine.filter { it.first in setOf("Group", "CameraNode") }) {
            assertNull(compositionFailure(engine, scene, content), what)
        }
    }
}

package io.github.erkko68.filament.compose.scene

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import io.github.erkko68.filament.compose.FilamentSceneScope
import io.github.erkko68.filament.compose.testutils.ComposeTestFixture
import io.github.erkko68.filament.compose.testutils.assertEntitiesDestroyed
import io.github.erkko68.filament.compose.testutils.assertSceneEmpty
import io.github.erkko68.filament.compose.testutils.withFilamentScene
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Lifecycle/leak coverage for the typed light composables. Lights touch only CPU-side managers
 * (Entity/Transform/Light) + scene membership, so they run fully under NOOP. The assertions guard
 * the dispose-ordering invariants documented in Light.kt: a light enters the scene with a live
 * component, and leaving composition removes it from the scene, destroys its entity, and frees the
 * light component — with nothing left behind.
 */
class LightLifecycleTest : ComposeTestFixture() {

    private val lights: List<Pair<String, @Composable FilamentSceneScope.() -> Unit>> = listOf(
        "DirectionalLight" to { DirectionalLight() },
        "SunLight" to { SunLight() },
        "PointLight" to { PointLight() },
        "SpotLight" to { SpotLight() },
        "FocusedSpotLight" to { FocusedSpotLight() },
    )

    // Each light type is mounted then disposed within one `runComposeUiTest` body (the loop runs
    // synchronously inside it) and the test returns that body's result so JS's asynchronous
    // `runComposeUiTest` is awaited — see ComposeSceneHarness.
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun eachLightTypeEntersAndLeavesCleanly() = withFilamentScene(engine, scene) { setContent ->
        for ((name, light) in lights) {
            setContent { light() }
            waitForIdle()
            val entities = scene.getEntities()
            assertEquals(1, scene.lightCount, "$name should add exactly one light while composed")
            assertEquals(1, entities.size, "$name should add exactly one entity while composed")
            assertTrue(
                entities.size == 1 && engine.lightManager.hasComponent(entities[0]),
                "$name should have a live light component while composed",
            )

            setContent {}
            waitForIdle()
            assertSceneEmpty(scene, "$name leaked after disposal")
            assertEntitiesDestroyed(engine, entities)
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun parameterUpdatesDoNotChurnEntities() = withFilamentScene(engine, scene) { setContent ->
        // Recomposing with changed runtime params (intensity/color) must update in place — exactly
        // one light, the same entity — never duplicate or leak it.
        var intensity by mutableStateOf(50_000f)
        setContent {
            DirectionalLight(intensity = LightIntensity.LuminousPower(intensity))
        }
        waitForIdle()
        val capturedEntity = scene.getEntities().single()

        repeat(5) {
            intensity += 10_000f
            waitForIdle()
            assertEquals(1, scene.lightCount, "update must not duplicate the light")
            assertEquals(capturedEntity, scene.getEntities().single(), "entity must be stable across updates")
        }

        setContent {}
        waitForIdle()
        assertSceneEmpty(scene)
        assertEntitiesDestroyed(engine, intArrayOf(capturedEntity))
    }
}

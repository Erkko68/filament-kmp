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
import io.github.erkko68.filament.compose.testutils.composeScene
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

    @Test
    fun eachLightTypeEntersAndLeavesCleanly() {
        for ((name, light) in lights) {
            // Captured while composed; the leak assertions below run after disposal.
            var liveCount = -1
            var entities = IntArray(0)
            var componentLive = false
            composeScene(
                engine, scene,
                whileComposed = {
                    liveCount = scene.lightCount
                    entities = scene.getEntities()
                    componentLive = entities.size == 1 && engine.getLightManager().hasComponent(entities[0])
                },
            ) {
                light()
            }
            assertEquals(1, liveCount, "$name should add exactly one light while composed")
            assertEquals(1, entities.size, "$name should add exactly one entity while composed")
            assertTrue(componentLive, "$name should have a live light component while composed")
            assertSceneEmpty(scene, "$name leaked after disposal")
            assertEntitiesDestroyed(engine, entities)
        }
    }

    @Test
    @OptIn(ExperimentalTestApi::class)
    fun parameterUpdatesDoNotChurnEntities() {
        // Recomposing with changed runtime params (intensity/color) must update in place — exactly
        // one light, the same entity — never duplicate or leak it.
        var capturedEntity = -1
        withFilamentScene(engine, scene) { setContent ->
            var intensity by mutableStateOf(50_000f)
            setContent {
                DirectionalLight(intensity = intensity)
            }
            waitForIdle()
            capturedEntity = scene.getEntities().single()

            repeat(5) {
                intensity += 10_000f
                waitForIdle()
                assertEquals(1, scene.lightCount, "update must not duplicate the light")
                assertEquals(capturedEntity, scene.getEntities().single(), "entity must be stable across updates")
            }

            setContent {}
            waitForIdle()
        }
        assertSceneEmpty(scene)
        assertEntitiesDestroyed(engine, intArrayOf(capturedEntity))
    }
}

package io.github.erkko68.filament.compose.scene

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import io.github.erkko68.filament.LightManager
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
            val entities = buildList { scene.forEach(::add) }.toIntArray()
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
        val capturedEntity = buildList { scene.forEach(::add) }.single()

        repeat(5) {
            intensity += 10_000f
            waitForIdle()
            assertEquals(1, scene.lightCount, "update must not duplicate the light")
            assertEquals(capturedEntity, buildList { scene.forEach(::add) }.single(), "entity must be stable across updates")
        }

        setContent {}
        waitForIdle()
        assertSceneEmpty(scene)
        assertEntitiesDestroyed(engine, intArrayOf(capturedEntity))
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun intensityUnitsAndShadowConfigReachTheLightManager() = withFilamentScene(engine, scene) { setContent ->
        var intensity: LightIntensity by mutableStateOf(LightIntensity.Candela(500f))
        setContent {
            SpotLight(intensity = intensity, shadow = ShadowConfig(mapSize = 2048))
        }
        waitForIdle()
        val lm = engine.lightManager
        val light = lm.getInstance(buildList { scene.forEach(::add) }.single())
        assertEquals(500f, lm.getIntensity(light), 1e-3f) // a spot light's candela is stored as is
        assertTrue(lm.isShadowCaster(light))
        assertEquals(2048, lm.getShadowOptions(light).mapSize)

        intensity = LightIntensity.Watts(100f, 0.5f) // 34150 lm; a spot divides by π
        mainClock.advanceTimeByFrame()
        waitForIdle()
        assertEquals(34150f / kotlin.math.PI.toFloat(), lm.getIntensity(light), 0.1f)
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun rawLightRebuildsWhenAKeyChanges() = withFilamentScene(engine, scene) { setContent ->
        var lux by mutableStateOf(1_000f)
        setContent {
            Light(LightManager.Type.DIRECTIONAL, lux) { intensity(lux) }
        }
        waitForIdle()
        val entity = buildList { scene.forEach(::add) }.single()
        val lm = engine.lightManager
        assertEquals(1_000f, lm.getIntensity(lm.getInstance(entity)))

        lux = 2_000f
        mainClock.advanceTimeByFrame()
        waitForIdle()
        assertEquals(entity, buildList { scene.forEach(::add) }.single(), "the entity survives a rebuild")
        assertEquals(2_000f, lm.getIntensity(lm.getInstance(entity)))

        setContent {}
        waitForIdle()
        assertSceneEmpty(scene)
        assertEntitiesDestroyed(engine, intArrayOf(entity))
    }
}

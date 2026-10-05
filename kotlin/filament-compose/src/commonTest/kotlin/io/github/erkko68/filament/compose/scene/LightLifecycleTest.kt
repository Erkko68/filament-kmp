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
import io.github.erkko68.filament.compose.internal.transformMatrix
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
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

    /** Filament applies the entity's transform to the light's own position: set on both, a light sits twice as far. */
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun positionIsCarriedByTheTransformAlone() = withFilamentScene(engine, scene) { setContent ->
        for (shadow in listOf(null, ShadowConfig())) { // a new shadow config rebuilds the component
            setContent { SpotLight(position = Position(1f, 2f, 3f), shadow = shadow) }
            waitForIdle()
            val entity = buildList { scene.forEach(::add) }.single()
            val lm = engine.lightManager
            val tm = engine.transformManager
            assertEquals(listOf(0f, 0f, 0f), lm.getPosition(lm.getInstance(entity)).toList())
            assertEquals(listOf(1f, 2f, 3f), tm.getWorldTransform(tm.getInstance(entity)).slice(12..14))
        }
    }

    /** Filament turns a parented light with its Group, so the direction it is given must not be turned as well. */
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun onlyAPinnedLightIsReAimedAgainstItsGroup() = withFilamentScene(engine, scene) { setContent ->
        val direction = Direction(0f, -1f, 0f)
        val turned = Rotation.axisAngle(Direction(0f, 0f, 1f), 90f)
        for (follow in listOf(true, false)) {
            setContent { Group(rotation = turned) { DirectionalLight(direction, followGroupRotation = follow) } }
            waitForIdle()
            repeat(2) { mainClock.advanceTimeByFrame() }
            val entity = buildList { scene.forEach(::add) }.single()
            val lm = engine.lightManager
            val tm = engine.transformManager
            val local = lm.getDirection(lm.getInstance(entity))
            if (follow) assertClose(direction, local, "a following light keeps the direction it was given")
            val world = cofactorTransform(tm.getWorldTransform(tm.getInstance(entity)), local)
            val expected = if (follow) Direction(1f, 0f, 0f) else direction
            assertClose(expected, world, "followGroupRotation = $follow")
        }
    }

    @Test
    fun pinnedDirectionSurvivesScaleAndMirroring() {
        val direction = Direction(0.3f, -1f, -0.5f).normalized()
        val transforms = listOf(
            transformMatrix(Position(4f, 5f, 6f), Rotation.euler(30f, 70f, 110f), Scale(1f)),
            transformMatrix(Position(0f), Rotation.euler(30f, 70f, 110f), Scale(1f, 3f, 0.5f)),
            transformMatrix(Position(0f), Rotation.euler(30f, 70f, 110f), Scale(-1f, 2f, 1f)),
        )
        for (world in transforms) {
            val local = assertNotNull(pinnedLocalDirection(world, direction))
            assertClose(direction, cofactorTransform(world, floatArrayOf(local.x, local.y, local.z)), "pinned")
        }
        assertNull(pinnedLocalDirection(FloatArray(16), direction), "a collapsed transform aims nowhere")
    }

    /** Filament's direction transform (`mat3::getTransformForNormals`, see FScene::prepare), normalized. */
    private fun cofactorTransform(m: FloatArray, d: FloatArray): FloatArray {
        fun c(col: Int, row: Int): Float {
            val (c1, c2) = (0..2).filter { it != col }
            val (r1, r2) = (0..2).filter { it != row }
            val minor = m[c1 * 4 + r1] * m[c2 * 4 + r2] - m[c2 * 4 + r1] * m[c1 * 4 + r2]
            return if ((col + row) % 2 == 0) minor else -minor
        }
        val out = FloatArray(3) { row -> (0..2).sumOf { col -> (c(col, row) * d[col]).toDouble() }.toFloat() }
        val len = kotlin.math.sqrt(out[0] * out[0] + out[1] * out[1] + out[2] * out[2])
        return FloatArray(3) { out[it] / len }
    }

    private fun assertClose(expected: Direction, actual: FloatArray, message: String) {
        val e = floatArrayOf(expected.x, expected.y, expected.z)
        for (i in 0..2) assertEquals(e[i], actual[i], 1e-4f, "$message: expected ${e.toList()}, got ${actual.toList()}")
    }
}

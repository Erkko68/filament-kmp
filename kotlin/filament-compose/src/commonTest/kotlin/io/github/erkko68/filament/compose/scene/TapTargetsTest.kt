package io.github.erkko68.filament.compose.scene

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import io.github.erkko68.filament.compose.LocalTapTargets
import io.github.erkko68.filament.compose.TapTargets
import io.github.erkko68.filament.compose.scene.primitives.Cube
import io.github.erkko68.filament.compose.testutils.ComposeTestFixture
import io.github.erkko68.filament.compose.testutils.TestGlb
import io.github.erkko68.filament.compose.testutils.withFilamentScene
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Which handler each scene object registers for taps, and that registrations follow the entities. */
class TapTargetsTest : ComposeTestFixture() {

    private fun TapTargets.tap(entity: Int) = assertNotNull(handlerFor(entity), "entity $entity should answer taps").invoke(Position(0f))

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun primitiveAnswersTapsWhileComposed() = run {
        val material = materialInstance()
        val taps = TapTargets()
        withFilamentScene(engine, scene) { setContent ->
            var cube = -1
            var tapped = 0
            setContent {
                CompositionLocalProvider(LocalTapTargets provides taps) {
                    Cube(material, onCreate = { cube = entity }, onTap = { tapped++ })
                }
            }
            waitForIdle()
            taps.tap(cube)
            assertEquals(1, tapped)

            setContent {}
            waitForIdle()
            assertNull(taps.handlerFor(cube), "a disposed cube should stop answering taps")
            assertTrue(taps.isEmpty)
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun withoutOnTapNothingIsRegistered() = run {
        val material = materialInstance()
        val taps = TapTargets()
        withFilamentScene(engine, scene) { setContent ->
            setContent {
                CompositionLocalProvider(LocalTapTargets provides taps) { Cube(material) }
            }
            waitForIdle()
            assertTrue(taps.isEmpty, "a view should install no tap detector for a scene without handlers")
        }
    }

    /** A new material rebuilds the entity: the old one must not keep answering. */
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun rebuiltEntityTakesOverTheRegistration() = run {
        val first = materialInstance()
        val second = materialInstance()
        val taps = TapTargets()
        withFilamentScene(engine, scene) { setContent ->
            var material by mutableStateOf(first)
            val entities = mutableListOf<Int>()
            var tapped = 0
            setContent {
                CompositionLocalProvider(LocalTapTargets provides taps) {
                    Cube(material, onCreate = { entities += entity }, onTap = { tapped++ })
                }
            }
            waitForIdle()
            material = second
            mainClock.advanceTimeByFrame()
            waitForIdle()

            assertEquals(2, entities.size, "a new material should rebuild the entity")
            assertNotEquals(entities[0], entities[1])
            assertNull(taps.handlerFor(entities[0]), "the old entity should be unregistered")
            taps.tap(entities[1])
            assertEquals(1, tapped)
        }
    }

    /** A tap reaches the lambda from the latest recomposition, with what it captured then. */
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun latestLambdaAnswers() = run {
        val material = materialInstance()
        val taps = TapTargets()
        withFilamentScene(engine, scene) { setContent ->
            var label by mutableStateOf("a")
            var cube = -1
            var last = ""
            setContent {
                val current = label
                CompositionLocalProvider(LocalTapTargets provides taps) {
                    Cube(material, onCreate = { cube = entity }, onTap = { last = current })
                }
            }
            waitForIdle()
            label = "b"
            mainClock.advanceTimeByFrame()
            waitForIdle()

            taps.tap(cube)
            assertEquals("b", last)
        }
    }

    /** Children without a handler answer with the nearest Group's; their own handler wins. */
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun groupHandlerReachesChildrenWithoutTheirOwn() = run {
        val material = materialInstance()
        val taps = TapTargets()
        withFilamentScene(engine, scene) { setContent ->
            var plain = -1
            var own = -1
            var nested = -1
            val hits = mutableListOf<String>()
            setContent {
                CompositionLocalProvider(LocalTapTargets provides taps) {
                    Group(onTap = { hits += "group" }) {
                        Cube(material, onCreate = { plain = entity })
                        Cube(material, onCreate = { own = entity }, onTap = { hits += "own" })
                        Group { Cube(material, onCreate = { nested = entity }) }
                    }
                }
            }
            waitForIdle()
            taps.tap(plain)
            taps.tap(own)
            taps.tap(nested)
            assertEquals(listOf("group", "own", "group"), hits)
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun gltfInstanceAnswersOnEveryRenderable() = run {
        val asset = gltfAsset(TestGlb.getDuckGlbBytes())
        val taps = TapTargets()
        withFilamentScene(engine, scene) { setContent ->
            var renderables = emptyList<Int>()
            var tapped = 0
            setContent {
                CompositionLocalProvider(LocalTapTargets provides taps) {
                    GltfInstance(
                        asset,
                        onCreate = { renderables = instance.entities.filter { engine.renderableManager.hasComponent(it) } },
                        onTap = { tapped++ },
                    )
                }
            }
            waitForIdle()
            assertTrue(renderables.isNotEmpty())
            renderables.forEach { taps.tap(it) }
            assertEquals(renderables.size, tapped)

            setContent {}
            waitForIdle()
            assertTrue(taps.isEmpty, "a disposed instance should stop answering taps")
        }
    }
}

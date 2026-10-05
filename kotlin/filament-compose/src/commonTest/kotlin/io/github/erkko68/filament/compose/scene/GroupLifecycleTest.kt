package io.github.erkko68.filament.compose.scene

import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentWithReceiverOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import io.github.erkko68.filament.compose.FilamentSceneScope
import io.github.erkko68.filament.compose.testutils.ComposeTestFixture
import io.github.erkko68.filament.compose.testutils.withFilamentScene
import io.github.erkko68.filament.compose.testutils.assertEntitiesDestroyed
import io.github.erkko68.filament.compose.testutils.assertSceneEmpty
import io.github.erkko68.filament.compose.testutils.composeScene
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Lifecycle coverage for [Group]: a group is a transform-only entity (not added to the scene) that
 * parents its children. Verifies children are parented to the group's transform, nested groups
 * chain correctly, and disposal tears everything down — inner before outer — leaving no live
 * entities or scene members.
 */
class GroupLifecycleTest : ComposeTestFixture() {

    // Assertions live in `whileComposed`/`afterDispose` and the test returns the harness result so
    // the JS (asynchronous) `runComposeUiTest` is awaited — see composeScene's KDoc.
    @Test
    fun childLightIsParentedToTheGroup() = run {
        var groupEntity = -1
        var lightEntity = -1
        composeScene(
            engine, scene,
            whileComposed = {
                lightEntity = buildList { scene.forEach(::add) }.single()
                val tm = engine.transformManager
                val lightParent = tm.getParent(tm.getInstance(lightEntity))
                assertTrue(groupEntity >= 0, "Group should have created a transform entity")
                assertEquals(groupEntity, lightParent, "child light should be parented to the group")
            },
            afterDispose = {
                assertSceneEmpty(scene)
                assertEntitiesDestroyed(engine, intArrayOf(groupEntity, lightEntity))
            },
        ) {
            Group(onCreate = { groupEntity = entity }) {
                DirectionalLight()
            }
        }
    }

    @Test
    fun parentingAndNestingHoldWhileComposed() = run {
        var outerGroup = -1
        var innerGroup = -1
        composeScene(
            engine, scene,
            whileComposed = {
                val tm = engine.transformManager
                val innerParent = tm.getParent(tm.getInstance(innerGroup))
                val childCountUnderInner = tm.getChildCount(tm.getInstance(innerGroup))
                assertEquals(outerGroup, innerParent, "inner group should be parented to the outer group")
                assertEquals(1, childCountUnderInner, "inner group should hold its one child light")
            },
            afterDispose = {
                assertSceneEmpty(scene)
                assertEntitiesDestroyed(engine, intArrayOf(outerGroup, innerGroup))
            },
        ) {
            Group(onCreate = { outerGroup = entity }) {
                Group(onCreate = { innerGroup = entity }) {
                    PointLight()
                }
            }
        }
    }

    /** A node moved out of its Group keeps its entity, so it has to let go of the Group's transform itself. */
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun contentMovedOutOfAGroupIsUnparented() = withFilamentScene(engine, scene) { setContent ->
        var grouped by mutableStateOf(true)
        var outer = -1
        var inner = -1
        setContent {
            val content = remember {
                movableContentWithReceiverOf<FilamentSceneScope> {
                    PointLight()
                    Group(onCreate = { inner = entity }) {}
                }
            }
            Group(onCreate = { outer = entity }) { if (grouped) content() }
            if (!grouped) content()
        }
        waitForIdle()
        val tm = engine.transformManager
        val light = buildList { scene.forEach(::add) }.single()
        assertEquals(outer, tm.getParent(tm.getInstance(light)))
        assertEquals(outer, tm.getParent(tm.getInstance(inner)))

        grouped = false
        mainClock.advanceTimeByFrame()
        waitForIdle()
        assertEquals(light, buildList { scene.forEach(::add) }.single(), "moved, not rebuilt")
        assertEquals(0, tm.getParent(tm.getInstance(light)), "the moved light should have no parent")
        assertEquals(0, tm.getParent(tm.getInstance(inner)), "the moved group should have no parent")
        assertEquals(0, tm.getChildCount(tm.getInstance(outer)))

        grouped = true
        mainClock.advanceTimeByFrame()
        waitForIdle()
        assertEquals(outer, tm.getParent(tm.getInstance(light)), "and moves back in")
    }
}

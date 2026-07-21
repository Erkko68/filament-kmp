package io.github.erkko68.filament.compose.scene

import io.github.erkko68.filament.compose.testutils.ComposeTestFixture
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
                lightEntity = scene.getEntities().single()
                val tm = engine.getTransformManager()
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
                val tm = engine.getTransformManager()
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
}

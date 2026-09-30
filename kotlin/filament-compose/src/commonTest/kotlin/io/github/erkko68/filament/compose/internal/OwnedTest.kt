package io.github.erkko68.filament.compose.internal

import kotlin.test.Test
import kotlin.test.assertEquals

/** [Owned]'s ordering rules on plain values: no GPU, runs on every target. */
class OwnedTest {
    private val destroyed = mutableListOf<String>()
    private fun owned(name: String, vararg dependsOn: Any?) = Owned(name, dependsOn.toList()) { destroyed += it }

    @Test
    fun childFirstReleaseDestroysImmediately() {
        val engine = owned("engine")
        val material = owned("material", "engine")
        material.release()
        assertEquals(listOf("material"), destroyed)
        engine.release()
        assertEquals(listOf("material", "engine"), destroyed)
    }

    @Test
    fun parentFirstReleaseWaitsForTheLastChild() {
        val engine = owned("engine")
        val material = owned("material", "engine")
        val a = owned("instanceA", "engine", "material")
        val b = owned("instanceB", "engine", "material")

        engine.release()
        material.release()
        a.release()
        assertEquals(listOf("instanceA"), destroyed, "material and engine still have a live child")

        b.release()
        assertEquals(listOf("instanceA", "instanceB", "material", "engine"), destroyed)
    }

    @Test
    fun diamondDependenciesDestroyEachParentOnce() {
        val engine = owned("engine")
        val material = owned("material", "engine")
        val instance = owned("instance", "engine", "material")
        val mesh = owned("mesh", "engine")
        val renderable = owned("renderable", "engine", "mesh", "instance")

        listOf(engine, material, instance, mesh).forEach { it.release() }
        assertEquals(emptyList(), destroyed)

        renderable.release()
        assertEquals(listOf("renderable", "mesh", "instance", "material", "engine"), destroyed)
    }

    @Test
    fun parentsWeDoNotOwnAreNotTracked() {
        val instance = owned("instance", "callersMaterial", null)
        instance.release()
        assertEquals(listOf("instance"), destroyed)
    }

    @Test
    fun aNullValueOwnsNothingAndHoldsNoParent() {
        val engine = owned("engine")
        val failed = Owned<String?>(null, listOf("engine")) { destroyed += it }
        engine.release()
        assertEquals(listOf("engine"), destroyed, "a failed create doesn't keep its engine alive")
        failed.release()
        assertEquals(listOf("engine"), destroyed)
    }

    @Test
    fun aSharedValueStaysFindableWhileAnyOwnerLives() {
        // Two call sites leasing one per-engine context: the first leaving mustn't hide the second.
        val first = owned("context")
        val second = owned("context")
        first.release()
        val asset = owned("asset", "context")
        second.release()
        assertEquals(listOf("context"), destroyed, "the first lease goes; the second waits for the asset")
        asset.release()
        assertEquals(listOf("context", "asset", "context"), destroyed)
    }

    @Test
    fun aReleasedValueCanBeOwnedAgain() {
        owned("entity").release()
        val again = owned("entity")
        val child = owned("child", "entity")
        again.release()
        assertEquals(listOf("entity"), destroyed, "a recycled id is tracked afresh")
        child.release()
        assertEquals(listOf("entity", "child", "entity"), destroyed)
    }
}

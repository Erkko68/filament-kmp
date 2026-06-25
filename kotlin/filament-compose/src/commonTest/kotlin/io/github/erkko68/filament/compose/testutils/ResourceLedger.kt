package io.github.erkko68.filament.compose.testutils

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Scene
import kotlin.test.assertEquals
import kotlin.test.assertFalse

/**
 * Resource-accounting helpers for compose lifecycle/leak tests.
 *
 * Filament exposes no global "live object count", so leak detection is done per-[Scene] and
 * per-manager: a composable that adds entities must, on disposal, leave the scene empty and its
 * entities dead. These wrap the recurring before/after checks.
 */

/** Snapshot of a scene's membership counts at one instant. */
data class SceneCounts(val entities: Int, val renderables: Int, val lights: Int) {
    companion object {
        fun of(scene: Scene) = SceneCounts(scene.entityCount, scene.renderableCount, scene.lightCount)
    }
}

/** Asserts the scene holds nothing — the post-dispose expectation for every lifecycle test. */
fun assertSceneEmpty(scene: Scene, message: String = "scene should be empty after disposal") {
    assertEquals(0, scene.entityCount, "$message (entityCount)")
    assertEquals(0, scene.renderableCount, "$message (renderableCount)")
    assertEquals(0, scene.lightCount, "$message (lightCount)")
}

/** Asserts none of [entities] is still alive in the engine's entity manager (no leaked entities). */
fun assertEntitiesDestroyed(engine: Engine, entities: IntArray) {
    val em = engine.getEntityManager()
    for (e in entities) {
        assertFalse(em.isAlive(e), "entity $e should be destroyed after disposal")
    }
}

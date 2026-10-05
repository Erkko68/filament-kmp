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
    val em = engine.entityManager
    for (e in entities) {
        assertFalse(em.isAlive(e), "entity $e should be destroyed after disposal")
    }
}

/**
 * Asserts a freed Filament object is no longer valid, via its `Engine.isValid…` query in [isValid].
 *
 * After `destroy…`, the query returns `false` on JVM / native / Web — but on **Android** the SDK
 * clears the wrapper's native handle, so the query *throws* `IllegalStateException("Calling method on
 * destroyed …")` instead of returning `false`. Both outcomes mean "destroyed", so a throw is treated
 * as success here; only a `true` return (object still live) fails.
 */
fun assertDestroyed(message: String, isValid: () -> Boolean) {
    val stillValid = try {
        isValid()
    } catch (e: Throwable) {
        false // Android: querying a destroyed handle throws — that *is* the proof of destruction.
    }
    assertFalse(stillValid, message)
}

/**
 * The first destroyed Filament object [scene] still draws with (its IBL and skybox with their textures, its
 * renderables' material instances), or null. Rendering the scene in that state is a use-after-free.
 */
fun danglingInScene(engine: Engine, scene: Scene): String? {
    // Android: querying a destroyed wrapper throws instead of answering false.
    fun valid(isValid: () -> Boolean) = try { isValid() } catch (e: Throwable) { false }

    scene.indirectLight?.let { ibl ->
        if (!valid { engine.isValid(ibl) }) return "the scene's IndirectLight"
        ibl.reflectionsTexture?.let { if (!valid { engine.isValid(it) }) return "the IndirectLight's reflections texture" }
        ibl.irradianceTexture?.let { if (!valid { engine.isValid(it) }) return "the IndirectLight's irradiance texture" }
    }
    scene.skybox?.let { skybox ->
        if (!valid { engine.isValid(skybox) }) return "the scene's Skybox"
        skybox.texture?.let { if (!valid { engine.isValid(it) }) return "the Skybox's texture" }
    }
    val rm = engine.renderableManager
    var dangling: String? = null
    scene.forEach { entity ->
        if (dangling != null || !rm.hasComponent(entity)) return@forEach
        val renderable = rm.getInstance(entity)
        for (primitive in 0 until rm.getPrimitiveCount(renderable)) {
            val instance = rm.getMaterialInstanceAt(renderable, primitive) ?: continue
            val material = instance.material
            if (!valid { engine.isValid(material) } || !valid { engine.isValid(material, instance) }) {
                dangling = "the material instance of entity $entity, primitive $primitive"
            }
        }
    }
    return dangling
}

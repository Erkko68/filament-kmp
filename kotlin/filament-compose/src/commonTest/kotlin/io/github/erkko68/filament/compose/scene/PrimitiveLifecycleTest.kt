package io.github.erkko68.filament.compose.scene

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.ExperimentalTestApi
import io.github.erkko68.filament.MaterialInstance
import io.github.erkko68.filament.compose.FilamentSceneScope
import io.github.erkko68.filament.compose.scene.primitives.Cube
import io.github.erkko68.filament.compose.scene.primitives.Cylinder
import io.github.erkko68.filament.compose.scene.primitives.Mesh
import io.github.erkko68.filament.compose.scene.primitives.Plane
import io.github.erkko68.filament.compose.scene.primitives.Sphere
import io.github.erkko68.filament.compose.testutils.TierBSceneFixture
import io.github.erkko68.filament.compose.testutils.assertEntitiesDestroyed
import io.github.erkko68.filament.compose.testutils.assertSceneEmpty
import io.github.erkko68.filament.compose.testutils.composeScene
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Tier-B (real-backend) lifecycle/leak coverage for the primitive composables. Each builds GPU
 * geometry (vertex/index buffers + a renderable), which the NOOP driver panics on — so this gates on
 * a DEFAULT backend via [TierBSceneFixture] and skips where none is available. The assertions guard
 * the dispose ordering documented in MeshData.kt: a primitive enters the scene as exactly one
 * renderable with a live RenderableManager component, and leaving composition removes it, destroys
 * the entity, and leaves the scene empty (the vertex/index buffers are freed in `onDispose`).
 */
class PrimitiveLifecycleTest : TierBSceneFixture() {

    private fun primitives(
        material: MaterialInstance,
    ): List<Pair<String, @Composable FilamentSceneScope.((entity: Int) -> Unit) -> Unit>> = listOf(
        "Cube" to { onCreate -> Cube(material, onCreate = onCreate) },
        "Sphere" to { onCreate -> Sphere(material, onCreate = onCreate) },
        "Plane" to { onCreate -> Plane(material, onCreate = onCreate) },
        "Cylinder" to { onCreate -> Cylinder(material, onCreate = onCreate) },
        "Mesh" to { onCreate ->
            // A single CCW triangle — the custom-geometry escape hatch.
            Mesh(
                material = material,
                positions = floatArrayOf(0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f, 0f),
                normals = floatArrayOf(0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f),
                uvs = floatArrayOf(0f, 0f, 1f, 0f, 0f, 1f),
                indices = intArrayOf(0, 1, 2),
                onCreate = onCreate,
            )
        },
    )

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun eachPrimitiveEntersAndLeavesCleanly() {
        val engine = engine ?: return
        val scene = scene ?: return
        val material = materialInstance() ?: return

        for ((name, primitive) in primitives(material)) {
            var captured = 0
            composeScene(
                engine = engine,
                scene = scene,
                whileComposed = {
                    assertEquals(1, scene.renderableCount, "$name should add exactly one renderable while composed")
                    assertEquals(1, scene.entityCount, "$name should add exactly one entity while composed")
                    assertTrue(captured != 0, "$name should report its renderable entity via onCreate")
                    assertTrue(
                        engine.getRenderableManager().hasComponent(captured),
                        "$name should have a live renderable component while composed",
                    )
                },
                afterDispose = {
                    assertSceneEmpty(scene, "$name leaked after disposal")
                    assertEntitiesDestroyed(engine, intArrayOf(captured))
                    assertTrue(
                        !engine.getRenderableManager().hasComponent(captured),
                        "$name renderable component should be gone after disposal",
                    )
                },
            ) {
                primitive { captured = it }
            }
        }
    }
}

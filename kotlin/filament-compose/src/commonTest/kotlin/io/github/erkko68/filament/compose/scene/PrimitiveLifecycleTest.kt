package io.github.erkko68.filament.compose.scene

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import io.github.erkko68.filament.Box
import io.github.erkko68.filament.MaterialInstance
import io.github.erkko68.filament.compose.EntityScope
import io.github.erkko68.filament.compose.FilamentSceneScope
import io.github.erkko68.filament.compose.scene.primitives.Cube
import io.github.erkko68.filament.compose.scene.primitives.Cylinder
import io.github.erkko68.filament.compose.scene.primitives.Mesh
import io.github.erkko68.filament.compose.scene.primitives.Plane
import io.github.erkko68.filament.compose.scene.primitives.Sphere
import io.github.erkko68.filament.compose.testutils.TestGlb
import io.github.erkko68.filament.compose.testutils.TierBSceneFixture
import io.github.erkko68.filament.compose.testutils.assertEntitiesDestroyed
import io.github.erkko68.filament.compose.testutils.assertSceneEmpty
import io.github.erkko68.filament.compose.testutils.skippedComposeTest
import io.github.erkko68.filament.compose.testutils.withFilamentScene
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
    ): List<Pair<String, @Composable FilamentSceneScope.(EntityScope.() -> Unit) -> Unit>> = listOf(
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

    // One withFilamentScene hosts the whole loop (mirroring LightLifecycleTest): each primitive is
    // mounted then disposed inside the single harness body, and the test *returns* the harness
    // result so the async web `runComposeUiTest` is awaited — see skippedComposeTest's KDoc.
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun eachPrimitiveEntersAndLeavesCleanly() = run {
        val engine = engine ?: return@run skippedComposeTest()
        val scene = scene ?: return@run skippedComposeTest()
        val material = materialInstance() ?: return@run skippedComposeTest()

        withFilamentScene(engine, scene) { setContent ->
            for ((name, primitive) in primitives(material)) {
                var captured = 0
                setContent { primitive { captured = entity } }
                waitForIdle()
                assertEquals(1, scene.renderableCount, "$name should add exactly one renderable while composed")
                assertEquals(1, scene.entityCount, "$name should add exactly one entity while composed")
                assertTrue(captured != 0, "$name should report its renderable entity via onCreate")
                assertTrue(
                    engine.renderableManager.hasComponent(captured),
                    "$name should have a live renderable component while composed",
                )

                setContent {}
                waitForIdle()
                assertSceneEmpty(scene, "$name leaked after disposal")
                assertEntitiesDestroyed(engine, intArrayOf(captured))
                assertTrue(
                    !engine.renderableManager.hasComponent(captured),
                    "$name renderable component should be gone after disposal",
                )
            }
        }
    }

    /** Shadow flags have setters: changing them keeps the entity (and doesn't run `onCreate` again). */
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun shadowFlagsChangeInPlace() = run {
        val engine = engine ?: return@run skippedComposeTest()
        val scene = scene ?: return@run skippedComposeTest()
        val material = materialInstance() ?: return@run skippedComposeTest()

        withFilamentScene(engine, scene) { setContent ->
            var shadows by mutableStateOf(true)
            var created = 0
            var entity = 0
            setContent {
                Cube(material, castShadows = shadows, receiveShadows = shadows, onCreate = { created++; entity = this.entity })
            }
            waitForIdle()
            val rm = engine.renderableManager
            assertTrue(rm.isShadowCaster(rm.getInstance(entity)) && rm.isShadowReceiver(rm.getInstance(entity)))

            shadows = false
            mainClock.advanceTimeByFrame()
            waitForIdle()
            assertEquals(1, created, "a shadow flag should not rebuild the entity")
            assertTrue(!rm.isShadowCaster(rm.getInstance(entity)) && !rm.isShadowReceiver(rm.getInstance(entity)))

            setContent {}
            waitForIdle()
            assertSceneEmpty(scene)
        }
    }

    /** A hidden Group takes its whole subtree out of the scene without destroying anything, at any depth. */
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun groupVisibilityHidesTheSubtree() = run {
        val engine = engine ?: return@run skippedComposeTest()
        val scene = scene ?: return@run skippedComposeTest()
        val material = materialInstance() ?: return@run skippedComposeTest()
        val asset = gltfAsset(TestGlb.getAnimatedMorphCubeGlbBytes()) ?: return@run skippedComposeTest()

        withFilamentScene(engine, scene) { setContent ->
            var outer by mutableStateOf(true)
            var inner by mutableStateOf(true)
            var own by mutableStateOf(true)
            var created = 0
            val entities = mutableListOf<Int>()
            setContent {
                Group(visible = outer) {
                    Cube(material, onCreate = { created++; entities += entity })
                    Group(visible = inner) {
                        Sphere(material, visible = own, onCreate = { created++; entities += entity })
                        GltfInstance(asset = asset, onCreate = { created++ })
                    }
                }
            }
            waitForIdle()
            fun settle() { mainClock.advanceTimeByFrame(); waitForIdle() }
            val all = scene.renderableCount
            val model = all - 2
            assertTrue(model > 0, "the glTF instance should draw something")

            own = false
            settle()
            assertEquals(all - 1, scene.renderableCount, "a primitive's own visible flag hides just it")
            own = true
            inner = false
            settle()
            assertEquals(1, scene.renderableCount, "the inner Group hides the sphere and the model, not the cube")
            outer = false
            settle()
            assertSceneEmpty(scene, "a hidden outer Group should leave nothing in the scene")
            inner = true
            settle()
            assertSceneEmpty(scene, "a visible Group inside a hidden one stays hidden")
            outer = true
            settle()
            assertEquals(all, scene.renderableCount)

            assertEquals(3, created, "hiding and showing must not rebuild anything")
            val em = engine.entityManager
            assertTrue(entities.all(em::isAlive))

            setContent {}
            waitForIdle()
            assertSceneEmpty(scene)
            assertEntitiesDestroyed(engine, entities.toIntArray())
        }
    }

    /** A primitive holds on to the material instance it was built with, so another one means another entity. */
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun changingTheMaterialRebuildsTheEntity() = run {
        val engine = engine ?: return@run skippedComposeTest()
        val scene = scene ?: return@run skippedComposeTest()
        val first = materialInstance() ?: return@run skippedComposeTest()
        val second = materialInstance() ?: return@run skippedComposeTest()

        withFilamentScene(engine, scene) { setContent ->
            var material: MaterialInstance? by mutableStateOf(first)
            val built = mutableListOf<Int>()
            setContent { Cube(material, onCreate = { built += entity }) }
            waitForIdle()
            val rm = engine.renderableManager
            // Wrappers are made per call: compare the native objects.
            fun drawnWith() = rm.getMaterialInstanceAt(rm.getInstance(built.last()), 0)?.nativeObject
            assertEquals(first.nativeObject, drawnWith())

            material = second
            mainClock.advanceTimeByFrame()
            waitForIdle()
            assertEquals(2, built.size)
            assertEquals(second.nativeObject, drawnWith())
            assertEquals(1, scene.renderableCount, "the old entity should have left the scene")
            assertEntitiesDestroyed(engine, intArrayOf(built.first()))

            // No material: nothing to draw, and nothing left behind.
            material = null
            mainClock.advanceTimeByFrame()
            waitForIdle()
            assertSceneEmpty(scene)
            assertEntitiesDestroyed(engine, built.toIntArray())
        }
    }

    /** Culling bounds: a one-sided plane is a thin slab, and a Mesh without bounds gets them from its positions. */
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun boundingBoxesEncloseTheGeometry() = run {
        val engine = engine ?: return@run skippedComposeTest()
        val scene = scene ?: return@run skippedComposeTest()
        val material = materialInstance() ?: return@run skippedComposeTest()
        val positions = floatArrayOf(-1f, 0f, 2f, 3f, 4f, 2f, 1f, -2f, 6f)
        val normals = floatArrayOf(0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f)
        val uvs = floatArrayOf(0f, 0f, 1f, 0f, 0f, 1f)

        withFilamentScene(engine, scene) { setContent ->
            var plane = 0
            var computed = 0
            var given = 0
            setContent {
                Plane(material, width = 2f, depth = 4f, doubleSided = false, onCreate = { plane = entity })
                Mesh(material, positions, normals, uvs, intArrayOf(0, 1, 2), onCreate = { computed = entity })
                Mesh(
                    material, positions, normals, uvs, intArrayOf(0, 1, 2),
                    boundingBox = Box(floatArrayOf(0f, 0f, 0f), floatArrayOf(9f, 9f, 9f)), onCreate = { given = entity },
                )
            }
            waitForIdle()
            val rm = engine.renderableManager
            fun assertBounds(center: List<Float>, halfExtent: List<Float>, entity: Int) {
                val box = rm.getAxisAlignedBoundingBox(rm.getInstance(entity))
                for (i in 0 until 3) {
                    assertEquals(center[i], box.center[i], 1e-6f, "center of ${box.center.toList()}")
                    assertEquals(halfExtent[i], box.halfExtent[i], 1e-6f, "half extent of ${box.halfExtent.toList()}")
                }
            }
            assertBounds(listOf(0f, 0f, 0f), listOf(1f, 0.001f, 2f), plane)
            assertBounds(listOf(1f, 1f, 4f), listOf(2f, 3f, 2f), computed)
            assertBounds(listOf(0f, 0f, 0f), listOf(9f, 9f, 9f), given)

            setContent {}
            waitForIdle()
            assertSceneEmpty(scene)
        }
    }
}

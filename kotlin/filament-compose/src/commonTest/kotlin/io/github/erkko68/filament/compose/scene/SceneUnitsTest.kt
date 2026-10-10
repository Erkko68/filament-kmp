package io.github.erkko68.filament.compose.scene

import io.github.erkko68.filament.Box
import io.github.erkko68.filament.compose.scene.primitives.Cube
import io.github.erkko68.filament.compose.scene.primitives.Cylinder
import io.github.erkko68.filament.compose.scene.primitives.Mesh
import io.github.erkko68.filament.compose.scene.primitives.MeshData
import io.github.erkko68.filament.compose.scene.primitives.Sphere
import io.github.erkko68.filament.compose.testutils.ComposeTestFixture
import io.github.erkko68.filament.compose.testutils.assertSceneEmpty
import io.github.erkko68.filament.compose.testutils.composeScene
import io.github.erkko68.filament.compose.testutils.compositionFailure
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** The CPU-side rules of the scene composables: input validation, content equality, and the shadow and light maths. */
class SceneUnitsTest : ComposeTestFixture() {
    private val triangle = floatArrayOf(0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f, 0f)
    private val facingZ = floatArrayOf(0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f)
    private val uvs = floatArrayOf(0f, 0f, 1f, 0f, 0f, 1f)

    // Values that went through a native float: on web the Kotlin side is a double.
    private fun assertNear(expected: FloatArray, actual: FloatArray) {
        assertEquals(expected.size, actual.size)
        for (i in expected.indices) assertEquals(expected[i], actual[i], 1e-6f, "[$i] of ${actual.toList()}")
    }

    /** Bad geometry is refused in composition, where the message can name the argument, not by a native abort. */
    @Test
    fun malformedGeometryIsRefused() {
        fun mesh(positions: FloatArray = triangle, normals: FloatArray = facingZ, uv: FloatArray = uvs, indices: IntArray = intArrayOf(0, 1, 2)) =
            compositionFailure(engine, scene) { Mesh(material = null, positions = positions, normals = normals, uvs = uv, indices = indices) }
        val refused = listOf(
            "positions" to mesh(positions = FloatArray(0)),
            "positions" to mesh(positions = FloatArray(4)),
            "normals" to mesh(normals = FloatArray(6)),
            "uvs" to mesh(uv = FloatArray(4)),
            "indices" to mesh(indices = IntArray(0)),
            "indices" to mesh(indices = intArrayOf(0, 1)),
            "rings" to compositionFailure(engine, scene) { Sphere(material = null, rings = 1) },
            "segments" to compositionFailure(engine, scene) { Sphere(material = null, segments = 2) },
            "segments" to compositionFailure(engine, scene) { Cylinder(material = null, segments = 2) },
        )
        for ((argument, failure) in refused) {
            assertIs<IllegalArgumentException>(failure, "bad $argument should be refused")
            assertTrue(failure.message.orEmpty().startsWith(argument), "should name $argument, said: ${failure.message}")
        }
        assertNull(mesh(), "the well-formed triangle composes")
    }

    /** A material that isn't ready yet (null) draws nothing, and starts drawing nothing without failing. */
    @Test
    fun primitivesWithoutAMaterialAddNothing() = composeScene(
        engine, scene,
        whileComposed = { assertSceneEmpty(scene, "nothing to draw without a material") },
        afterDispose = { assertSceneEmpty(scene) },
    ) {
        Cube(material = null)
        Sphere(material = null)
        Mesh(material = null, positions = triangle, normals = facingZ, uvs = uvs, indices = intArrayOf(0, 1, 2))
    }

    /** Equal by content, so arrays rebuilt every recomposition don't re-upload the buffers. */
    @Test
    fun meshDataComparesByContent() {
        fun data(
            positions: FloatArray = triangle.copyOf(), normals: FloatArray = facingZ.copyOf(), uv: FloatArray = uvs.copyOf(),
            indices: IntArray = intArrayOf(0, 1, 2), box: Box = Box(floatArrayOf(0f, 0f, 0f), floatArrayOf(1f, 1f, 1f)),
        ) = MeshData(positions, normals, uv, indices, box)
        val mesh = data()
        assertTrue(mesh.equals(mesh))
        assertEquals(data(), mesh)
        assertEquals(data().hashCode(), mesh.hashCode())
        val different = listOf(
            data(positions = FloatArray(9)), data(normals = FloatArray(9)), data(uv = FloatArray(6)), data(indices = intArrayOf(2, 1, 0)),
            data(box = Box(floatArrayOf(1f, 0f, 0f), floatArrayOf(1f, 1f, 1f))), data(box = Box(floatArrayOf(0f, 0f, 0f), floatArrayOf(2f, 1f, 1f))),
        )
        for (other in different) assertNotEquals(other, mesh)
        assertFalse(mesh.equals("not a mesh"))
    }

    @Test
    fun shadowCascades() {
        assertEquals(1, ShadowConfig().toShadowOptions().shadowCascades)
        // Unset splits are spread evenly; set ones are taken as given.
        val uniform = ShadowConfig(cascades = 3).toShadowOptions()
        assertEquals(3, uniform.shadowCascades)
        assertNear(floatArrayOf(1f / 3f, 2f / 3f), uniform.cascadeSplitPositions.copyOf(2))
        val explicit = ShadowConfig(cascades = 3, cascadeSplits = listOf(0.1f, 0.4f)).toShadowOptions()
        assertNear(floatArrayOf(0.1f, 0.4f), explicit.cascadeSplitPositions.copyOf(2))
        // Filament supports 1 to 4.
        assertEquals(listOf(1, 4), listOf(ShadowConfig(cascades = 0), ShadowConfig(cascades = 9)).map { it.toShadowOptions().shadowCascades })

        val rotated = ShadowConfig(transform = Rotation(0f, 0.6f, 0f, 0.8f)).toShadowOptions()
        assertNear(floatArrayOf(0f, 0.6f, 0f, 0.8f), rotated.transform)
    }

    /** The local direction that a parent transform turns back into the wanted world direction. */
    @Test
    fun pinnedLocalDirection() {
        val down = Direction(0f, -1f, 0f)
        val identity = floatArrayOf(1f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f)
        // Column-major: +X maps to +Y and +Y to -X (a quarter turn about Z), then a translation that must not matter.
        val quarterTurn = floatArrayOf(0f, 1f, 0f, 0f, -1f, 0f, 0f, 0f, 0f, 0f, 1f, 0f, 5f, 6f, 7f, 1f)
        val scaled = FloatArray(16) { identity[it] * 3f }
        val mirrored = identity.copyOf().also { it[5] = -1f }
        val cases = listOf(
            identity to down,
            quarterTurn to Direction(-1f, 0f, 0f), // local -X is turned to world -Y
            scaled to down,                         // stays a unit vector
            mirrored to down,                       // Filament's cofactor transform ignores the mirror's sign
        )
        for ((world, expected) in cases) {
            val local = assertNotNull(pinnedLocalDirection(world, down))
            // Component-wise: the maths yields -0.0 where 0.0 is expected.
            assertContentEquals(
                floatArrayOf(expected.x, expected.y, expected.z), floatArrayOf(local.x + 0f, local.y + 0f, local.z + 0f),
                "under ${world.toList()}",
            )
        }
        // A collapsed or non-finite parent has no answer; the light keeps its last direction.
        for (degenerate in listOf(FloatArray(16), FloatArray(16) { Float.NaN }, FloatArray(16) { Float.POSITIVE_INFINITY })) {
            assertNull(pinnedLocalDirection(degenerate, down))
        }
    }
}
